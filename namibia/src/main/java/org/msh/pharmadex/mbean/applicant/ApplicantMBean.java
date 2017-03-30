package org.msh.pharmadex.mbean.applicant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ApplicantListDTO;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.AgentAgreement;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ApplicantType;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.domain.enums.UserRole;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.context.RequestContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ApplicantMBean implements Serializable {

	private static final long serialVersionUID = -3983563460376543047L;
	@ManagedProperty(value = "#{applicantService}")
	ApplicantService applicantService;
	@ManagedProperty(value = "#{userService}")
	UserService userService;
	@ManagedProperty(value = "#{globalEntityLists}")
	GlobalEntityLists globalEntityLists;
	FacesContext facesContext = FacesContext.getCurrentInstance();
	ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	private Applicant selectedApplicant;
	private List<Applicant> allApplicant;
	private List<Applicant> allStateApplicant;
	private List<ApplicantListDTO> filteredApplicant;
	private User user;
	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;

	@ManagedProperty(value = "#{roleDAO}")
	private RoleDAO roleDAO;

	private User selectResponsable;
	/** use in form applicant */
	private List<User> usersByApplicant;

	private String sourcePage="/home.faces";

	private List<ProdApplications> prodApplicationses;
	private List<ProdApplications> prodNotRegApplicationses;
	private AgentAgreement selectedAgentAgreement;
	private List<AgentAgreement> agentAgreements = new ArrayList<AgentAgreement>();
	private List<AgentAgreement> myAgentAgreements = new ArrayList<AgentAgreement>();
	private Date startDate = null;
	private Date finishDate = null;
	private Applicant selectedAgent = null;

	@PostConstruct
	private void init() {
		//appID
		String appID = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("appID");
		if(appID != null && !appID.equals("")) {
			selectedApplicant = applicantService.findApplicant(Long.valueOf(appID));
			if(selectedApplicant != null){
				usersByApplicant = new ArrayList<User>();
				if(selectedApplicant.getUsers() != null && selectedApplicant.getUsers().size() > 0)
					usersByApplicant.addAll(selectedApplicant.getUsers());

				selectResponsable = addUserInList(findResponsableInDB(), usersByApplicant);
			}
		}else{
			selectedApplicant = new Applicant();
			usersByApplicant = new ArrayList<User>();
			selectResponsable = new User();
		}

		if(getSelectedApplicant() != null && getSelectedApplicant().getApplcntId() != null && getSelectedApplicant().getApplcntId() > 0){
			agentAgreements = getApplicantService().fetchAgentAgreements(getSelectedApplicant());
			myAgentAgreements = getApplicantService().fetchMyApplicants(getSelectedApplicant());
		}

		String srcPage = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sourcePage");
		if (srcPage != null){
			sourcePage = srcPage;
			buildIdApp();
		}
	}

	private void buildIdApp(){
		int index = sourcePage.indexOf(":");
		if(index != -1){
			//String id = sourcePage.substring(0, index);
			//idAppSource = new Long(id);
			sourcePage = sourcePage.substring(index + 1);
		}
	}

	public List<User> completeUserList(String query) {
		return JsfUtils.completeSuggestions(query, getUnregisteredUsers());
	}

	public void onRowSelect() {
		facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), "Selected " + selectedApplicant.getAppName()));
	}

	public void initNewUser() {
		user = new User();
		user.setType(UserType.COMPANY);
		user.setEnabled(false);
	}

	@Transactional
	public void addSelectUserInList() {
		if(user != null){
			if(usersByApplicant == null)
				usersByApplicant = new ArrayList<User>();
			boolean flag = true;
			for(User us:usersByApplicant){
				if(us.getEmail().equals(user.getEmail()))
					flag = false;
			}
			if(flag)
				usersByApplicant.add(user);
			else{
				FacesMessage msg = new FacesMessage(resourceBundle.getString("Error.dublicateUser"), user.getUsername());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			user = new User();
		}
	}

	public String submitApp() {
		if(varificationApplicant(true)){
			selectedApplicant.setSubmitDate(new Date());
			applicantService.submitApp(selectedApplicant);

			facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "applicantMBean", null);
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
			return sourcePage;
		}
		return null;
	}

	public String saveApp() {
		if(varificationApplicant(false)){
			selectedApplicant.setUpdatedDate(new Date());
			applicantService.updateApp(selectedApplicant, null);

			facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "applicantMBean", null);
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
			return sourcePage;
		}
		return null;
	}

	public String registerApplicant() {
		if(varificationApplicant(false)){
			applicantService.updateApp(selectedApplicant, ApplicantState.REGISTERED);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "applicantMBean", null);
			return sourcePage;
		}
		return null;
	}

	private boolean varificationApplicant(boolean isSubmit){
		try {
			if (selectedApplicant == null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
				return false;
			}
			if(isSubmit){
				if(applicantService.isApplicantDuplicated(selectedApplicant.getAppName())){
					FacesMessage error = new FacesMessage(resourceBundle.getString("valid_applicant_exist"));
					error.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, error);
					return false;
				}
			}
			if (!(usersByApplicant != null && usersByApplicant.size() > 0)) {
				FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
				error.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, error);
				return false;
			}
			if(selectResponsable == null){
				FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
				error.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, error);
				return false;
			}
			// set or update responsable
			selectedApplicant.setContactName(selectResponsable != null ? selectResponsable.getUsername() : "");
			selectedApplicant.setUsers(usersByApplicant);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
			return false;
		}
	}

	public List<ApplicantType> completeApplicantTypeList(String query) {
		return JsfUtils.completeSuggestions(query, globalEntityLists.getApplicantTypes());
	}

	public void editApp() {
		System.out.println("inside editUser");
	}

	public String cancelApp() {
		selectedApplicant = new Applicant();
		facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		WebUtils.setSessionAttribute(request, "applicantMBean", null);
		//"/home.faces"
		return sourcePage;
	}

	@Transactional
	public void addUserToApplicant() {
		if (usersByApplicant == null)
			usersByApplicant = new ArrayList<User>();
		usersByApplicant.add(user);
		selectedApplicant.setUsers(usersByApplicant);
		user = new User();

	}

	@Transactional
	public void newUser() {
		user.setEnabled(false);
		user.setType(UserType.COMPANY);
		String username = user.getName().replaceAll("\\s", "");
		user.setUsername(username);
		user.setPassword(username);

		user = userService.passwordGenerator(user);
		List<Role> roles = new ArrayList<Role>();
		Role role = findRole(UserRole.ROLE_COMPANY);
		if(role != null)
			roles.add(role);
		user.setRoles(roles);

		User verifUser = userService.findByUsernameOrEmail(user);
		if(verifUser != null){// dublicate
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.validationFailed();
			ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
			facesContext.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("valid_user_exist"), resourceBundle.getString("valid_user_exist")));
			return ;
		}

		if (usersByApplicant == null)
			usersByApplicant = new ArrayList<User>();
		usersByApplicant.add(user);
		selectedApplicant.setUsers(usersByApplicant);

		user = new User();
	}

	public Role findRole(UserRole enumrole){
		List<Role> allRoles = (List<Role>) roleDAO.findAll();
		if(allRoles != null && allRoles.size() > 0){
			for(Role r:allRoles){
				if(r.getRolename().equals(enumrole.name()))
					return r;
			}
		}
		return null;
	}

	public void validate(ComponentSystemEvent e) {
		if(selectResponsable == null){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Error selected user."));
			fc.renderResponse();
		}
	}

	public String cancelAddUser() {
		user = new User();
		return "";
	}


	public List<User> getAvailableUsers() {
		return userService.findUnregisteredUsers();
	}


	public Applicant getSelectedApplicant() {
		return selectedApplicant;
	}

	public void setSelectedApplicant(Applicant selectedApplicant) {
		this.selectedApplicant = selectedApplicant;
	}


	public List<Applicant> getAllApplicant() {
		if(allApplicant==null)
			allApplicant = applicantService.getRegApplicants();

		return allApplicant;
	}
	/**
	 * Get all registered applicants to display
	 * @return
	 */
	public List<ApplicantListDTO> getAllForList(){
		return applicantService.getAllRegisteredForList();
	}
	
	public void setAllForList(List<ApplicantListDTO> dummy){
		//nothing to do, only obey bean specifications
	}

	public void setAllApplicant(List<Applicant> allApplicant) {
		this.allApplicant = allApplicant;
	}

	public void setAllStateApplicant(List<Applicant> allStateApplicant) {
		this.allStateApplicant = allStateApplicant;
	}

	public List<Applicant> getAllStateApplicant() {
		if(allStateApplicant==null)

			allStateApplicant = applicantService.findAllApplicants(null);

		return allStateApplicant;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = userService.findUser(user.getUserId());
	}

	public List<ApplicantListDTO> getFilteredApplicant() {
		return filteredApplicant;
	}

	public void setFilteredApplicant(List<ApplicantListDTO> filteredApplicant) {
		this.filteredApplicant = filteredApplicant;
	}

	public String userIsEnabled(boolean isEn){
		if(!isEn)
			return "X";
		return "";
	}

	private User findResponsableInDB(){
		User resp = null;
		if(selectedApplicant != null && selectedApplicant.getApplcntId() != null){
			String contactName = selectedApplicant.getContactName();
			if(contactName != null && !contactName.equals("") && !contactName.equals("NOT SPECIFIED")){
				resp = userService.findUserByUsername(contactName);
				if(resp != null){
					if (resp.getApplicant()==null){
						resp = null;
						selectedApplicant.setContactName("");
					}
				}else{
					selectedApplicant.setContactName("");
				}
			}	
		}
		return resp;
	}

	private User addUserInList(User us, List<User> list){
		if(us != null && list != null){
			for(User u:list){
				if(us.getUserId().intValue() == u.getUserId().intValue()){
					return u;
				}
			}
			list.add(us);
		}
		return us;
	}

	public List<User> getUnregisteredUsers() {
		return userService.findUnregisteredUsers();
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

	public void setSelectResponsable(User us){
		this.selectResponsable = us;
	}

	public User getSelectResponsable(){
		return this.selectResponsable;
	}
	public List<User> getUsersByApplicant(){
		return usersByApplicant;
	}

	public void setUsersByApplicant(List<User> list){
		this.usersByApplicant = list;
	}

	public List<ProdApplications> getProdApplicationses() {
		if (prodApplicationses == null && getSelectedApplicant() != null)
			prodApplicationses = applicantService.findRegProductForApplicant(getSelectedApplicant().getApplcntId());

		return prodApplicationses;
	}

	public void setProdApplicationses(List<ProdApplications> prodApplicationses) {
		this.prodApplicationses = prodApplicationses;
	}

	public List<ProdApplications> getProdNotRegApplicationses() {
		if (prodNotRegApplicationses == null && getSelectedApplicant() != null)
			prodNotRegApplicationses = applicantService.findProductNotRegForApplicant(getSelectedApplicant().getApplcntId());

		return prodNotRegApplicationses;
	}

	public void setProdNotRegApplicationses(List<ProdApplications> prodNotRegApplicationses) {
		this.prodNotRegApplicationses = prodNotRegApplicationses;
	}

	public String getSourcePage() {
		return sourcePage;
	}

	public void setSourcePage(String sourcePage) {
		this.sourcePage = sourcePage;
	}

	public RoleDAO getRoleDAO() {
		return roleDAO;
	}

	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}

	public boolean visibleDetailsgroupPnl(){
		if((userSession.isAdmin() || userSession.isStaff() || userSession.isHead())
				&& getSelectedApplicant().getState() != ApplicantState.NEW_APPLICATION)
			return true;

		return false;
	}

	public boolean visibleProdlistPnl(){
		if(getSelectedApplicant().getState() != ApplicantState.NEW_APPLICATION)
			return true;

		return false;
	}

	public boolean visibleRegister(){
		if(userSession.isAdmin() && getSelectedApplicant().getState() == ApplicantState.NEW_APPLICATION)
			return true;
		return false;
	}

	public String publicForm(){//userSession.getLoggedINUserID()
		if(userSession.getLoggedInUser() != null && !"".equals(userSession.getLoggedInUser()))
			return "/internal/processapp.faces";

		return "/public/processapplicant.faces";
	}

	/**
	 * Get agent agreements for current applicant
	 * @return
	 */
	public List<AgentAgreement> getAgentAgreements(){
		return agentAgreements;
	}

	public void setAgentAgreements(List<AgentAgreement> agentAgreements) {
		this.agentAgreements = agentAgreements;
	}
	
	

	public List<AgentAgreement> getMyAgentAgreements() {
		return myAgentAgreements;
	}

	public void setMyAgentAgreements(List<AgentAgreement> myAgentAgreements) {
		this.myAgentAgreements = myAgentAgreements;
	}

	/**
	 * Select this agent
	 * @param agent
	 */
	public void selectAgentAgreement(AgentAgreement agentAgreement){
		setSelectedAgentAgreement(agentAgreement);
		setSelectedAgent(agentAgreement.getAgent());
		setStartDate(agentAgreement.getStart());
		setFinishDate(agentAgreement.getFinish());
	}

	/**
	 * Currently selected agent agreement for edit or new one
	 * @return
	 */
	public AgentAgreement getSelectedAgentAgreement() {
		return selectedAgentAgreement;
	}

	/**
	 * Currently selected agent agreement for edit or new one
	 * @return
	 */
	public void setSelectedAgentAgreement(AgentAgreement selectedAgentAgreement) {
		this.selectedAgentAgreement = selectedAgentAgreement;
	}

	/**
	 * Agent from agent agreement
	 * @return
	 */
	public Applicant getSelectedAgent(){
		/*if(getSelectedAgentAgreement() != null)
			selectedAgent = getSelectedAgentAgreement().getAgent();
		else
			selectedAgent = null;*/
		
		return selectedAgent;
	}
	/**
	 * Agent from agent agreement
	 * @return
	 */
	public void setSelectedAgent(Applicant agent){
		selectedAgent = agent;
		//if(getSelectedAgentAgreement() != null && agent != null){
		//	getSelectedAgentAgreement().setAgent(agent);
		//}
	}

	/**
	 * make suggestions for agents. Currently any applicant may be agent
	 * @param query
	 * @return
	 */
	public List<Applicant> completeAgentList(String query) {
		try {
			List<Applicant> applicants = applicantService.getRegApplicants();
			return JsfUtils.completeSuggestions(query, applicants);
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}
		return null;
	}
	/**
	 * Save currently selected agent agreement
	 */
	public void saveSelectedAgreement(){
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		if(getSelectedAgentAgreement() != null && getSelectedApplicant() != null){
			getSelectedAgentAgreement().setApplicant(getSelectedApplicant());

			String error = varificationSelectAgreement();
			if(error.isEmpty()){
				getApplicantService().saveAgentAgreement(getSelectedAgentAgreement());
				agentAgreements = getApplicantService().fetchAgentAgreements(getSelectedApplicant());
				
				RequestContext.getCurrentInstance().execute("PF('editDialog').hide()");
			}else
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, error, ""));
		}else
			globalFail();
		
		cancelSelectedAgreement();
	}
	
	public void cancelSelectedAgreement(){
		setSelectedAgent(null);
		setStartDate(null);
		setFinishDate(null);
		setSelectedAgentAgreement(null);
	}

	public String varificationSelectAgreement(){
		String err = "";
		if(getSelectedApplicant() != null && getSelectedAgentAgreement() != null && getSelectedAgent() != null){
			if(getSelectedApplicant().getApplcntId().intValue() == getSelectedAgent().getApplcntId().intValue()){
				err = resourceBundle.getString("error_agent");
			}
			Long agentID = getSelectedAgent().getApplcntId();
			if(agentAgreements != null && agentAgreements.size() > 0){
				for(AgentAgreement ag:agentAgreements){
					if(ag.getAgent() != null){
						if(getSelectedAgentAgreement() != null && getSelectedAgentAgreement().getId() != null){ // edit
							if(ag.getId().intValue() != getSelectedAgentAgreement().getId().intValue()){
								if(ag.getAgent().getApplcntId().intValue() == agentID.intValue()){
									err = resourceBundle.getString("error_dublicate_agent");
									break;
								}
							}
						}else{// create new
							if(ag.getAgent().getApplcntId().intValue() == agentID.intValue()){
								err = resourceBundle.getString("error_dublicate_agent");
								break;
							}
						}
					}
				}
			}
			if(err.isEmpty()){
				getSelectedAgentAgreement().setAgent(getSelectedAgent());
			}else{
				setSelectedAgent(getSelectedAgentAgreement().getAgent());
				return err;
			}
			if(getStartDate() != null && getFinishDate() != null){
				if(getStartDate().getTime() >= getFinishDate().getTime()){
					setStartDate(getSelectedAgentAgreement().getStart());
					setFinishDate(getSelectedAgentAgreement().getFinish());
					
					err = resourceBundle.getString("error_date");
					return err;
				}else{
					// set dates in SelectedAgentAgreement
					getSelectedAgentAgreement().setStart(getStartDate());
					getSelectedAgentAgreement().setFinish(getFinishDate());
				}
			}else{
				err = resourceBundle.getString("date_is_mandatory");
				return err;
			}
		}else
			err = resourceBundle.getString("global_fail");
		return err;
	}
	/**
	 * Global failure message
	 */
	public void globalFail() {
		facesContext = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,resourceBundle.getString("global_fail") , ""));
	}

	/**
	 * Create new agent agreement and set it as selected for edit
	 */
	public void createAgentAgreement(){
		setSelectedAgentAgreement(new AgentAgreement());
	}

	/**
	 * Temporary disable agent agreement or enable disabled
	 */
	public void disableAgentAgreement(AgentAgreement agreement){
		if(agreement != null){
			agreement.setActive(!agreement.getActive());
			setSelectedAgentAgreement(agreement);
			saveSelectedAgreement();
		}else{
			globalFail();
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
}
