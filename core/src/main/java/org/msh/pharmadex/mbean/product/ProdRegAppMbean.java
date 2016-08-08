package org.msh.pharmadex.mbean.product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.AdminRoute;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.DrugPrice;
import org.msh.pharmadex.domain.ForeignAppStatus;
import org.msh.pharmadex.domain.PharmClassif;
import org.msh.pharmadex.domain.Pricing;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.InnService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.ReportService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import com.mysql.jdbc.PacketTooBigException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Author: usrivastava
 * Update: Odissey
 */
@ManagedBean
@ViewScoped
public class ProdRegAppMbean implements Serializable {
	FacesContext context = FacesContext.getCurrentInstance();
	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
	List<UseCategory> useCategories;
	private Logger logger = LoggerFactory.getLogger(ProdRegAppMbean.class);
	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
	@ManagedProperty(value = "#{applicantService}")
	private ApplicantService applicantService;
	@ManagedProperty(value = "#{productService}")
	private ProductService productService;
	@ManagedProperty(value = "#{innService}")
	private InnService innService;
	@ManagedProperty(value = "#{prodApplicationsService}")
	private ProdApplicationsService prodApplicationsService;
	@ManagedProperty(value = "#{globalEntityLists}")
	private GlobalEntityLists globalEntityLists;
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	@ManagedProperty(value = "#{checklistService}")
	private ChecklistService checklistService;
	@ManagedProperty(value = "#{reportService}")
	private ReportService reportService;
	@ManagedProperty(value = "#{companyService}")
	private CompanyService companyService;
	@ManagedProperty(value = "#{timelineService}")
	private TimelineService timelineService;
	@ManagedProperty(value = "#{attachmentDAO}")
	private AttachmentDAO attachmentDAO;

	private List<ProdInn> selectedInns;
	private List<ProdExcipient> selectedExipients;
	private List<Atc> selectedAtcs;
	private List<ProdAppChecklist> prodAppChecklists;
	private List<ProdCompany> companies;
	private List<ForeignAppStatus> foreignAppStatuses;
	private List<DrugPrice> drugPrices;
	private Applicant applicant;
	private Product product;
	private ProdApplications prodApplications;
	private User applicantUser;
	private User loggedInUser;
	private boolean showNCE;
	private String password;
	private PharmClassif selectedPharmClassif;
	private Pricing pricing;
	private boolean showDrugPrice;
	private JasperPrint jasperPrint;
	private Attachment attachment;
	private List<Attachment> attachments;
	private UploadedFile file;
	private UploadedFile payReceipt;
	private UploadedFile clinicalReview;
	private boolean showfull;
	private String appType;


