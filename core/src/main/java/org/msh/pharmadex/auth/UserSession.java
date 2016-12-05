package org.msh.pharmadex.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.mbean.product.ProdAppInit;
import org.msh.pharmadex.service.DisplayReviewInfo;
import org.msh.pharmadex.service.UserAccessService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@ManagedBean
@SessionScoped
public class UserSession implements Serializable, HttpSessionBindingListener {
    private static final long serialVersionUID = 2473412644164656187L;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    private Logger log = LoggerFactory.getLogger(DBControl.class);
    private UserAccess userAccess;
    private boolean displayMessagesKeys;
   // private String loggedInUser;
    private Long loggedINUserID;
    private Long applcantID;
    private Long prodID;
    private Long prodAppID;
    private boolean admin = false;
    private boolean company = false;
    private boolean staff = false;
    private boolean general = false;
    private boolean inspector = false;
    private boolean moderator = false;
    private boolean reviewer = false;
    private boolean head = false;
    private boolean csd = false;
    private boolean cst = false;
    private boolean lab = false;
    private boolean clinical = false;
    private boolean labModerator = false;
    private boolean labHead = false;
    private boolean displayAppReg = false;
    private boolean displayPricing = false;
    private DisplayReviewInfo displayReviewInfo;
    private ProdAppInit prodAppInit;
    private String workspaceName;
    private boolean ethiopia;

    @ManagedProperty(value = "#{userAccessService}")
    private UserAccessService userAccessService;

    @ManagedProperty(value = "#{onlineUserBean}")
    private OnlineUserBean onlineUserBean;
    private String sessionID;

    public void login() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("${pageContext.request.contextPath}/j_spring_security_check");
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

   /* public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }*/

    public String editUser() {
        return "/secure/usersettings.faces";
    }

    /**
     * Register the logout when the user session is finished by time-out
     */
    @Transactional
    public String logout() throws ServletException, IOException {
        if (userAccess == null) {
            return null;
        }
        

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                .getRequestDispatcher("/j_spring_security_logout");

        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());
        FacesContext.getCurrentInstance().responseComplete();
        
        // It's OK to return null here because Faces is just going to exit.

