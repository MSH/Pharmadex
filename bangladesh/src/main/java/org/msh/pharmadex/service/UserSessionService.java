package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.List;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class UserSessionService implements Serializable {

	private static final long serialVersionUID = 2953901132984714933L;

	/**
     * show Item menu Applicant Registration Form
     * show by user STAFF and Admin
     */
    public boolean displayAppReg(UserSession userSession) {
    	boolean display = false;
    	User user = userSession.getUserAccess().getUser();
        List<Role> roles = user != null ? user.getRoles() : null;
        if(roles != null && roles.size() > 0){
        	for(Role r:roles){
        		if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN") || r.getRolename().equalsIgnoreCase("ROLE_STAFF"))
        			display = true;
        	}
        }
        return display;
    }
    
    /**
     * show Item menu Pending Applicants
     * show by user STAFF and Admin
     */
    public boolean displayListAppOnReg(UserSession userSession) {
    	boolean display = false;
    	User user = userSession.getUserAccess().getUser();
        List<Role> roles = user != null ? user.getRoles() : null;
        if(roles != null && roles.size() > 0){
        	for(Role r:roles){
        		if (r.getRolename().equalsIgnoreCase("ROLE_ADMIN") || r.getRolename().equalsIgnoreCase("ROLE_STAFF"))
        			display = true;
        	}
        }
        return display;
    }
}
