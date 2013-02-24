package org.msh.pharmadex.failure;

import org.msh.pharmadex.auth.OnlineUserBean;
import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.service.UserAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Component("userSession")
@Scope("session")
public class UserSessionImpl implements UserSession, Serializable {

    private static final long serialVersionUID = 2473412644164656187L;
    private UserAccess userAccess;
    private boolean displayMessagesKeys;
    private String loggedInUser;
    private User loggedInUserObj;

    private boolean admin = false;
    private boolean company = false;
    private boolean staff = false;
    private boolean general = false;
    private boolean inspector = false;

    public void login() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/pharmadex/j_spring_security_check");
            System.out.println("reached inside login usersession");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getLoggedInUser() {
        if (userAccess != null)
            return userAccess.getUser().getName();
        else
            return "";
    }

    public String editUser() {
        System.out.print("inside edituser");
        return "/secure/usersettings.faces";
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Autowired
    private UserAccessService userAccessService;

    @Autowired
    private OnlineUserBean onlineUsersHome;

    @Autowired
    private WebSession webSession;

    /**
     * Register the logout when the user session is finished by time-out
     */
    @Transactional
    public void logout() {
        if (userAccess == null) {
            return;
        }
        registerLogout();
    }

    /**
     * Register the user login
     */
    @Transactional
    public void registerLogin(User user, HttpServletRequest request) {
        // get client information
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String ipAddr = request.getRemoteAddr();
        String app = request.getHeader("User-Agent");

        // register new login        
        userAccess = new UserAccess();
        userAccess.setUser(user);
        userAccess.setLoginDate(new Date());
        if (app != null && app.length() > 200)
            app = app.substring(0, 200);
        userAccess.setApplication(app);
        userAccess.setIpAddress(ipAddr);
        onlineUsersHome.add(userAccess);
        userAccessService.saveUserAccess(userAccess);
        webSession.setUser(user);
        webSession.setApplicant(user.getApplicant());
        loadUserRoles();
    }

    private void loadUserRoles() {
        User user = userAccess.getUser();
        List<Role> roles = user.getRoles();
        if (roles != null) {
            for (Role role : roles) {
                if (role.getRolename().equalsIgnoreCase("ROLE_ADMIN")) {
                    setAdmin(true);
                    setStaff(true);
                    setGeneral(true);
                    setInspector(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_STAFF"))
                    setStaff(true);
                if (role.getRolename().equalsIgnoreCase("ROLE_COMPANY"))
                    setCompany(true);
                if (role.getRolename().equalsIgnoreCase("ROLE_PUBLIC"))
                    setGeneral(true);
                if (role.getRolename().equalsIgnoreCase("ROLE_INSPECTOR"))
                    setInspector(true);
            }
        }
    }


    /**
     * Register the logout of the current user
     */
    public void registerLogout() {
        userAccess.setLogoutDate(new Date());

        userAccessService.update(userAccess);
        onlineUsersHome.remove(userAccess);
    }


//    /**
//     * Monta a lista de permiss�es do usu�rio
//     * @param usu
//     */
//    public void updateUserRoleList() {
//    	removePermissions();
//    	
//    	Identity identity = Identity.instance();
//    	UserProfile prof = userWorkspace.getProfile();
//
//    	List<Object[]> lst = getEntityManager().createQuery("select u.userRole.name, u.canChange, u.caseClassification " +
//    			"from UserPermission u where u.userProfile.id = :id and u.canExecute = true")
//    			.setParameter("id", prof.getId())
//    			.getResultList();
//    	
//    	for (Object[] vals: lst) {
//    		String roleName = (String)vals[0];
//    		
//    		CaseClassification classification = (CaseClassification)vals[2];
//
//    		if (classification != null)
//    			roleName = classification.toString() + "_" + roleName;
//    		identity.addRole(roleName);
//    	
//    		boolean change = (Boolean)vals[1];
//    		if (change) {
//    			identity.addRole(roleName + "_EDT");
//    		}
//    	}
//    }


//    /**
//     * Remove all user permissions for the current session (in memory operation)
//     */
//    protected void removePermissions() {
//    	Identity identity = Identity.instance();
//    	for (Group g: identity.getSubject().getPrincipals(Group.class)) {
//    		if (g.getName().equals("Roles")) {
//    			Enumeration e = g.members();
//    			
//    			List<Principal> members = new ArrayList<Principal>();
//    			while (e.hasMoreElements()) {
//    				Principal member = (Principal) e.nextElement();
//    				members.add(member);
//    			}
//
//    			for (Principal p: members) {
//    				g.removeMember(p);
//    			}
//    		}
//    	}    	
//    }


    /**
     * @param userAccess the userLogin to set
     */
    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public UserAccess getUserAccess() {
        return userAccess;
    }

    public boolean isDisplayMessagesKeys() {
        return displayMessagesKeys;
    }


    public void setDisplayMessagesKeys(boolean displayMessagesKeys) {
        this.displayMessagesKeys = displayMessagesKeys;
    }

    public User getLoggedInUserObj() {
        if (userAccess != null)
            return userAccess.getUser();
        else
            return null;
    }

    public void setLoggedInUserObj(User loggedInUserObj) {
        this.loggedInUserObj = loggedInUserObj;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }

    public boolean isInspector() {
        return inspector;
    }

    public void setInspector(boolean inspector) {
        this.inspector = inspector;
    }
}
