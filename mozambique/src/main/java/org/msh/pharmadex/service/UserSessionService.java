package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.List;

import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class UserSessionService implements Serializable {

	private static final long serialVersionUID = 8092237334743972540L;

	@Autowired
	private UserService userService;
	
	/**
	 * show Item menu Applicant Registration Form
	 * show by user STAFF and Admin
	 */
	public boolean displayAppReg(Long curUserID) {
		boolean display = false;
		User user = userService.findUser(curUserID);
		if(user != null){
			List<Role> roles = user != null ? user.getRoles() : null;
			if(roles != null && roles.size() > 0){
				for(Role r:roles){
					if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN") || r.getRolename().equalsIgnoreCase("ROLE_STAFF"))
						display = true;
				}
			}
		}
		return display;
	}

	/**
	 * show Item menu Pending Applicants
	 * show by user STAFF and Admin
	 */
	public boolean displayListAppOnReg(Long curUserID) {
		boolean display = false;
		User user = userService.findUser(curUserID);
		if(user != null){
			List<Role> roles = user != null ? user.getRoles() : null;
			if(roles != null && roles.size() > 0){
				for(Role r:roles){
					if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN") || r.getRolename().equalsIgnoreCase("ROLE_STAFF"))
						display = true;
				}
			}
		}
		return display;
	}

	/**
	 * show submenu registration_form and menuItem registration_form
	 * show by user STAFF and Admin and Company
	 * by HEAD do not show item (role HEAD = HEAD+Staff)
	 */
	public boolean displayRegistrationFormItem(Long curUserID) {
		boolean display = false;
		User user = userService.findUser(curUserID);
		if(user != null){
			List<Role> roles = user != null ? user.getRoles() : null;
			if(roles != null && roles.size() > 0){
				for(Role r:roles){
					if (r.getRolename().equalsIgnoreCase("ROLE_HEAD"))
						return false;
					if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN") || r.getRolename().equalsIgnoreCase("ROLE_STAFF")
							|| r.getRolename().equalsIgnoreCase("ROLE_COMPANY"))
						return true;
				}
			}
		}
		return display;
	}
	
	/**
	 * show menuItem saved applications
	 * show by user STAFF and Admin and Company
	 * by HEAD do not show item (role HEAD = HEAD+Staff)
	 */
	public boolean displaySavedItem(Long curUserID) {
		boolean display = false;
		User user = userService.findUser(curUserID);
		if(user != null){
			List<Role> roles = user != null ? user.getRoles() : null;
			if(roles != null && roles.size() > 0){
				for(Role r:roles){
					if (r.getRolename().equalsIgnoreCase("ROLE_HEAD"))
						return false;
					if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN"))
						return false;
					if (r.getRolename().equalsIgnoreCase("ROLE_STAFF")
							|| r.getRolename().equalsIgnoreCase("ROLE_COMPANY"))
						return true;
				}
			}
		}
		return display;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
