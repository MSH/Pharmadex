package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.UserAccess;

import java.util.Date;


public class OnlineUser {
	private UserAccess userAccess;
	private String lastPage;
	private Date lastAccess;
	
	public UserAccess getUserAccess() {
		return userAccess;
	}
	public void setUserAccess(UserAccess userAccess) {
		this.userAccess = userAccess;
	}
	public String getLastPage() {
		return lastPage;
	}
	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}
	public Date getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	
}
