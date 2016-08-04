package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class AppSelectMBean implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(AppSelectMBean.class);

	@ManagedProperty(value = "#{prodRegAppMbean}")
	ProdRegAppMbean prodRegAppMbean;

	@ManagedProperty(value = "#{globalEntityLists}")
	GlobalEntityLists globalEntityLists;

	@ManagedProperty(value = "#{countryService}")
	CountryService countryService;

	@ManagedProperty(value = "#{applicantService}")
	ApplicantService applicantService;

	@ManagedProperty(value = "#{userService}")
	UserService userService;

	private Applicant selectedApplicant;
	private org.msh.pharmadex.domain.User applicantUser;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

	/** flag by visible panel with info by Applicant */
	private boolean showApp;
	/** flag by visible list users of Applicant */
	private boolean showUserSelect = false;
	/** flag by visible label no Users of Applicant */
	private boolean showLblNoUsers = false;
	/** flag by visible btn Save */
	private boolean showSaveBtn = false;

	private List<UserDTO> users;
	private UserDTO responsable;
	private UserDTO selectedUser;

	@PostConstruct
	public void init() {
		System.out.println("Initialize AppSelectMBean");
		cancelAddApplicant();
	}

	@PreDestroy
	public void destroy() {
		System.out.println("Destroy bean");
	}

	public void gmpChangeListener() {
		try {
			if (selectedApplicant != null){
				if(selectedApplicant.getApplcntId() != null) {
					selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
					showApp = true;
					convertUser(selectedApplicant.getUsers());
					// 1) no Responsable && no Users - showLbl GOTO admin
					// 2) no Responsable && list Users - choose user from list Users
					// 3) Responsable && no Users - user=responsable
					// 4) Responsable && list Users - choose user from list Users


					setShowUserSelect(users.size() > 0);
					setShowLblNoUsers(!(users.size() > 0));
					selectedUser = responsable;
					showSaveBtn = (selectedUser != null);
				}
			}else{
				setShowUserSelect(false);
				setShowLblNoUsers(false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Error", ex.getMessage()));
		}
	}

	private void convertUser(List<org.msh.pharmadex.domain.User> list) {
		String respName = selectedApplicant != null ? selectedApplicant.getContactName():"";
		org.msh.pharmadex.domain.User resp = userService.findUserByUsername(respName);
		responsable = resp != null ? new UserDTO(resp):null;

		this.users = new ArrayList<UserDTO>();
		for (org.msh.pharmadex.domain.User u : list) {
			/* 04082016 Issue Bug #1929
			if(u.isEnabled())
			*/
			this.users.add(new UserDTO(u));
		}
		addUserInList();
	}

	private void addUserInList(){
		if(responsable != null){
			boolean contains = false;
			for(UserDTO us:users){
				if(responsable.getUserId().intValue() == us.getUserId().intValue()){
					contains = true;
					break;
				}
			}
			if(!contains)
				users.add(responsable);
		}
	}

	public void appChangeListenener(SelectEvent event) {
		logger.error("inside appChangeListenener");
		logger.error("Selected company is " + selectedApplicant.getAppName());
		logger.error("event " + event.getObject());
		gmpChangeListener();
	}

	public void onRowSelect(SelectEvent event) {
		FacesMessage msg = new FacesMessage("User Selected", ((UserDTO) event.getObject()).getUsername());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		logger.error("Selected User is " + ((UserDTO) event.getObject()).getUsername());
		showSaveBtn = (selectedUser != null);
	}
	
	public void validate(ComponentSystemEvent e) {
		if(selectedUser == null){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Error selected user."));
			fc.renderResponse();
		}
	}

	public void onRowUnselect(UnselectEvent event) {
		FacesMessage msg = new FacesMessage("Car Unselected", ((Applicant) event.getObject()).getAppName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}


	public void appChangeListenener(AjaxBehaviorEvent event) {
		logger.error("inside appChangeListenener");
		//        logger.error("Selected company is " + selectedApplicant.getAppName());
		logger.error("event " + event.getSource());
		gmpChangeListener();


	}

	public String addApptoRegistration() {
		try{
			selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
			showSaveBtn = (selectedUser != null);
			if (selectedUser == null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Person responsible cannot be empty."));
				FacesContext.getCurrentInstance().renderResponse();
				return null;
			} else {
				applicantUser = userService.findUser(selectedUser.getUserId());
				prodRegAppMbean.setApplicant(selectedApplicant);
				//prodRegAppMbean.getApplicant().setContactName(applicantUser.getUsername());
				prodRegAppMbean.setApplicantUser(applicantUser);
				prodRegAppMbean.getProdApplications().setApplicantUser(applicantUser);
				prodRegAppMbean.getProdApplications().setApplicant(selectedApplicant);
			}
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}

		return "";
	}

	public void cancelAddApplicant() {
		selectedApplicant = new Applicant();
		applicantUser = new org.msh.pharmadex.domain.User();
		selectedUser = null;
		this.users = new ArrayList<UserDTO>();
		setShowLblNoUsers(false);
		setShowUserSelect(false);
		setShowSaveBtn(false);
		setShowApp(false);
	}

	/**
	 * show only REGISTER Applicants
	 */
	public List<Applicant> completeApplicantList(String query) {
		try {
			List<Applicant> applicants = applicantService.getRegApplicants();
			return JsfUtils.completeSuggestions(query, applicants);
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}
		return null;
	}

	public boolean isShowApp() {
		return showApp;
	}

	public void setShowApp(boolean showApp) {
		this.showApp = showApp;
	}

	public boolean isShowUserSelect() {
		return showUserSelect;
	}

	public void setShowUserSelect(boolean showUserSelect) {
		this.showUserSelect = showUserSelect;
	}

	public boolean isShowLblNoUsers() {
		return showLblNoUsers;
	}

	public void setShowLblNoUsers(boolean showLblNoUSers) {
		this.showLblNoUsers = showLblNoUSers;
	}

	public boolean isShowSaveBtn() {
		return showSaveBtn;
	}

	public void setShowSaveBtn(boolean showSaveBtn) {
		this.showSaveBtn = showSaveBtn;
	}
	
	public Applicant getSelectedApplicant() {
		return selectedApplicant;
	}

	public void setSelectedApplicant(Applicant selectedApplicant) {
		this.selectedApplicant = selectedApplicant;
	}

	public org.msh.pharmadex.domain.User getApplicantUser() {
		return applicantUser;
	}

	public void setApplicantUser(org.msh.pharmadex.domain.User applicantUser) {
		this.applicantUser = applicantUser;
	}

	public List<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

	public UserDTO getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(UserDTO selectedUser) {
		this.selectedUser = selectedUser;
	}

	public GlobalEntityLists getGlobalEntityLists() {
		return globalEntityLists;
	}

	public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
		this.globalEntityLists = globalEntityLists;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
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

	public ProdRegAppMbean getProdRegAppMbean() {
		return prodRegAppMbean;
	}

	public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
		this.prodRegAppMbean = prodRegAppMbean;
	}
}
