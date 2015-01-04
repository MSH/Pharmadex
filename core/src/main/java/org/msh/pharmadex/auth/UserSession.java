package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.DisplayReviewInfo;
import org.msh.pharmadex.service.UserAccessService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class UserSession implements Serializable {

    private static final long serialVersionUID = 2473412644164656187L;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    private UserAccess userAccess;
    private boolean displayMessagesKeys;
    private String loggedInUser;
    private User loggedInUserObj;
    private Applicant applicant;
    private Product product;
    private UploadedFile file;
    private ProdAppChecklist prodAppChecklist;
    private ProdApplications prodApplications;
    private Review review;
    private Long reviewInfoID;
    private Long applcantID;
    private boolean admin = false;
    private boolean company = false;
    private boolean staff = false;
    private boolean general = false;
    private boolean inspector = false;
    private boolean moderator = false;
    private boolean reviewer = false;
    private boolean head = false;
    private boolean displayAppReg = false;
    private boolean displayPricing = false;
    private DisplayReviewInfo displayReviewInfo;

    @ManagedProperty(value = "#{userAccessService}")
    private UserAccessService userAccessService;

    @ManagedProperty(value = "#{onlineUserBean}")
    private OnlineUserBean onlineUserBean;
    private String licHolderID;


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

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public String editUser() {
        return "/secure/usersettings.faces";
    }

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
        String ipAddr = request.getRemoteAddr();
        String app = request.getHeader("User-Agent");
        user = userService.findUser(user.getUserId());

        // register new login        
        userAccess = new UserAccess();
        userAccess.setUser(user);
        userAccess.setLoginDate(new Date());
        userAccess.setApplication(JsfUtils.getBrowserName(app));
        userAccess.setIpAddress(ipAddr);
        onlineUserBean.add(userAccess);
        userAccessService.saveUserAccess(userAccess);
        setLoggedInUserObj(user);
        setApplicant(user.getApplicant());
        setWorkspaceParam();
        loadUserRoles();
    }

    private void setWorkspaceParam() {
        try {
            Workspace w = userAccessService.getWorkspace();
            setDisplayPricing(w.isDisplatPricing());
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
                if (role.getRolename().equalsIgnoreCase("ROLE_COMPANY")) {
                    setCompany(true);
                    if (user.getApplicant() != null)
                        displayAppReg = false;
                    else
                        displayAppReg = true;
                }
                if (role.getRolename().equalsIgnoreCase("ROLE_PUBLIC"))
                    setGeneral(true);
                if (role.getRolename().equalsIgnoreCase("ROLE_MODERATOR")) {
                    setModerator(true);
//                    setStaff(true);
                    setDisplayAppReg(true);
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
            }
        }
    }


    /**
     * Register the logout of the current user
     */
    public void registerLogout() {
        userAccess.setLogoutDate(new Date());

        userAccessService.update(userAccess);
        onlineUserBean.remove(userAccess);
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ProdAppChecklist getProdAppChecklist() {
        return prodAppChecklist;
    }

    public void setProdAppChecklist(ProdAppChecklist prodAppChecklist) {
        this.prodAppChecklist = prodAppChecklist;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
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

    public Long getReviewInfoID() {
        return reviewInfoID;
    }

    public void setReviewInfoID(Long reviewInfoID) {
        this.reviewInfoID = reviewInfoID;
    }

    public DisplayReviewInfo getDisplayReviewInfo() {
        return displayReviewInfo;
    }

    public void setDisplayReviewInfo(DisplayReviewInfo displayReviewInfo) {
        this.displayReviewInfo = displayReviewInfo;
    }

    public void setLicHolderID(String licHolderID) {
        this.licHolderID = licHolderID;
    }

    public String getLicHolderID() {
        return licHolderID;
    }
}