	@PostConstruct
	private void init() {
		Long prodAppID;
		try {
			//prodAppID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("prodAppID"));
			prodAppID = getParam("prodAppID");
		} catch (NumberFormatException nfe) {
			prodAppID = null;
		}
		if (prodAppID == null) {
			ProdAppInit prodApp = userSession.getProdAppInit();
			if (prodApp != null) {
				product = new Product();
				prodApplications = new ProdApplications(product);
				prodApplications.setProdAppType(prodApp.getProdAppType());
				prodApplications.setSra(prodApp.isSRA());
				prodApplications.setFastrack(prodApp.isEml());
				prodApplications.setFeeAmt(prodApp.getFee());
				prodApplications.setPrescreenfeeAmt(prodApp.getPrescreenfee());

				if (prodApp.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY))
					product.setNewChemicalEntity(true);

				//Initialize associated product entities
				product.setDosForm(new DosageForm());
				product.setDosUnit(new DosUom());
				product.setAdminRoute(new AdminRoute());

				//being a new application. set regstate as saved
				prodApplications.setRegState(RegState.SAVED);


				//Initialize Inns
				selectedInns = new ArrayList<ProdInn>();
				product.setInns(selectedInns);
				//Initialize Excipients
				selectedExipients = new ArrayList<ProdExcipient>();
				product.setExcipients(selectedExipients);
				//Initialize Atcs if null
				selectedAtcs = new ArrayList<Atc>();
				product.setAtcs(selectedAtcs);
				//initialize pricing
				drugPrices = new ArrayList<DrugPrice>();
				pricing = new Pricing(drugPrices, product);
				product.setPricing(pricing);

				// responsable User in prodApplications - prodApplications.getApplicantUser()
				applicantUser = prodApplications.getApplicantUser();

				if(applicantUser == null)
					applicantUser = prodApplications.getCreatedBy();
				if(applicantUser == null)
					applicantUser = getLoggedInUser();

				prodApplications.setApplicant(applicantUser.getApplicant());
				prodApplications.setApplicantUser(applicantUser);

				prodApplications.setCreatedBy(getLoggedInUser());
			}
		} else {
			initProdApps(prodAppID);
		}
		if (prodApplications!=null)
			if(prodApplications.getcRevAttach() == null)
				prepareUpload();
	}

	@PreDestroy
	private void destroyBn() {
		System.out.println("--------------------------------------");
		System.out.println("------ProdRegAppMbean Bean destroyed-----");
		System.out.println("--------------------------------------");
	}
	private Long getParam(String parameter){
		context = FacesContext.getCurrentInstance();
		String procId=null;
		if (context.getExternalContext().getFlash()!=null){
			Object id = context.getExternalContext().getFlash().get(parameter);
			String name="";
			if (id!=null)
				name = id.getClass().getName();
			if (name.toLowerCase().endsWith("long"))
				return (Long) context.getExternalContext().getFlash().get(parameter);
			else if (name.toLowerCase().endsWith("string"))
				procId = (String) context.getExternalContext().getFlash().get(parameter);
		}
		if (procId==null){
			if (context.getExternalContext().getRequestParameterMap()!=null)
				procId = context.getExternalContext().getRequestParameterMap().get(parameter);
		}
		if (procId!=null){
			return Long.parseLong(procId);
		}
		return null;
	}

	public void PDF() {
		context = FacesContext.getCurrentInstance();
		try {
			jasperPrint = reportService.reportinit(prodApplications);
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
			httpServletResponse.setContentType("application/pdf");
			javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
			net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
			FacesContext.getCurrentInstance().responseComplete();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "regHomeMbean", null);
		} catch (JRException e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
		} catch (IOException e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
	}

	public void prepareUpload() {
		attachment = new Attachment();
		attachment.setUpdatedDate(new Date());
		attachment.setProdApplications(prodApplications);
		attachment.setRegState(RegState.SAVED);
	}

	public void handleFileUpload(FileUploadEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		try {

			file = event.getFile();
			if(attachment != null){
				attachment.setFile(IOUtils.toByteArray(file.getInputstream()));
				attachment.setProdApplications(prodApplications);
				attachment.setFileName(file.getFileName());
				attachment.setContentType(file.getContentType());
				attachment.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
				attachment.setRegState(prodApplications.getRegState());
			}
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
			facesContext.addMessage(null, msg);
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
	}

	public void handlePayReceiptUpload() {
		FacesMessage msg = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

		try {
			if (payReceipt != null) {
				msg = new FacesMessage(bundle.getString("global.success"), payReceipt.getFileName() + bundle.getString("upload_success"));
				facesContext.addMessage(null, msg);
				try {
					prodApplications.setFeeReceipt(IOUtils.toByteArray(payReceipt.getInputstream()));

				} catch (IOException e) {
					msg = new FacesMessage(bundle.getString("global_fail"), payReceipt.getFileName() + bundle.getString("upload_fail"));
					facesContext.addMessage(null, msg);
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			} else {
				msg = new FacesMessage(bundle.getString("global_fail"), payReceipt.getFileName() + bundle.getString("upload_fail"));
				facesContext.addMessage(null, msg);
			}
		} catch (Exception ex) {
			if (ex instanceof PacketTooBigException) {
				msg = new FacesMessage("Upload file size is too big!!");
			} else {
				msg = new FacesMessage("Error uploading file");
			}
			facesContext.addMessage(null, msg);
		}

	}

	public void handleClinicalReviewUpload() {
		FacesMessage msg;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

		try {
			if (clinicalReview != null) {
				msg = new FacesMessage(bundle.getString("global.success"), clinicalReview.getFileName() + bundle.getString("upload_success"));
				facesContext.addMessage(null, msg);
				try {
					prodApplications.setClinicalReview(IOUtils.toByteArray(clinicalReview.getInputstream()));
					saveApp();

				} catch (IOException e) {
					msg = new FacesMessage(bundle.getString("global_fail"), clinicalReview.getFileName() + bundle.getString("upload_fail"));
					FacesContext.getCurrentInstance().addMessage(null, msg);
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			} else {
				msg = new FacesMessage(bundle.getString("global_fail"), clinicalReview.getFileName() + bundle.getString("upload_fail"));
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

	}

	public StreamedContent fileDownload() {
		FacesMessage msg;
		// FacesContext facesContext = FacesContext.getCurrentInstance();
		// java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		try {
			byte[] file1 = prodApplications.getFeeReceipt();
			if(file != null){
				InputStream ist = new ByteArrayInputStream(file1);
				StreamedContent download = new DefaultStreamedContent(ist);

				return download;
			}else {
				msg = new FacesMessage("Error no file for download");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return null;
			}
		} catch (Exception ex) {
			msg = new FacesMessage("Error downloading the file");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}
	}


	public void addDocument() {
		FacesMessage msg;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			if(attachment != null && attachment.getFile() != null){
				int len = attachment.getFile().length;
				if(len > 4194304){// в БД используем LONGBLOB = 4194304 - maximum
					msg = new FacesMessage("Error! " + bundle.getString("Error.bigSizeFile"));
					facesContext.addMessage(null, msg);
				}else{
					attachmentDAO.save(attachment);
					setAttachments(null);
					msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
					facesContext.addMessage(null, msg);
				}
			}
		} catch (Exception ex) {
			if (ex instanceof PacketTooBigException) {
				msg = new FacesMessage("Error! " + bundle.getString("Error.bigSizeFile"));
				//msg = new FacesMessage("Upload file size is too big!!");
			} else {
				msg = new FacesMessage("Error! " + bundle.getString("Error.uploadFile"));
				// msg = new FacesMessage("Error uploading file");
			}
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	public void deleteDoc(Attachment attach) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		try {

			FacesMessage msg = new FacesMessage(resourceBundle.getString("global_delete"), attach.getFileName() + resourceBundle.getString("is_deleted"));
			attachmentDAO.delete(attach);
			attachments = null;
			facesContext.addMessage(null, msg);
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), attach.getFileName() + resourceBundle.getString("cannot_delte"));
			facesContext.addMessage(null, msg);
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}


	}


	//fires everytime you click on next or prev button on the wizard
	public String onFlowProcess(FlowEvent event) {
		context = FacesContext.getCurrentInstance();
		String currentWizardStep = event.getOldStep();
		String nextWizardStep = event.getNewStep();
		try {
			initializeNewApp(nextWizardStep);
			if (currentWizardStep.equals("prodreg")) {
				if (applicant == null || applicant.getApplcntId()==null) {
					FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant not selected", "Select an Applicant.");
					context.addMessage(null, msg1);
					nextWizardStep = currentWizardStep; // keep wizard on current step if error
				}
				if (applicantUser == null) {
					FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant user not selected", "Select person responsible for the application");
					context.addMessage(null, msg2);
					nextWizardStep = currentWizardStep; // keep wizard on current step if error
				}

			}
			if (!currentWizardStep.equals("prodreg")){
				saveApp();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
			context.addMessage(null, msg);
			nextWizardStep = currentWizardStep; // keep wizard on current step if error
		}
		return nextWizardStep; // return new step if all ok
	}

	//Used to initialize field values only for new applications. For saved applications the values are assigned in setprodapplications
	public void initializeNewApp(String currentWizardStep) {
		if (currentWizardStep.equals("prodreg") && product.getId() == null) {
		} else if (currentWizardStep.equals("proddetails")) {

		} else if (currentWizardStep.equals("appdetails")) {

		} else if (currentWizardStep.equals("applicationStatus")) {

		} else if (currentWizardStep.equals("manufdetail")) {
			product.setProdCompanies(companyService.findCompanyByProdID(product.getId()));

		} else if (currentWizardStep.equals("pricing")) {
			RetObject retObject = productService.findDrugPriceByProd(product.getId());
			if (retObject.getMsg().equals("persist")) {
				pricing = (Pricing) retObject.getObj();
				if (pricing == null)
					pricing = new Pricing(drugPrices, product);
				product.setPricing(pricing);
			} else {
				FacesMessage msg = new FacesMessage(bundle.getString("global_fail"), retObject.getMsg());
				context.addMessage(null, msg);
			}
		} else if (currentWizardStep.equals("attach")) {
			if (prodAppChecklists != null && prodAppChecklists.size() > 0)
				prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
		} else if (currentWizardStep.equals("prodAppChecklist")) {
			prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
			if (prodAppChecklists != null && prodAppChecklists.size() < 1) {
				prodAppChecklists = new ArrayList<ProdAppChecklist>();
				List<Checklist> allChecklist = null;

				if (prodApplications.getMjVarQnt()>0)//if exists major variation, show it on this tab
					allChecklist = checklistService.getETChecklists(prodApplications, true);
				else//there isn't major - show minor check list here
					allChecklist = checklistService.getETChecklists(prodApplications, false);
				if (allChecklist==null) return;
				if (allChecklist.size()==0) return;
				ProdAppChecklist eachProdAppCheck;
				if (allChecklist != null && allChecklist.size() > 0) {
					for (int i = 0; allChecklist.size() > i; i++) {
						eachProdAppCheck = new ProdAppChecklist();
						eachProdAppCheck.setChecklist(allChecklist.get(i));
						eachProdAppCheck.setProdApplications(prodApplications);
						prodAppChecklists.add(eachProdAppCheck);
					}
				}
			}
		} else if (currentWizardStep.equals("prodAddAppChecklist")) {
			prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
			if (prodAppChecklists != null && prodAppChecklists.size() < 1) {
				prodAppChecklists = new ArrayList<ProdAppChecklist>();
				List<Checklist> allChecklist = null;
				//if exists both major and minor variation, show minor app on this tab
				if (prodApplications.getMjVarQnt() > 0 && prodApplications.getMnVarQnt() > 0)
					allChecklist = checklistService.getETChecklists(prodApplications, false);
				if (allChecklist == null) return;
				if (allChecklist.size() == 0) return;
				ProdAppChecklist eachProdAppCheck;
				if (allChecklist != null && allChecklist.size() > 0) {
					for (int i = 0; allChecklist.size() > i; i++) {
						eachProdAppCheck = new ProdAppChecklist();
						eachProdAppCheck.setChecklist(allChecklist.get(i));
						eachProdAppCheck.setProdApplications(prodApplications);
						prodAppChecklists.add(eachProdAppCheck);
					}
				}
			}
		} else if (currentWizardStep.equals("appointment")) {

		} else if (currentWizardStep.equals("summary")) {
			if (prodAppChecklists != null){
				prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
			}
		}

	}


	@Transactional
	public void saveApp() {
		context = FacesContext.getCurrentInstance();
		product.setUseCategories(useCategories);	
		try {
			if (prodAppChecklists != null){
				prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
			}
			RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, userSession.getLoggedINUserID());
			if (retObject.getMsg().equals("persist")) {
				prodApplications = (ProdApplications) retObject.getObj();
				setFieldValues();
				context.addMessage(null, new FacesMessage(bundle.getString("app_save_success")));
			} else {
				String err = bundle.getString("save_app_error") + " " + bundle.getString("Error." + retObject.getMsg());
				context.addMessage(null, new FacesMessage(err));
			}
		} catch (Exception e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
		}

	}

	public String removeInn(ProdInn prodInn) {
		context = FacesContext.getCurrentInstance();
		try {
			selectedInns.remove(prodInn);
			innService.removeProdInn(prodInn);
			context.addMessage(null, new FacesMessage(bundle.getString("inn_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
		return null;
	}

	public String removeExcipient(ProdExcipient prodExcipient) {
		context = FacesContext.getCurrentInstance();
		try {
			selectedExipients.remove(prodExcipient);
			innService.removeExcipient(prodExcipient);
			context.addMessage(null, new FacesMessage(bundle.getString("expnt_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

		return null;
	}

	public String removeAtc(Atc atc) {
		context = FacesContext.getCurrentInstance();
		try {
			selectedAtcs.remove(atc);
			context.addMessage(null, new FacesMessage(bundle.getString("atc_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
		return null;
	}

	public String removeDrugPrice(DrugPrice drugPrice) {
		context = FacesContext.getCurrentInstance();
		try {
			drugPrices.remove(drugPrice);
			context.addMessage(null, new FacesMessage(bundle.getString("drugprice_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
		return null;
	}

	public void removeCompany(ProdCompany selectedCompany) {
		context = FacesContext.getCurrentInstance();
		try {
			companies.remove(selectedCompany);
			companyService.removeProdCompany(selectedCompany);

			context.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

	}

	public void removeAppStatus(ForeignAppStatus foreignAppStatus) {
		context = FacesContext.getCurrentInstance();
		try {
			foreignAppStatuses.remove(foreignAppStatus);
			prodApplicationsService.removeForeignAppStatus(foreignAppStatus);
			context.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
	}

	public void nceChangeListener() {
		if (product.isNewChemicalEntity())
			showNCE = true;
		else
			showNCE = false;
	}

	public String validateApp() {
		context = FacesContext.getCurrentInstance();
		try {
			saveApp();
			prodApplications.setApplicant(applicant);
			// 22.07.2016
			//prodApplications.setCreatedBy(applicantUser);
			//        prodApplications.setForeignAppStatus(foreignAppStatuses);
			prodApplications.setProduct(product);
			if (product.getId() == null) {
				product.setCreatedBy(getLoggedInUser());
			}

			RetObject retObject = productService.validateProduct(prodApplications);

			if (retObject.getMsg().equals("persist")) {
				userSession.setProdAppID(prodApplications.getId());
				return "/secure/consentform.faces";
			} else {
				ArrayList<String> erroMsgs = (ArrayList<String>) retObject.getObj();
				for (String msg : erroMsgs) {
					context.addMessage(null, new FacesMessage(bundle.getString(msg)));
				}
				return "";
			}
		} catch (Exception ex) {
			context.addMessage(null, new FacesMessage(bundle.getString("save_error")));
			return "";
		}

	}

	@Transactional
	public String submitApp() {
		context = FacesContext.getCurrentInstance();

		try {
			if (!userService.verifyUser(userSession.getLoggedINUserID(), password)) {
				context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));

			}

			prodApplications.setProdAppNo(prodApplicationsService.generateAppNo(prodApplications));
			prodApplications.setRegState(RegState.NEW_APPL);
			prodApplications.setSubmitDate(new Date());
			TimeLine timeLine = new TimeLine();
			timeLine.setComment(bundle.getString("timeline_newapp"));
			timeLine.setRegState(prodApplications.getRegState());
			timeLine.setProdApplications(prodApplications);
			timeLine.setUser(getLoggedInUser());
			timeLine.setStatusDate(prodApplications.getSubmitDate());
			saveApp();
			timelineService.saveTimeLine(timeLine);
			context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));
			return "/secure/prodregack.faces";
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
			return "";
		}

	}

	public String cancel() {
		try {
			userSession.setProdAppID(null);
			context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			WebUtils.setSessionAttribute(request, "regHomeMbean", null);
			return "/public/registrationhome.faces";
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
			return "";
		}

	}

	public ProdApplications getProdApplications() {
		if (prodApplications == null || userSession.getProdAppID() != null) {
			initProdApps(userSession.getProdAppID());
		}
		return prodApplications;
	}

	public void setProdApplications(ProdApplications prodApplications) {
		//        this.prodApplications = prodApplicationsService.findProdApplications(prodApplications.getId());
		//        product = productService.findProduct(prodApplications.getProd().getId());
		this.prodApplications = prodApplications;
		setFieldValues();
	}

	private void initProdApps(Long prodAppID) {
		try {
			prodApplications = prodApplicationsService.findProdApplications(prodAppID);
			setFieldValues();
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

	}

	//used to set all the field values after insert/update operation
	private void setFieldValues() {
		try {
			if (prodApplications != null && prodApplications.getProduct() != null) {
				product = productService.findProduct(prodApplications.getProduct().getId());
				selectedInns = product.getInns();
				selectedExipients = product.getExcipients();
				selectedAtcs = product.getAtcs();
				companies = product.getProdCompanies();
				applicant = prodApplications.getApplicant();

				applicantUser = prodApplications.getApplicantUser();
				if(applicantUser == null)
					applicantUser = prodApplications.getCreatedBy();
				if(applicantUser == null)
					applicantUser = getLoggedInUser();

				pricing = product.getPricing();
				drugPrices = pricing != null ? pricing.getDrugPrices() : null;
				useCategories = product.getUseCategories();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

	}

	public Applicant getApplicant() {
		try {
			if (applicant == null || applicant.getApplcntId() == null) {
				if (prodApplications != null && prodApplications.getApplicant() != null && prodApplications.getApplicant().getApplcntId() != null) {
					applicant = applicantService.findApplicant(prodApplications.getApplicant().getApplcntId());
				} else if (getLoggedInUser().getApplicant() != null) {
					applicant = applicantService.findApplicant(getLoggedInUser().getApplicant().getApplcntId());
				} else
					applicant = new Applicant();
			}
			return applicant;
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
			return null;
		}

	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public List<ProdInn> getSelectedInns() {
		return selectedInns;
	}

	public void setSelectedInns(List<ProdInn> selectedInns) {
		this.selectedInns = selectedInns;
	}

	public PharmClassif getSelectedPharmClassif() {
		return selectedPharmClassif;
	}

	public void setSelectedPharmClassif(PharmClassif selectedPharmClassif) {
		this.selectedPharmClassif = selectedPharmClassif;
	}


	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public User getLoggedInUser() {
		try {
			if (loggedInUser == null)
				loggedInUser = userService.findUser(userSession.getLoggedINUserID());
		} catch (Exception ex) {
			ex.printStackTrace();
			context.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public List<ProdAppChecklist> getProdAppChecklists() {
		return prodAppChecklists;
	}

	public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
		this.prodAppChecklists = prodAppChecklists;
	}

	public List<DrugPrice> getDrugPrices() {
		return drugPrices;
	}

	public void setDrugPrices(List<DrugPrice> drugPrices) {
		this.drugPrices = drugPrices;
	}

	public boolean isShowNCE() {
		return showNCE;
	}

	public void setShowNCE(boolean showNCE) {
		this.showNCE = showNCE;
	}


	public User getApplicantUser() {
		return applicantUser;
	}

	public void setApplicantUser(User applicantUser) {
		this.applicantUser = applicantUser;
	}

	public List<ProdCompany> getCompanies() {
		return companies;
	}

	public void setCompanies(List<ProdCompany> companies) {
		this.companies = companies;
	}

	public List<ForeignAppStatus> getForeignAppStatuses() {
		try {
			if (foreignAppStatuses == null) {
				foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
				if (foreignAppStatuses == null)
					foreignAppStatuses = new ArrayList<ForeignAppStatus>();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

		return foreignAppStatuses;
	}

	public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
		this.foreignAppStatuses = foreignAppStatuses;
	}

	public List<ProdExcipient> getSelectedExipients() {
		return selectedExipients;
	}

	public void setSelectedExipients(List<ProdExcipient> selectedExipients) {
		this.selectedExipients = selectedExipients;
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

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProdApplicationsService getProdApplicationsService() {
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
		this.prodApplicationsService = prodApplicationsService;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public InnService getInnService() {
		return innService;
	}

	public void setInnService(InnService innService) {
		this.innService = innService;
	}

	public GlobalEntityLists getGlobalEntityLists() {
		return globalEntityLists;
	}

	public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
		this.globalEntityLists = globalEntityLists;
	}

	public ChecklistService getChecklistService() {
		return checklistService;
	}

	public void setChecklistService(ChecklistService checklistService) {
		this.checklistService = checklistService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UseCategory> getUseCategories() {
		return useCategories;
	}

	public void setUseCategories(List<UseCategory> useCategories) {
		this.useCategories = useCategories;
	}

	public List<Atc> getSelectedAtcs() {
		return selectedAtcs;
	}

	public void setSelectedAtcs(List<Atc> selectedAtcs) {
		this.selectedAtcs = selectedAtcs;
	}

	public Pricing getPricing() {
		return pricing;
	}

	public void setPricing(Pricing pricing) {
		this.pricing = pricing;
	}

	public boolean isShowDrugPrice() {
		return showDrugPrice;
	}

	public void setShowDrugPrice(boolean showDrugPrice) {
		this.showDrugPrice = showDrugPrice;
	}

	public TimelineService getTimelineService() {
		return timelineService;
	}

	public void setTimelineService(TimelineService timelineService) {
		this.timelineService = timelineService;
	}

	public FacesContext getContext() {
		return context;
	}

	public void setContext(FacesContext context) {
		this.context = context;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}


	public List<Attachment> getAttachments() {
		try {
			if (attachments == null)
				attachments = (ArrayList<Attachment>) attachmentDAO.findByProdApplications_Id(getProdApplications().getId());
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
		}

		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public AttachmentDAO getAttachmentDAO() {
		return attachmentDAO;
	}

	public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
		this.attachmentDAO = attachmentDAO;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public UploadedFile getPayReceipt() {
		return payReceipt;
	}

	public void setPayReceipt(UploadedFile payReceipt) {
		this.payReceipt = payReceipt;
	}

	public UploadedFile getClinicalReview() {
		return clinicalReview;
	}

	public void setClinicalReview(UploadedFile clinicalReview) {
		this.clinicalReview = clinicalReview;
	}

	public boolean isShowfull() {
		if (userSession.isStaff() || userSession.isModerator() || userSession.isReviewer() || userSession.isLab() || userSession.isClinical()) {
			showfull = true;
		} else if (userSession.isCompany()) {
			if (prodApplications.getApplicant().getApplcntId().equals(userSession.getApplcantID())) {
				showfull = true;
			} else {
				showfull = false;
			}
		}
		return showfull;
	}

	public void setShowfull(boolean showfull) {
		this.showfull = showfull;
	}

	public String getAppType() {
		String res = bundle.getString(prodApplications.getProdAppType().getKey());
		if (prodApplications.getProdAppType().equals(ProdAppType.VARIATION)){
			res = res + " (";
			if (prodApplications.getMjVarQnt()>0){
				res = res + bundle.getString("variationType_major").toLowerCase() + " " + String.valueOf(prodApplications.getMjVarQnt());
			}
			if (prodApplications.getMnVarQnt()>0 && (prodApplications.getMjVarQnt()>0)){
				res = res + " " + "/" + " ";
			}
			if (prodApplications.getMnVarQnt()>0){
				res = res + " " + bundle.getString("variationType_minor").toLowerCase() + " " + String.valueOf(prodApplications.getMnVarQnt());
			}
			res = res + ")";
		}
		appType = res;
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
}