        return null;
    }

    public void keepSessionAlive(){
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        request.getSession();
        getLoggedINUserID();
    }

    /**
     * Register the user login
     */
    @Transactional
    public void registerLogin(User user, HttpServletRequest request) {

        // get client information
        String ipAddr = request.getRemoteAddr();
        String app = request.getHeader("User-Agent");
//        user = userService.findUser(user.getUserId());

        // register new login
        userAccess = new UserAccess();
        userAccess.setUser(user);
        userAccess.setLoginDate(new Date());
        userAccess.setApplication(JsfUtils.getBrowserName(app));
        userAccess.setIpAddress(ipAddr);
        onlineUserBean.add(userAccess);
        userAccessService.saveUserAccess(userAccess);
        setLoggedINUserID(user.getUserId());
        setApplcantID(user.getApplicant()!=null?user.getApplicant().getApplcntId():null);
        setWorkspaceParam();
        loadUserRoles();
    }

    private void setWorkspaceParam() {
        try {
            Workspace w = userAccessService.getWorkspace();
            setDisplayPricing(w.isDisplatPricing());
            setWorkspaceName(w.getName());
            if (workspaceName.equalsIgnoreCase("Ethiopia"))
                setEthiopia(true);
            else
                setEthiopia(false);
        } catch (NoResultException e) {
            setDisplayPricing(false);
        } catch (Exception e) {
            e.printStackTrace();
            setDisplayPricing(false);
        }
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
                    setDisplayAppReg(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_STAFF")) {
                    setStaff(true);
                    setDisplayAppReg(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_LAB")) {
                    setStaff(false);
                    setDisplayAppReg(false);
                    setCsd(false);
                    setLab(true);
                    setLabModerator(false);
                    setLabHead(false);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_LAB_MODERATOR")) {
                    setStaff(false);
                    setDisplayAppReg(false);
                    setCsd(false);
                    setLab(true);
                    setLabModerator(true);
                    setLabHead(false);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_LAB_HEAD")) {
                    setStaff(false);
                    setDisplayAppReg(false);
                    setCsd(false);
                    setLab(true);
                    setLabModerator(false);
                    setLabHead(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_CSD")) {
                    setStaff(true);
                    setDisplayAppReg(true);
                    setCsd(true);
                    setCst(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_CST")) {
                    setStaff(true);
                    setDisplayAppReg(true);
                    setCst(true);
                    setCsd(false);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_CLINICAL")) {
                    setClinical(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_COMPANY")) {
                    setCompany(true);
                    if (user.getApplicant() != null)
                        displayAppReg = false;
                    else
                        displayAppReg = true;
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_EXT")) {
                    setGeneral(true);
                    setDisplayAppReg(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_MODERATOR")) {
                    setModerator(true);
                    setLab(false);
                    setLabModerator(false);
                    setLabHead(false);
//                    setStaff(true);
//                    setDisplayAppReg(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_REVIEWER")) {
                    setReviewer(true);
//                    setStaff(true);
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_HEAD")) {
                    setHead(true);
                    setStaff(true);
                    setDisplayAppReg(true);

                }
                if (role.getRolename().equalsIgnoreCase("ROLE_INSPECTOR")) {
                    displayAppReg = true;
                    setInspector(true);
                }
            }
        }
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
     * Register the logout of the current user
     */
    public void registerLogout() {
        userAccess.setLogoutDate(new Date());

        userAccessService.update(userAccess);
        onlineUserBean.remove(userAccess);
    }

    public UserAccess getUserAccess() {
        return userAccess;
    }

    /**
     * @param userAccess the userLogin to set
     */
    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

    public boolean isDisplayMessagesKeys() {
        return displayMessagesKeys;
    }

    public void setDisplayMessagesKeys(boolean displayMessagesKeys) {
        this.displayMessagesKeys = displayMessagesKeys;
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

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isReviewer() {
        return reviewer;
    }

    public void setReviewer(boolean reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isHead() {
        return head;
    }

    public void setHead(boolean head) {
        this.head = head;
    }

    public boolean isDisplayAppReg() {
        return displayAppReg;
    }

    public void setDisplayAppReg(boolean displayAppReg) {
        this.displayAppReg = displayAppReg;
    }

    public boolean isDisplayPricing() {
        return displayPricing;
    }

    public void setDisplayPricing(boolean displayPricing) {
        this.displayPricing = displayPricing;
    }

    public Long getApplcantID() {
        return applcantID;
    }

    public void setApplcantID(Long applcantID) {
        this.applcantID = applcantID;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserAccessService getUserAccessService() {
        return userAccessService;
    }

    public void setUserAccessService(UserAccessService userAccessService) {
        this.userAccessService = userAccessService;
    }

    public OnlineUserBean getOnlineUserBean() {
        return onlineUserBean;
    }

    public void setOnlineUserBean(OnlineUserBean onlineUserBean) {
        this.onlineUserBean = onlineUserBean;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public boolean isCsd() {
        return csd;
    }

    public void setCsd(boolean csd) {
        this.csd = csd;
    }

    public boolean isEthiopia() {
        return ethiopia;
    }

    public void setEthiopia(boolean ethiopia) {
        this.ethiopia = ethiopia;
    }


    public boolean isLab() {
        return lab;
    }

    public void setLab(boolean lab) {
        this.lab = lab;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("Inside valueBound");
        log.info("valueBound:" + event.getName() + " session:" + event.getSession().getId());
        sessionID = event.getSession().getId();
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        log.info("valueUnBound:" + event.getName() + " session:" + event.getSession().getId());
        System.out.println("Inside valueUnbound");
        if (!getLoggedInUser().equals("") && sessionID.equals(event.getSession().getId())) {
            registerLogout();
        }
    }

    public Long getProdID() {
        return prodID;
    }

    public void setProdID(Long prodID) {
        this.prodID = prodID;
    }

    public Long getProdAppID() {
        return prodAppID;
    }

    public void setProdAppID(Long prodAppID) {
        this.prodAppID = prodAppID;
    }


    public DisplayReviewInfo getDisplayReviewInfo() {
        return displayReviewInfo;
    }

    public void setDisplayReviewInfo(DisplayReviewInfo displayReviewInfo) {
        this.displayReviewInfo = displayReviewInfo;
    }

    public ProdAppInit getProdAppInit() {
        return prodAppInit;
    }

    public void setProdAppInit(ProdAppInit prodAppInit) {
        this.prodAppInit = prodAppInit;
    }

    public Long getLoggedINUserID() {
        return loggedINUserID;
    }

    public void setLoggedINUserID(Long loggedINUserID) {
        this.loggedINUserID = loggedINUserID;
    }

    public boolean isLabModerator() {
        return labModerator;
    }

    public void setLabModerator(boolean labModerator) {
        this.labModerator = labModerator;
    }

    public boolean isClinical() {
        return clinical;
    }

    public void setClinical(boolean clinical) {
        this.clinical = clinical;
    }

    public boolean isCst() {
        return cst;
    }

    public void setCst(boolean cst) {
        this.cst = cst;
    }

    public boolean isLabHead() {
        return labHead;
    }

    public void setLabHead(boolean labHead) {
        this.labHead = labHead;
    }
    
    /**
     * Used in Mozambique
	 * show submenu registration_form and menuItem registration_form
	 * show by user STAFF and Admin and Company
	 * by HEAD do not show item (role HEAD = HEAD+Staff)
	 */
	public boolean displayRegistrationFormItemMZ() {
		if(isHead())
			return false;
		if(isAdmin() || isStaff() || isCompany())
			return true;
		
		return false;
	}
	
	/**
	 * Used in Mozambique
	 * show Item menu Applicant Registration Form
	 * show by user STAFF and Admin
	 */
	public boolean displayAppRegMZ() {
		if(isAdmin() || isStaff())
			return true;
		
		return false;
	}
	
	/**
	 * Used in Mozambique
	 * show Item menu Pending Applicants
	 * show by user STAFF and Admin
	 */
	public boolean displayListAppOnRegMZ() {
		if(isAdmin() || isStaff())
			return true;
		
		return false;
	}
	
	/**
	 * Used in Mozambique
	 * show menuItem saved applications
	 * show by user STAFF and Admin and Company
	 * by HEAD do not show item (role HEAD = HEAD+Staff)
	 */
	public boolean displaySavedItemMZ() {
		if(isHead() || isAdmin())
			return false;
		if(isStaff() || isCompany())
			return true;
		
		return false;
	}
}
