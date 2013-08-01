package com.tjxjh.action;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cn.cafebabe.autodao.pojo.Page;

import com.tjxjh.enumeration.ClubMemberRole;
import com.tjxjh.enumeration.ClubMemberSource;
import com.tjxjh.po.Activity;
import com.tjxjh.po.Club;
import com.tjxjh.po.ClubMember;
import com.tjxjh.po.ClubPost;
import com.tjxjh.po.Merchant;
import com.tjxjh.po.User;
import com.tjxjh.pojo.ClubPostList;
import com.tjxjh.service.ActivityService;
import com.tjxjh.service.ClubPostService;
import com.tjxjh.service.ClubService;
import com.tjxjh.service.SearchService;

@ParentPackage("struts-default")
@Namespace("/")
public class ClubAction extends BaseAction
{
	static final String CHANGE_PROPRIETER_INPUT = "changeProprieterInput";
	static final String CHANGE_PROPRIETER = "changeProprieter";
	static final String UPDATE_MEMBER_TO_NORMAL = "updateMemberToNormal";
	static final String UPDATE_MEMBER_TO_MANAGER = "updateMemberToManager";
	static final String CLUB_REQUEST = "clubRequest";
	static final String FIRE_CLUB_MEMBER = "fireClubMember";
	static final String CLUB_ACCEPT_INVITED = "clubAcceptInvited";
	static final String CLUB_REFUSE_INVITED = "clubRefuseInvited";
	static final String CLUB_MEMBERS = "clubMembers";
	static final String USER_ADD_CLUB = "userAddClub";
	static final String USER_REFUSE_INVITED = "userRefuseInvited";
	static final String USER_ACCEPT_INVITED = "userAcceptInvited";
	static final String CLUB_ADD_USER = "clubAddUser";
	static final String SEARCH_USER_TO_MANAGE = "searchUserToManage";
	static final String CLUB_MAIN = "clubMain";
	static final String MY_CLUBS = "myClubs";
	static final String CHECK_CLUB = "checkClub";
	static final String APPLY_CLUB = "applyClub";
	static final String APPLY_CLUB_INPUT = "applyClubInput";
	private static final long serialVersionUID = 1067987058025414445L;
	private Club club = null;
	protected File logo = null;
	protected String logoFileName = null;
	@Resource
	private ClubService clubService = null;
	@Resource
	private SearchService searchService = null;
	@Resource
	private ClubPostService clubPostService = null;
	private ActivityService activityService = null;
	private Page page = null;
	private String text = null;
	private User user = null;
	private int type;
	private List<Activity> acs = null;
	@Actions({@Action(value = APPLY_CLUB_INPUT, results = {@Result(name = SUCCESS, location = FOREPART
			+ APPLY_CLUB + JSP)})})
	public String page()
	{
		return SUCCESS;
	}
	
