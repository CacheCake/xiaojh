package com.tjxjh.util;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.tjxjh.action.BaseAction;
import com.tjxjh.po.Club;
import com.tjxjh.po.ClubMember;
import com.tjxjh.po.Merchant;
import com.tjxjh.po.User;

public class Auth {
	public static User getUserFromSession (){
		ActionContext context = ActionContext.getContext();  
	    Map<String, Object> session = context.getSession();
	    User user=new User();
	    user=(User) session.get("user");
	    return user;
	}
	public static ClubMember getClubMemberFromSession (){
		ActionContext context = ActionContext.getContext();  
	    Map<String, Object> session = context.getSession();
	    ClubMember cm=(ClubMember) session.get(BaseAction.CLUB_MEMBER);
	    return cm;
	}
	public static Club getCluFromSession (){
		ActionContext context = ActionContext.getContext();  
	    Map<String, Object> session = context.getSession();
	    ClubMember cm=(ClubMember) session.get(BaseAction.CLUB_MEMBER);
	    if(cm!=null){
	    	return cm.getClub();
	    }
	    return null;
	}
	
	public static Merchant getMerchantFromSession (){
		ActionContext context = ActionContext.getContext();  
	    Map<String, Object> session = context.getSession();
	    Merchant merchant=new Merchant();
	    merchant=(Merchant) session.get("merchant");
	    return merchant;
	}

}
