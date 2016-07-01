package org.msh.pharmadex.mbean.applicant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.domain.enums.UserRole;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessAppBn implements Serializable {

	private static final long serialVersionUID = -8302237287679562751L;

	@ManagedProperty(value = "#{globalEntityLists}")
	GlobalEntityLists globalEntityLists;
	FacesContext facesContext = FacesContext.getCurrentInstance();
	ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	private Applicant applicant;
	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
	@ManagedProperty(value = "#{applicantService}")
	private ApplicantService applicantService;
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	@ManagedProperty(value = "#{mailService}")
	private MailService mailService;

	@ManagedProperty(value = "#{roleDAO}")
	RoleDAO roleDAO;

	@ManagedProperty(value = "#{applicantMBean}")
	private ApplicantMBean applicantMBean;

	//private User responsable;

	/** Always alone */
	//private List<User> responsableList;
	//private List<User> users;
	//private User selectedUser;

	/** use in form applicant */
	private List<User> usersByApplicant;
	//private User selectResponsable;
	//private User responsable;
	//private User selectedUser;

	/** new User - create only ADMINISTRATOR */
	private User user;

	private List<ProdApplications> prodApplicationses;
	private List<ProdApplications> prodNotRegApplicationses;

	/* @Transactional
    public void replaceResponsableInApplicant() {
        responsableList = new ArrayList<User>();
        responsableList.add(responsable);
        applicant.setContactName(responsable.getUsername());
    }*/

	@Transactional
	public void addSelectUserInList() {
		if(getApplicantMBean().getUser() != null){
			if(usersByApplicant == null)
				usersByApplicant = new ArrayList<User>();
			boolean flag = true;
			for(User us:usersByApplicant){
				if(us.getEmail().equals(getApplicantMBean().getUser().getEmail()))
					flag = false;
			}
			if(flag)
				usersByApplicant.add(getApplicantMBean().getUser());
			else{
				FacesMessage msg = new FacesMessage(resourceBundle.getString("Error.dublicateUser"), getApplicantMBean().getUser().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
	}

	public String saveApp() {
		if(varificationApplicant(false)){
			applicant.setUpdatedDate(new Date());
			applicant = applicantService.updateApp(applicant, null);

			facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "applicantMBean", null);
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
			if(!applicant.getState().equals(ApplicantState.REGISTERED))
				return "/internal/processapplist.faces";
			else
				return "/public/applicantlist.faces";
		}
		return null;
	}

	public String submitApp() {
		if(varificationApplicant(true)){
			applicant.setSubmitDate(new Date());
			applicant = applicantService.submitApp(applicant);

			facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "processAppBn", null);
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
			//return "/public/applicantlist.faces";
			return "/internal/processapplist.faces";
		}
		return null;
	}

	public String registerApplicant() {
		if(varificationApplicant(false)){
			applicantService.updateApp(applicant, ApplicantState.REGISTERED);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "processAppBn", null);
			return "/internal/processapplist.faces";
		}
		return null;
	}

	private boolean varificationApplicant(boolean isSubmit){
		try {
			if (applicant == null) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
				return false;
			}
			if(isSubmit){
				if(applicantService.isApplicantDuplicated(applicant.getAppName())){
					facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_applicant_exist"), ""));
					return false;
				}
			}
			if (!(usersByApplicant != null && usersByApplicant.size() > 0)) {
				FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
				error.setSeverity(FacesMessage.SEVERITY_ERROR);
				facesContext.addMessage(null, error);
				return false;
			}
			if(getApplicantMBean().getSelectResponsable() == null){
				FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
				error.setSeverity(FacesMessage.SEVERITY_ERROR);
				facesContext.addMessage(null, error);
				return false;
			}
			// set or update responsable
			applicant.setContactName(getApplicantMBean().getSelectResponsable() != null ? getApplicantMBean().getSelectResponsable().getUsername() : "");
			applicant.setUsers(usersByApplicant);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
			return false;
		}
	}

	public String cancel() {
		ApplicantState appState = applicant.getState();
		applicant = new Applicant();
		getApplicantMBean().setSelectResponsable(null);
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		WebUtils.setSessionAttribute(request, "processAppBn", null);

		if(userSession.getLoggedINUserID() == null){ // no logged user
			return "/public/applicantlist.faces";
		}else{
			if(!appState.equals(ApplicantState.REGISTERED))
				return "/internal/processapplist.faces";
			else
				return "/public/applicantlist.faces";
		}
	}

	public List<User> completeUserList(String query) {
		return JsfUtils.completeSuggestions(query, getUnregisteredUsers());
	}

	public String cancelAddUser() {
		return "";
	}

	public Applicant getApplicant() {
		if (applicant == null) {
			if (userSession.getApplcantID() != null) {
				applicant = applicantService.findApplicant(userSession.getApplcantID());
				if (applicant.getAddress() == null)
					applicant.setAddress(new Address());
				if (applicant.getAddress().getCountry() == null)
					applicant.getAddress().setCountry(new Country());
			} else {
				applicant = null;
			}
		}
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public List<User> getUnregisteredUsers() {
		return userService.findUnregisteredUsers();
	}

	public List<User> getUsersByApplicant(){
		return usersByApplicant;
	}

	public void setUsersByApplicant(List<User> list){
		this.usersByApplicant = list;
	}

	public String loadUsersByApplicant(){
		if(applicant == null)
			applicant = getApplicant();
		// init usersByApplicant list
		if(applicant != null){
			if(usersByApplicant == null){
				List<User> list = userService.findUserByApplicant(applicant.getApplcntId());
				User resp = findResponsableInDB();
				addUserInList(resp, list);
				getApplicantMBean().setSelectResponsable(resp);

				usersByApplicant = new ArrayList<User>();
				usersByApplicant.addAll(list);
			}
		}else
			usersByApplicant = new ArrayList<User>();
		return resourceBundle.getString("user_lookup");
	}


	private User findResponsableInDB(){
		User resp = null;
		String contactName = applicant.getContactName();
		if(contactName != null && !contactName.equals("") && !contactName.equals("NOT SPECIFIED")){
			resp = userService.findUserByUsername(contactName);
		}
		return resp;
	}

	/*public void setSelectResponsable(User us){
		this.selectResponsable = us;
	}

	public User getSelectResponsable(){
		return this.selectResponsable;
	}
	 */
	public void onRowSelect(SelectEvent event) {
		User us = (User) event.getObject();
		if(us != null && !us.isEnabled()){
			getApplicantMBean().setSelectResponsable(null);
			FacesMessage msg = new FacesMessage("Selected User does not have an access!!! ", us.getUsername());
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return;
		}

		FacesMessage msg = new FacesMessage("User Selected", us.getUsername());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/*	public void onRowUnselect(UnselectEvent event) {
		FacesMessage msg = new FacesMessage("Car Unselected", ((Applicant) event.getObject()).getAppName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}*/



	/**
	 * Add user from list users not relativs with applicant
	 * adn userType = COMPANY
	 * @return
	 */
	public List<User> getUsersNotApplicant() {
		List<User> list = userService.findUnregisteredUsers();
		return list;
	}

	/*public User getResponsable() {
		return responsable;
	}

	public void setResponsable(User user) {
		this.responsable = user;
	}*/

	private void addUserInList(User us, List<User> list){
		if(us != null && list != null){
			boolean contains = false;
			for(User u:list){
				if(us.getUserId().intValue() == u.getUserId().intValue()){
					contains = true;
					break;
				}
			}
			if(!contains)
				list.add(us);
		}
	}

	//TODO
	public void initNewUser() {
		user = new User();
		user.setType(UserType.COMPANY);
		user.setEnabled(false);
	}

	@Transactional
	public void newUser() {
		user.setRegistrationDate(new Date());
		user.setEnabled(false);
		user.setType(UserType.COMPANY);

		List<Role> roles = new ArrayList<Role>();
		Role role = findRole(UserRole.ROLE_COMPANY);
		if(role != null)
			roles.add(role);
		user.setRoles(roles);

		String username = user.getName().replaceAll("\\s", "");
		user.setUsername(username);
		user.setPassword(username);
		user = userService.passwordGenerator(user);

		if (usersByApplicant == null)
			usersByApplicant = new ArrayList<User>();
		usersByApplicant.add(user);
		applicant.setUsers(usersByApplicant);
		//user.setUserId(new Long(68));

		user = userService.updateUser(user);
		//selectedUser = responsable;
		//user = new User();

	}

	private Role findRole(UserRole enumrole){
		List<Role> allRoles = (List<Role>) roleDAO.findAll();
		if(allRoles != null && allRoles.size() > 0){
			for(Role r:allRoles){
				if(r.getRolename().equals(enumrole))
					return r;
			}
		}
		return null;
	}
	////
	public GlobalEntityLists getGlobalEntityLists() {
		return globalEntityLists;
	}

	public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
		this.globalEntityLists = globalEntityLists;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public ApplicantService getApplicantService() {
		return applicantService;
	}

	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	/*public User getSelectedUser(){
		return selectedUser;
	}

	public void setSelectedUser(User u){
		this.selectedUser = u;
	}*/

	/*public String userIsEnabled(User usRow){
		if(usRow != null)
			if(!usRow.isEnabled())
				return "!";
		return "";
	}*/

	public User getUser(){
		return user;
	}

	public void setUser(User u){
		this.user = u;
	}

	public void setRoleDAO(RoleDAO r){
		this.roleDAO = r;
	}

	public RoleDAO getRoleDAO(){
		return this.roleDAO;
	}

	public ApplicantMBean getApplicantMBean(){
		return applicantMBean;
	}

	public void setApplicantMBean(ApplicantMBean applMBean){
		this.applicantMBean = applMBean;
	}

	public List<ProdApplications> getProdApplicationses() {
		if (prodApplicationses == null && getApplicant() != null)
			prodApplicationses = applicantService.findRegProductForApplicant(getApplicant().getApplcntId());

		return prodApplicationses;
	}

	public void setProdApplicationses(List<ProdApplications> prodApplicationses) {
		this.prodApplicationses = prodApplicationses;
	}

	public List<ProdApplications> getProdNotRegApplicationses() {
		if (prodNotRegApplicationses == null && getApplicant() != null)
			prodNotRegApplicationses = applicantService.findProductNotRegForApplicant(getApplicant().getApplcntId());

		return prodNotRegApplicationses;
	}

	public void setProdNotRegApplicationses(List<ProdApplications> prodNotRegApplicationses) {
		this.prodNotRegApplicationses = prodNotRegApplicationses;
	}
}
