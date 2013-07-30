package com.tjxjh.action;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cn.cafebabe.autodao.pojo.Page;

import com.tjxjh.enumeration.ClubMemberRole;
import com.tjxjh.enumeration.UserStatus;
import com.tjxjh.po.Announcement;
import com.tjxjh.service.AnnouncementService;

@ParentPackage("struts-default")
@Namespace("/")
public class AnnouncementAction extends BaseAction
{
	static final String CLUB_ANNOUNCEMENTS = "clubAnnouncements";
	static final String MY_ANNOUNCEMENTS = "myAnnouncements";
	static final String ADD_ANNOUNCEMENT = "addAnnouncement";
	static final String ADD_ANNOUNCEMENT_INPUT = "addAnnouncementInput";
	private static final long serialVersionUID = -8109609053983009227L;
	@Resource
	private AnnouncementService announcementService = null;
	private Announcement announcement = null;
	private String path = null;
	private Page page = null;
	
	@Actions({@Action(value = ADD_ANNOUNCEMENT_INPUT, results = {@Result(name = SUCCESS, location = MANAGE
			+ ADD_ANNOUNCEMENT + JSP, params = {"path", "${path}"})})})
	public String page()
	{
		return SUCCESS;
	}
	
	@Action(value = ADD_ANNOUNCEMENT, results = {
			@Result(name = SUCCESS, type = REDIRECT_ACTION, location = "${path}"),
			@Result(name = INPUT, type = REDIRECT_ACTION, location = ADD_ANNOUNCEMENT),
			@Result(name = ERROR, location = FOREPART + ERROR_PAGE)})
	public String addAnnouncement()
	{
		if(path == null)
		{
			return ERROR;
		}
		if(currentUser().getStatus() != UserStatus.ADMIN)
		{
			if((super.currentClubMember() == null || super.currentClubMember()
					.getRole() == ClubMemberRole.NORMAL))
			{
				return ERROR;
			}
			else
			{
				announcement.setClub(super.currentClubMember().getClub());
			}
		}
		return super.successOrInput(announcementService.save(announcement));
	}
	
	@Action(value = MY_ANNOUNCEMENTS, results = {@Result(name = SUCCESS, location = FOREPART
			+ MY_ANNOUNCEMENTS + JSP)})
	public String myAnnouncements()
	{
		if(page == null)
		{
			page = announcementService.announcementsPage(super.currentUser(),
					true);
		}
		super.getRequestMap().put(
				MY_ANNOUNCEMENTS,
				announcementService.announcements(super.currentUser(), page,
						true));
		return SUCCESS;
	}
	
	@Action(value = CLUB_ANNOUNCEMENTS, results = {@Result(name = SUCCESS, location = FOREPART
			+ CLUB_ANNOUNCEMENTS + JSP)})
	public String clubAnnouncements()
	{
		if(page == null)
		{
			page = announcementService.announcementsPage(super.currentUser(),
					false);
		}
		super.getRequestMap().put(
				MY_ANNOUNCEMENTS,
				announcementService.announcements(super.currentUser(), page,
						false));
		return SUCCESS;
	}
	
	public void setAnnouncementService(AnnouncementService announcementService)
	{
		this.announcementService = announcementService;
	}
	
	public Announcement getAnnouncement()
	{
		return announcement;
	}
	
	public void setAnnouncement(Announcement announcement)
	{
		this.announcement = announcement;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public Page getPage()
	{
		return page;
	}
	
	public void setPage(Page page)
	{
		this.page = page;
	}
}
