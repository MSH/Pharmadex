package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.UserAccess;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * @author Utkarsh 
 *
 *	Maintains a list of users logged into the system
 */
@Component
@Scope("singleton")
public class OnlineUserBean implements Serializable{

    private static final long serialVersionUID = -2211513383305898044L;
    private List<OnlineUser> items;
	private List<OnlineUser> onlineUserlist = new ArrayList<OnlineUser>();

	/**
	 * Return the user login currently on-line
	 * @return
	 */
	public UserAccess getUserAccess() {
//		return (UserAccess)Component.getInstance("userAccess", false);
        return null;
	}

	/**
	 * Add the online user to the list of users
	 * @param loginUser
	 * @return
	 */
	public OnlineUser add(UserAccess loginUser) {
		OnlineUser item = new OnlineUser();
		item.setUserAccess(loginUser);
		
		onlineUserlist.add(item);
		
		return item;
	}

	/**
	 * Remove a user from the list of logged in users
	 * @param loginUser
	 */
	public void remove(UserAccess loginUser) {
		OnlineUser item = getOnlineUser(loginUser);
		if (item != null)
			onlineUserlist.remove(item);
	}

	/**
	 * Returns the list of users online in the system.
	 * @param loginUser
	 * @return
	 */
	public OnlineUser getOnlineUser(UserAccess loginUser) {
		OnlineUser aux = null;
		
		for (OnlineUser item: onlineUserlist) {
            if (item.getUserAccess().getId() == loginUser.getId()) {
                aux = item;
                break;
            }
        }

		return aux;
	}
	
	/**
	 * Updates the list of user with the page date and time of last access.
     */
	public void update() {
		UserAccess userLogin = getUserAccess();
		if (userLogin == null)
			return;
		
		OnlineUser item = getOnlineUser(userLogin);
		if (item == null)
			return;
		
		// Update the current data of access
		item.setLastAccess(new Date());
		
		String page = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
		item.setLastPage(page);
	}
	
	/**
	 * Refresh the report data
	 */
	public void refresh() {
		onlineUserlist = null;
	}	
	
	/**
	 * Return onlineUsersHome component containing list of on-line users
	 * @return
	 */
	public OnlineUserBean getOnlineUsersHome() {
//		return (OnlineUserBean)Component.getInstance("applicationBean", true);
        return null;
	}

	
	
	public List<OnlineUser> getOnlineUsersList() {
		return onlineUserlist;
	}

	/**
	 * Create the report of on-line users by workspace
	 */
	protected void createOnlineUserList() {
		items = new ArrayList<OnlineUser>();
		List<OnlineUser> lst = getOnlineUsersList();
		for (OnlineUser item: lst) {
				items.add(item);
		}
	}
	
	/**
	 * Return list of on-line users
	 * @return
	 */
	public List<OnlineUser> getItems() {
		if (items == null)
			createOnlineUserList();
		return items;
	}	
	
	public int getCount() {
		return onlineUserlist.size();
	}
}
