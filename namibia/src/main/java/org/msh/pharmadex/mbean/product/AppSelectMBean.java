package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.AgentAgreement;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.Scrooge;
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

	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;

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

	@ManagedProperty(value = "#{productService}")
	ProductService productService;

	@ManagedProperty(value = "#{prodApplicationsService}")
	ProdApplicationsService prodApplicationsService;

	@ManagedProperty(value = "#{productDAO}")
	private ProductDAO productDAO;

	private Applicant selectedApplicant;
	private org.msh.pharmadex.domain.User applicantUser;
	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	private User curUser;
	private Product selectedProduct;
	private ProdApplications prodappl;

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

	private ProdAppType prodAppType;
	private ProdTable prodTable;
	private boolean showProductChoice;

	@PostConstruct
	public void init() {
		System.out.println("Initialize AppSelectMBean");
		prodAppType=prodRegAppMbean.getProdApplications().getProdAppType();
		curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
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

	// when save pressed
	public String addApptoRegistration() {
		try{
			selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
			showSaveBtn = (selectedUser != null);
			if (selectedUser == null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Person responsible cannot be empty."));
				FacesContext.getCurrentInstance().renderResponse();
				return null;
			} else {
				if (prodTable!=null) {
					selectedProduct = productDAO.findProduct(prodTable.getId());
					prodappl = prodApplicationsService.findActiveProdAppByProd(selectedProduct.getId());
					prodRegAppMbean.setProduct(selectedProduct);
					//prodRegAppMbean.setProdApplications(prodappl);
					ProdApplications newAppl = startReregVar(prodAppType, prodappl.getId(), userSession.getProdAppInit());
					prodRegAppMbean.setProdApplications(newAppl);

				}
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
	//save for prod	
	public void addProdToRegistration() {
		try{
			if (prodTable != null) {
				selectedProduct = productDAO.findProduct(prodTable.getId());
				prodappl = prodApplicationsService.findActiveProdAppByProd(selectedProduct.getId());
				prodRegAppMbean.setProduct(selectedProduct);
				//prodRegAppMbean.setProdApplications(prodappl);
				ProdApplications newAppl = startReregVar(prodAppType, prodappl.getId(), userSession.getProdAppInit());
				prodRegAppMbean.setProdApplications(newAppl);
			}
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}

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
	 * 
	 */
	public List<Applicant> completeApplicantList(String query) {
		try {
			List<Applicant> applicants = new ArrayList<Applicant>();
			Applicant curApplicant = curUser.getApplicant();
			if(userSession.isResponsible(curApplicant)){
				List<AgentAgreement> agreements = new ArrayList<AgentAgreement>();

				if(curApplicant != null && curApplicant.getApplcntId() != null && curApplicant.getApplcntId() > 0)
					agreements = getApplicantService().fetchAgentAgreements(curApplicant);

				applicants.add(curApplicant);
				if(agreements != null && agreements.size() > 0){
					Date today = new Date();
					for(AgentAgreement aa:agreements){
						if(aa.getActive() && today.after(aa.getStart()) && today.before(aa.getFinish()))
							applicants.add(aa.getAgent());
					}
				}
			}else{
				if(userSession.isStaff()){
					applicants.addAll(applicantService.getRegApplicants());
				}else if(userSession.isCompany() && curApplicant != null && curApplicant.getApplcntId() > 0){
					applicants.add(curApplicant);
				}
			}

			return JsfUtils.completeSuggestions(query, applicants);
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}
		return null;
	}

	/**
	 * show only 
	 */
	public List<Applicant> completeAgentList(String query) {
		try {
			List<AgentAgreement> agreements = new ArrayList<AgentAgreement>();
			List<Applicant> agents = new ArrayList<Applicant>();

			Applicant curApplicant = curUser.getApplicant();
			if(curApplicant != null && curApplicant.getApplcntId() != null && curApplicant.getApplcntId() > 0)
				agreements = getApplicantService().fetchAgentAgreements(curApplicant);

			agents.add(curApplicant);
			if(agreements != null && agreements.size() > 0){
				Date today = new Date();
				for(AgentAgreement aa:agreements){
					if(aa.getActive() && today.after(aa.getStart()) && today.before(aa.getFinish()))
						agents.add(aa.getAgent());
				}
			}

			return JsfUtils.completeSuggestions(query, agents);
		} catch (Exception ex){
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
		}
		return null;
	}

	private ProdTable createProdTableRecord(Product p){
		ProdTable pt = new ProdTable();
		pt.setId(p.getId());
		pt.setManufName(p.getManufName());
		pt.setGenName(p.getGenName());
		pt.setProdName(p.getProdName());
		pt.setProdCategory(p.getProdCategory());
		List<ProdApplications> apps = p.getProdApplicationses();
		ProdApplications pa = apps.get(0);
		pt.setProdAppID(pa.getId());
		pt.setRegDate(pa.getRegistrationDate());
		pt.setRegExpiryDate(pa.getRegExpiryDate());
		pt.setRegNo(pa.getProdRegNo());
		pt.setProdDesc(p.getProdDesc());
		pt.setAppName(pa.getApplicant().getAppName());
		return pt;
	}
	private ProdApplications getLastProductApplication(Product p){
		List<ProdApplications> prodApps = p.getProdApplicationses();
		if (prodApps==null) return null;
		for(ProdApplications pa:prodApps){
			if (pa.getRegState().equals(RegState.REGISTERED))
				return pa;
		}
		return null;
	}

	public List<ProdTable> completeProduct(String query) {
		List<ProdTable> suggestions = new ArrayList<ProdTable>();
		List<ProdTable> prods;

		if (selectedApplicant == null){
			selectedApplicant= prodRegAppMbean.getApplicant();
		}
		List<ProdApplications> paList;
		Long a=selectedApplicant.getApplcntId();
		paList=applicantService.findRegProductForApplicant(a);
		Calendar minDate = Calendar.getInstance();
		minDate.add(Calendar.DAY_OF_YEAR,120);

		if (paList==null) return suggestions;

		for (ProdApplications pa : paList) {
			if (pa!=null) {
				if (prodAppType == ProdAppType.RENEW) {
					//include product to list only if expiration time have not came and no more 120 days before
					if (pa.getRegExpiryDate() != null) {
						if (!(pa.getRegExpiryDate().before(minDate.getTime()) && (pa.getRegExpiryDate().after(Calendar.getInstance().getTime())))) {
							continue;
						}
					}
				} else if (prodAppType == ProdAppType.VARIATION) {
					//if product is expired - ommit it, check next
					if (pa.getRegExpiryDate() != null) {
						if (pa.getRegExpiryDate().before(Calendar.getInstance().getTime())) {
							continue;
						}
					}
				}

				if (" ".equals(query))
					suggestions.add(createProdTableRecord(pa.getProduct()));
				else {
					Product p=pa.getProduct();
					if (p.getProdName() != null) {
						if (p.getProdName().toLowerCase().startsWith(query)) {
							suggestions.add(createProdTableRecord(p));
							continue;
						}
					}
					if (p.getGenName() != null) {
						if (p.getGenName().toLowerCase().startsWith(query)) {
							suggestions.add(createProdTableRecord(p));
						}
					}
				}
			}
		}

		return suggestions;
	}

	public void onItemChange(AjaxBehaviorEvent event){
		if (event.getSource() instanceof ProdTable){
			ProdTable prodTableCh = (ProdTable) event.getSource();
		}
	}

	public void onItemSelect(SelectEvent event) {
		if(event.getObject() instanceof ProdTable){
			ProdTable prodTableCh = (ProdTable) event.getObject();
			setProdTable(prodTableCh);
			if (prodTable!=null) {
				selectedProduct = productDAO.findProduct(prodTable.getId());
				prodappl = prodApplicationsService.findActiveProdAppByProd(selectedProduct.getId());
				//prodRegAppMbean.setProduct(selectedProduct);
				//prodRegAppMbean.setProdApplications(prodappl);
				//showSaveBtn=true;
				//RequestContext.getCurrentInstance().update("@form @this :reghome:prod");
				//ProdApplications newAppl = startReregVar(prodAppType, prodappl.getId(), userSession.getProdAppInit());
				//selectedProduct = productDAO.findProduct(prodTable.getId());
				//prodappl = prodApplicationsService.findActiveProdAppByProd(selectedProduct.getId());
				//prodRegAppMbean.setProduct(selectedProduct);
				//prodRegAppMbean.setProdApplications(prodappl);
				ProdApplications newAppl = startReregVar(prodAppType, prodappl.getId(), userSession.getProdAppInit());
				prodRegAppMbean.setProdApplications(newAppl);
				prodRegAppMbean.setProduct(newAppl.getProduct());
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", prodTable.getProdName()));
		}
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
	public boolean isShowProductChoice() {
		showProductChoice = ((prodAppType==ProdAppType.VARIATION) || (prodAppType==ProdAppType.RENEW)) && (prodRegAppMbean.getProduct().getId()==null);
		return showProductChoice;
	}

	public boolean isShowChooseApplicant(){
		boolean show = false;
		if(userSession.isCompany() || userSession.isStaff()){
			if(prodRegAppMbean.getProdApplications() != null){
				if(prodRegAppMbean.getProdApplications().getId() != null && prodRegAppMbean.getProdApplications().getId() > 0){
					return false; // show save product with applicant
				}else{ // new registration
					if(prodRegAppMbean.getProdApplications().getApplicant() != null && 
							prodRegAppMbean.getProdApplications().getApplicant().getApplcntId() != null &&
							prodRegAppMbean.getProdApplications().getApplicant().getApplcntId() > 0)
						return false;
					else
						return true;
				}
			}
		}
		return show;
	}

	public ProdApplications startReregVar(ProdAppType newtype, Long parentAppId, ProdAppInit paInit){
		//create copy of inital application and product
		ProdApplications prodAppRenew;
		ProdApplications prodApp = prodApplicationsService.findProdApplications(parentAppId);
		prodApp.setMjVarQnt(paInit.getMjVarQnt());
		prodApp.setMnVarQnt(paInit.getMnVarQnt());
		prodAppRenew=clone(prodApp,newtype,false);
		prodAppRenew.setProdAppDetails(paInit.getVarSummary());
		prodAppRenew.setPrescreenfeeAmt(paInit.getPrescreenfee());
		prodAppRenew.setFeeAmt(paInit.getFee());
		prodAppRenew.setFeeReceived(false);
		prodAppRenew.setFeeSubmittedDt(null);
		prodAppRenew.setBankName(null);
		prodAppRenew.setPrescreenBankName(null);
		prodAppRenew.setDosRecDate(null);
		prodAppRenew.setRegExpiryDate(null);
		prodAppRenew.setProdRegNo(null);
		prodAppRenew.setProdAppNo(null);
		prodAppRenew.setFeeReceipt(null);
		prodAppRenew.setReceiptNo(null);
		prodAppRenew.setPrescreenReceiptNo(null);
		prodAppRenew.setParentApplication(prodApp);
		prodAppRenew.setCreatedBy(curUser);
		//prodAppRenew = prodApplicationsService.saveApplication(prodAppRenew,curUser.getUserId());

		return prodAppRenew;
	}

	private ProdApplications clone(ProdApplications src, ProdAppType type, boolean isMajor) {
		ProdApplications paNew = new ProdApplications();
		paNew.setDosRecDate(src.getDosRecDate());
		Long parentProdId = src.getProduct().getId();
		Scrooge.copyData(src, paNew);
		paNew.setId((long) 0);
		paNew.setActive(false);
		paNew.setProdAppType(type);
		paNew.setRegState(RegState.SAVED);
		paNew.setProdRegNo("");
		paNew.setProdAppNo("");
		paNew.setRegistrationDate(null);
		Product p = new Product();
		Product pp = productDAO.findProductEager(parentProdId);
		p.setManufName(pp.getManufName());
		p.setDosUnit(pp.getDosUnit());
		p.setAdminRoute(pp.getAdminRoute());
		p.setAgeGroup(pp.getAgeGroup());
		p.setContType(pp.getContType());
		p.setDosForm(pp.getDosForm());
		p.setDosStrength(pp.getDosStrength());
		p.setApprvdName(null);
		p.setDrugType(pp.getDrugType());
		p.setGenName(pp.getGenName());
		p.setIndications(pp.getIndications());
		p.setNewChemicalEntity(false);
		p.setNewChemicalName(null);
		p.setPackSize(pp.getPackSize());
		p.setPharmacopeiaStds(pp.getPharmacopeiaStds());
		p.setPharmClassif(pp.getPharmClassif());
		p.setDrugType(pp.getDrugType());
		p.setFnm(pp.getFnm());
		p.setIngrdStatment(pp.getIngrdStatment());
		p.setPosology(pp.getPosology());
		p.setProdCategory(pp.getProdCategory());
		p.setProdDesc(pp.getProdDesc());
		p.setProdName(pp.getProdName());
		p.setProdType(pp.getProdType());
		p.setShelfLife(pp.getShelfLife());
		p.setStorageCndtn(pp.getStorageCndtn());
		p.setUseCategories(pp.getUseCategories());
		paNew.setProduct(p);

		List<Atc> atcs = pp.getAtcs();
		List<Atc> atcsNew = null;
		if (atcs != null && atcs.size() > 0){
			atcsNew = new ArrayList<Atc>();
			for (int i = 0; i < atcs.size(); i++) {
				Atc atc = new Atc();
				Atc exist = atcs.get(i);
				Scrooge.copyData(exist, atc);
				atcsNew.add(atc);
			}
		}
		p.setAtcs(atcsNew);

		List<ProdCompany> cmpns = pp.getProdCompanies();
		List<ProdCompany> cmpnsNew=null;
		if (cmpns!=null&&cmpns.size()>0){
			cmpnsNew = new ArrayList<ProdCompany>();
			for(int i=0;i<cmpns.size();i++){
				ProdCompany company = new ProdCompany();
				Scrooge.copyData(cmpns.get(i),company);
				company.setProduct(p);
				cmpnsNew.add(company);
			}
		}
		p.setProdCompanies(cmpnsNew);

		List<ProdExcipient> excs = pp.getExcipients();
		List<ProdExcipient> excsNew=new ArrayList<ProdExcipient>();
		if (excs!=null&&excs.size()>0){
			for(int i=0;i<excs.size();i++){
				ProdExcipient exc = excs.get(i);
				ProdExcipient excNew = new ProdExcipient();
				Scrooge.copyData(exc,excNew);
				excNew.setProduct(p);
				excNew.setId(null);
				excsNew.add(excNew);
			}
		}
		p.setExcipients(excs);

		List<ProdInn> inns = pp.getInns();
		List<ProdInn> innsNew=new ArrayList<ProdInn>();
		if (inns!=null&&inns.size()>0) {
			for(int i=0; i<inns.size();i++){
				ProdInn inn = inns.get(i);
				ProdInn innNew = new ProdInn();
				Scrooge.copyData(inn,innNew);
				innNew.setId(null);
				innNew.setProduct(p);
				innsNew.add(innNew);
			}
		}
		p.setInns(innsNew);

		return paNew;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProdTable getProdTable() {
		return prodTable;
	}

	public void setProdTable(ProdTable prodTable) {
		this.prodTable = prodTable;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public ProdApplicationsService getProdApplicationsService() {
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
		this.prodApplicationsService = prodApplicationsService;
	}

	public User getCurUser() {
		return curUser;
	}

	public void setCurUser(User curUser) {
		this.curUser = curUser;
	}

	public ProductDAO getProductDAO() {
		return productDAO;
	}

	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public ProdAppType getProdAppType() {
		return prodAppType;
	}

	public void setProdAppType(ProdAppType prodAppType) {
		this.prodAppType = prodAppType;
	}
}