	@Action(value = APPLY_CLUB, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CHECK_CLUB, params = {
			"club.id", "${club.id}"})})
	public String applyClub()
	{
		club.setLogoPath(new StringBuilder("upload/clubLogo/school_")
				.append(club.getSchool().getId()).append("_")
				.append(club.getName())
				.append(logoFileName.substring(logoFileName.indexOf('.')))
				.toString());
		clubService.applyClub(club, super.currentUser(), logo);
		return SUCCESS;
	}
	
	@Action(value = CHECK_CLUB, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = MY_CLUBS)})
	public String checkClub()
	{
		clubService.checkClub(club, true);
		return SUCCESS;
	}
	
	@Action(value = MY_CLUBS, results = {@Result(name = SUCCESS, location = FOREPART
			+ MY_CLUBS + JSP)})
	public String myClubs()
	{
		if(page == null)
		{
			page = clubService.userClubsPage(currentUser());
		}
		super.getRequestMap().put(MY_CLUBS,
				clubService.userClubs(currentUser(), page));
		return SUCCESS;
	}
	
	@Action(value = CLUB_MAIN, results = {
			@Result(name = SUCCESS, location = FOREPART + CLUB_MAIN + JSP),
			@Result(name = INPUT, type = REDIRECT_ACTION, location = UserAction.MAIN)})
	public String clubMain()
	{
		if(isClubEmpty())
		{
			return INPUT;
		}
		super.saveClubMember(clubService.userClub(super.currentUser(), club));
		Club c = clubService.clubById(club);
		/**********************fineTu***********************/
		c.getClubsForTargetClubId();
		List<Club> focusClubList = clubService.getFocusList(Club.class,c);
		if(focusClubList.size() > 9)
		{
			focusClubList = focusClubList.subList(0, 9);
		}
		getRequestMap().put("focusClubList", focusClubList);
		
		List<Club> focusMerchantList = clubService.getFocusList(Merchant.class,c);
		if(focusMerchantList.size() > 9)
		{
			focusMerchantList = focusMerchantList.subList(0, 9);
		}
		getRequestMap().put("focusMerchantList", focusMerchantList);
		
		Page page = new Page(1*7+1);
		page.setCurrentPage(1);
		ClubPostList clubPostList = new ClubPostList();
		List<ClubPost> list = clubPostService.clubPostList(c.getId(), page);
		for(ClubPost p:list){
			if(p.getTittle().length()>15){
				p.setTittle(p.getTittle().substring(0, 15));
			}
		}
		clubPostList.setClubPostList(list);
		getRequestMap().put("clubPostList", clubPostList);
		page=activityService.adminGetOneClubPageByHql(6,1,0,club,null);
		acs=activityService.adminFindOneClubActivityByHql(page,club,null);
		getRequestMap().put("acs", acs);
		/****************************************************/
		super.getRequestMap().put("club",c);
		if(super.currentClubMember() != null
				&& super.currentClubMember().getRole() != ClubMemberRole.NORMAL)
		{
			super.getRequestMap().put(
					"userRequestCount",
					clubService.userRequestCount(super.currentClubMember()
							.getClub()));
		}
		return SUCCESS;
	}

	private boolean isClubEmpty()
	{
		return club == null || club.getId() == null;
	}
	
	@Action(value = CLUB_REQUEST, results = {@Result(name = SUCCESS, location = FOREPART
			+ CLUB_REQUEST + JSP)})
	public String myInvited()
	{
		super.getRequestMap().put(CLUB_REQUEST,
				clubService.clubRequest(super.currentClubMember().getClub()));
		return SUCCESS;
	}
	
	@Action(value = CLUB_MEMBERS, results = {@Result(name = SUCCESS, location = FOREPART
			+ CLUB_MEMBERS + JSP)})
	public String clubMembers()
	{
		if(page == null)
		{
			page = clubService.clubMembersPage(super.currentClubMember()
					.getClub());
		}
		super.getRequestMap().put(
				CLUB_MEMBERS,
				clubService.clubMembers(super.currentClubMember().getClub(),
						page));
		return SUCCESS;
	}
	
	@Action(value = SEARCH_USER_TO_MANAGE, results = {@Result(name = SUCCESS, location = FOREPART
			+ SEARCH_USER_TO_MANAGE + JSP)})
	public String searchUserToManage()
	{
		if(page == null)
		{
			page = new Page(Page.getDefaultPageNumber());
		}
		super.getRequestMap()
				.put("users", searchService.searchUser(text, page));
		super.getRequestMap()
				.put(CLUB_MEMBERS,
						clubService.clubMembersMap(super.currentClubMember()
								.getClub()));
		return SUCCESS;
	}
	
	@Action(value = CLUB_ADD_USER, results = {
			@Result(name = SUCCESS, type = REDIRECT_ACTION, location = SEARCH_USER_TO_MANAGE, params = {
					"text", "${text}"}),
			@Result(name = INPUT, type = REDIRECT_ACTION, location = SEARCH_USER_TO_MANAGE)})
	public String clubAddUser()
	{
		if(user == null || user.getId() == null)
		{
			return INPUT;
		}
		clubService.addMember(super.currentClubMember().getClub(), user,
				ClubMemberSource.CLUB_TO_USER);
		return SUCCESS;
	}
	
	@Action(value = USER_ADD_CLUB, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MAIN, params = {
			"club.id", "${club.id}"})})
	public String userAddClub()
	{
		if(isClubEmpty())
		{
			return INPUT;
		}
		clubService.addMember(club, super.currentUser(),
				ClubMemberSource.USER_TO_CLUB);
		return SUCCESS;
	}
	
	@Action(value = USER_ACCEPT_INVITED, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MAIN, params = {
			"club.id", "${club.id}"})})
	public String userAcceptInvited()
	{
		clubService.acceptInvited(super.currentUser(), club);
		return SUCCESS;
	}
	
	@Action(value = USER_REFUSE_INVITED, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = UserAction.MY_INVITED)})
	public String userRefuseInvited()
	{
		clubService.refuseInvited(super.currentUser(), club);
		return SUCCESS;
	}
	
	@Action(value = CLUB_ACCEPT_INVITED, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String clubAcceptInvited()
	{
		clubService.acceptInvited(user, super.currentClubMember().getClub());
		return SUCCESS;
	}
	
	@Action(value = CLUB_REFUSE_INVITED, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String clubRefuseInvited()
	{
		clubService.refuseInvited(user, super.currentClubMember().getClub());
		return SUCCESS;
	}
	
	@Action(value = FIRE_CLUB_MEMBER, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String fireClubMember()
	{
		clubService.deleteClubMember(user, super.currentClubMember().getClub(),
				super.currentClubMember());
		return SUCCESS;
	}
	
	@Action(value = "clubFocus", results = {@Result(name = SUCCESS, location = BaseAction.FOREPART
			+ "clubFocus.jsp")})
	public String myFocus()
	{
		// List<User> focusList = sessionUser.getUsersForTargetUserId();
		ClubMember clubMember = (ClubMember) getSessionMap().get("clubMember");
		Club club = new Club();
		club.setId(clubMember.getId().getClubId());
		switch(type)
		{
			case (1):
				List<Club> clubList = clubService.getFocusList(Club.class,club);
				getRequestMap().put("focusList", clubList);
				break;
			case (2):
				List<Merchant> merchantList = clubService.getFocusList(Merchant.class, club);
				getRequestMap().put("focusList", merchantList);
				break;
		}
		return SUCCESS;
	}
	
	@Action(value = UPDATE_MEMBER_TO_MANAGER, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String updateMemberToManager()
	{
		clubService.changeMemberRole(user, super.currentClubMember(),
				ClubMemberRole.NORMAL, ClubMemberRole.MANAGER);
		return SUCCESS;
	}
	
	@Action(value = UPDATE_MEMBER_TO_NORMAL, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String updateMemberToNormal()
	{
		clubService.changeMemberRole(user, super.currentClubMember(),
				ClubMemberRole.MANAGER, ClubMemberRole.NORMAL);
		return SUCCESS;
	}
	
	@Action(value = CHANGE_PROPRIETER_INPUT, results = {@Result(name = SUCCESS, location = FOREPART
			+ CHANGE_PROPRIETER + JSP)})
	public String changeProprieterInput()
	{
		if(page == null)
		{
			page = clubService.clubMembersWithoutProprieterpage(super
					.currentClubMember());
		}
		super.getRequestMap().put(
				CHANGE_PROPRIETER,
				clubService.clubMembersWithoutProprieter(
						super.currentClubMember(), page));
		return SUCCESS;
	}
	
	@Action(value = CHANGE_PROPRIETER, results = {@Result(name = SUCCESS, type = REDIRECT_ACTION, location = CLUB_MEMBERS)})
	public String changeProprieter()
	{
		clubService.changeProprieter(user, super.currentClubMember());
		return SUCCESS;
	}
	
	public Club getClub()
	{
		return club;
	}
	
	public void setClub(Club club)
	{
		this.club = club;
	}
	
	public void setClubService(ClubService clubService)
	{
		this.clubService = clubService;
	}
	
	public File getLogo()
	{
		return logo;
	}
	
	public void setLogo(File logo)
	{
		this.logo = logo;
	}
	
	public String getLogoFileName()
	{
		return logoFileName;
	}
	
	public void setLogoFileName(String logoFileName)
	{
		this.logoFileName = logoFileName;
	}
	
	public Page getPage()
	{
		return page;
	}
	
	public void setPage(Page page)
	{
		this.page = page;
	}
	
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public List<Activity> getAcs() {
		return acs;
	}

	public void setAcs(List<Activity> acs) {
		this.acs = acs;
	}

	public void setClubPostService(ClubPostService clubPostService) {
		this.clubPostService = clubPostService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}
	
}
