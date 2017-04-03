package org.msh.pharmadex.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

import org.msh.pharmadex.domain.AgentAgreement;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.mbean.product.ProdAppInit;
import org.msh.pharmadex.service.ApplicantService;
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
	private DisplayReviewInfo displayReviewInfo;
	private ProdAppInit prodAppInit;
	private String workspaceName;
	private boolean ethiopia=false;

	@ManagedProperty(value = "#{userAccessService}")
	private UserAccessService userAccessService;
	@ManagedProperty(value = "#{applicantService}")
	public ApplicantService applicantService;
	
	@ManagedProperty(value = "#{onlineUserBean}")
	private OnlineUserBean onlineUserBean;
	private String sessionID;
	//current roles, actually it is only one role, but...
	private List<String> roles = new ArrayList<String>();
	private List<String> allRoles = new ArrayList<String>();
	private boolean displayPricing;
	private List<Role> rList;

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

	private void setDisplayPricing(boolean displatPricing) {
		// TODO Auto-generated method stub

	}

	/**
	 * Cache all roles for this user set first role as active
	 */
	private void loadUserRoles() {
		User user = userAccess.getUser();
		rList = user.getRoles();
		//fetch all roles
		if(rList != null){
			for(Role r : rList){
				getAllRoles().add(r.getRolename().toUpperCase());
			}
		}
		//first roles will be active, only Admin is exception!!!!!
		getRoles().clear();
		if (getAllRoles().contains("ROLE_ADMIN")){
			getRoles().add("ROLE_ADMIN");
		}else{
			if(getAllRoles().size()>0){
				getRoles().add(getAllRoles().get(0));
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

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}



	public List<String> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<String> allRoles) {
		this.allRoles = allRoles;
	}

	public boolean isAdmin() {
		return getRoles().contains("ROLE_ADMIN");
	}


	public boolean isCompany() {
		return getRoles().contains("ROLE_COMPANY");
	}


	public boolean isStaff() {
		return getRoles().contains("ROLE_STAFF");
	}

	public boolean isModerator() {
		return getRoles().contains("ROLE_MODERATOR");
	}


	public boolean isReviewer() {
		return getRoles().contains("ROLE_REVIEWER");
	}


	public boolean isHead() {
		return getRoles().contains("ROLE_HEAD");
	}


	public boolean isDisplayAppReg() {
		return isStaff() || isCompany();
	}

	public boolean isDisplayPricing() {
		return displayPricing;
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

	public ApplicantService getApplicantService() {
		return applicantService;
	}
	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}
	
	public String getWorkspaceName() {
		return workspaceName;
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}


	public boolean isEthiopia() {
		return ethiopia;
	}

	public void setEthiopia(boolean ethiopia) {
		this.ethiopia = ethiopia;
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
		if(isAdmin() || isStaff() )
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

	public boolean isLab() {
		//TODO not implemented yet
		return false;
	}
	
	public boolean isLabHead() {
		//TODO not implemented yet
		return false;
    }
	
	public boolean isLabModerator() {
		//TODO not implemented yet
		return false;
	}

	public boolean isClinical() {
		// TODO not implemented yet
		return false;
	}

	public boolean isInspector() {
		// TODO not implemented yet
		return false;
	}

	public boolean isCsd(){
		// TODO not implemented yet
		return false;
	}

	public List<Role> getrList() {
		if(rList == null){
			rList = new ArrayList<Role>();
		}
		return rList;
	}


	/**
	 * Display current user's role
	 * @return empty string or current role
	 */
	public String displayCurrentRole(){
		Role r = getCurrentRole();
		if(r!=null){
			return r.getDisplayname();
		}else{
			return "";
		}
	}

	/**
	 * Return role from the list of user's roles by role key
	 * @param roleKey
	 * @return null if not found
	 */
	private Role fetchRoleByKey(String roleKey) {
		for(Role r :getrList()){
			if(r.getRolename().toUpperCase().equals(roleKey)){
				return r;
			}
		}
		return null; 
	}

	/**
	 * Is this user has many roles
	 * @return
	 */
	public boolean isMultiRole(){
		return getrList().size()>1;
	}

	/**
	 * Get current user's role Current is the first in list (for the present)
	 * @return null if not found
	 */
	public Role getCurrentRole(){
		if(getRoles().size()>0){
			String roleKey = getRoles().get(0);
			return fetchRoleByKey(roleKey);
		}else{
			return null;
		}
	}
	/**
	 * Set current user's role Current is the first in list (for the present)
	 * @param r if null - do nothing
	 */
	public void setCurrentRole(Role r){
		if(r!=null){
			Role ret = fetchRoleByKey(r.getRolename());
			if(ret != null){
				getRoles().clear();
				getRoles().add(r.getRolename());
			}
		}
	}
	/**
	 * Is this user responsible for an applicant
	 * @return
	 */
	public boolean isResponsible(Applicant applicant){
		if(isCompany()){
			User user = getUserAccess().getUser();
			if(user != null){
				//Applicant applicant = user.getApplicant();
				if(applicant != null && applicant.getContactName() != null){
					return applicant.getContactName().equalsIgnoreCase(user.getUsername());
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	/**
	 * applicant is agent 
	 * @return
	 */
	public boolean isAgent(Applicant prodApp_Applicant){
		if(isCompany()){
			User user = getUserAccess().getUser();
			if(user == null)
				return false;
			
			Applicant curAppl = user.getApplicant();
			if(curAppl == null)
				return false;
			
			List<AgentAgreement> agents = getApplicantService().fetchAgentAgreements(curAppl);

			for(AgentAgreement ag:agents){
				if(ag.getAgent() != null && ag.getAgent().getApplcntId().intValue() == prodApp_Applicant.getApplcntId().intValue()){
					if(ag.getFinish().after(new Date()))
						return true;
				}
			}
			//List<AgentAgreement> agentAgreements = getApplicantService().fetchAgentAgreements(getSelectedApplicant());
		}
		return false;
	}
	/**
	 * Get Id of applicant if current user is company user, otherwise 0
	 * @return
	 */
	public long getUserApplicantId(){
		User user = getUserAccess().getUser();
		if(user != null){
			Applicant applicant = user.getApplicant();
			if(applicant != null){
				return applicant.getApplcntId();
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
	
	/**
	 * Go to home page
	 */
	public void goHome(){
		try {
			String path = FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String defaultHomePage(){
		if(isCompany() || isStaff())
			return "../templates/submittedproducts_templ.xhtml";
		if(isModerator() || isHead() || isAdmin())
			return "../templates/processprodlisttempl.xhtml";
		if(isReviewer())
			return "../templates/processreviewlisttempl.xhtml";
			
		return "../templates/registerproductstempl.xhtml";
	}
	
	public boolean isDisplayPaymentList(){
		if(isCompany())
			return true;
		return false;
	}
}
