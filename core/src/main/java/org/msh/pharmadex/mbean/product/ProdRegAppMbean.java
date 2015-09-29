package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.service.*;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
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

    @PostConstruct
    private void init() {
        Long prodAppID = userSession.getProdAppID();
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

                //Set logged in user company as the company.
                if (userSession.isCompany()) {
                    applicantUser = getLoggedInUser();
                    prodApplications.setApplicant(applicantUser.getApplicant());
                    prodApplications.setApplicantUser(applicantUser);
                }
                prodApplications.setCreatedBy(getLoggedInUser());
            }
        } else {
            initProdApps(prodAppID);
        }
    }

    @PreDestroy
    private void destroyBn() {
        System.out.println("--------------------------------------");
        System.out.println("------ProdRegAppMbean Bean destroyed-----");
        System.out.println("--------------------------------------");
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.reportinit(prodApplications);
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

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

        file = event.getFile();
        try {
            attachment.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        attachment.setProdApplications(prodApplications);
        attachment.setFileName(file.getFileName());
        attachment.setContentType(file.getContentType());
        attachment.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        attachment.setRegState(prodApplications.getRegState());
//        attachmentDAO.save(attachment);
//        userSession.setFile(file);
    }

    public void handlePayReceiptUpload() {
        FacesMessage msg;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        if (payReceipt != null) {
            msg = new FacesMessage(bundle.getString("global.success"), payReceipt.getFileName() + bundle.getString("upload_success"));
            facesContext.addMessage(null, msg);
            try {
                prodApplications.setFeeReceipt(IOUtils.toByteArray(payReceipt.getInputstream()));

            } catch (IOException e) {
                msg = new FacesMessage(bundle.getString("global_fail"), payReceipt.getFileName() + bundle.getString("upload_fail"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            msg = new FacesMessage(bundle.getString("global_fail"), payReceipt.getFileName() + bundle.getString("upload_fail"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public StreamedContent fileDownload() {
        byte[] file1 = prodApplications.getFeeReceipt();
        InputStream ist = new ByteArrayInputStream(file1);
        StreamedContent download = new DefaultStreamedContent(ist);
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }



    public void addDocument() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        facesContext = FacesContext.getCurrentInstance();
//        file = userSession.getFile();
//        attachment.setFile(file.getContents());
        attachmentDAO.save(attachment);
        setAttachments(null);
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
        facesContext.addMessage(null, msg);

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
    @Transactional
    public String onFlowProcess(FlowEvent event) {
        context = FacesContext.getCurrentInstance();
        String currentWizardStep = event.getOldStep();
        String nextWizardStep = event.getNewStep();
        try {
            initializeNewApp(nextWizardStep);
            if (!currentWizardStep.equals("prodreg"))
                saveApp();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            context.addMessage(null, msg);
            nextWizardStep = currentWizardStep; // keep wizard on current step if error
        }
        return nextWizardStep; // return new step if all ok
    }

    //Used to initialize field values only for new applications. For saved applications the values are assigned in setprodapplications
    @Transactional
    private void initializeNewApp(String currentWizardStep) {
        if (currentWizardStep.equals("prodreg") && product.getId() == null) {
        } else if (currentWizardStep.equals("proddetails")) {
            //Only initialize once for new product applications. For saved application it is initialized in the setprodapplication method
//            if (prodAppChecklists == null || prodAppChecklists.size() < 1) {
//                prodAppChecklists = new ArrayList<ProdAppChecklist>();
//                prodApplications.setProdAppChecklists(prodAppChecklists);
//                List<Checklist> allChecklist = checklistService.getChecklists(prodApplications.getProdAppType(), true);
//                ProdAppChecklist eachProdAppCheck;
//                if (allChecklist != null && allChecklist.size() > 0) {
//                    for (int i = 0; allChecklist.size() > i; i++) {
//                        eachProdAppCheck = new ProdAppChecklist();
//                        eachProdAppCheck.setChecklist(allChecklist.get(i));
//                        eachProdAppCheck.setProdApplications(prodApplications);
//                        prodAppChecklists.add(eachProdAppCheck);
//                    }
//                }
//                prodApplications.setProdAppChecklists(prodAppChecklists);

//            }
        } else if (currentWizardStep.equals("appdetails")) {

        } else if (currentWizardStep.equals("applicationStatus")) {

        } else if (currentWizardStep.equals("manufdetail")) {
            product.setProdCompanies(companyService.findCompanyByProdID(product.getId()));

        } else if (currentWizardStep.equals("pricing")) {
            RetObject retObject = productService.findDrugPriceByProd(product.getId());
            if(retObject.getMsg().equals("persist")) {
                pricing = (Pricing) retObject.getObj();
                if(pricing==null)
                    pricing = new Pricing(drugPrices, product);
                product.setPricing(pricing);
            }else{
                FacesMessage msg = new FacesMessage(bundle.getString("global_fail"), retObject.getMsg());
                context.addMessage(null, msg);
            }
        } else if (currentWizardStep.equals("attach")) {
            if(prodAppChecklists!=null&&prodAppChecklists.size()>0)
                prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
        } else if (currentWizardStep.equals("prodAppChecklist")) {
            prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
            if(prodAppChecklists!=null&&prodAppChecklists.size()<1) {
                prodAppChecklists = new ArrayList<ProdAppChecklist>();
                List<Checklist> allChecklist = checklistService.getChecklists(prodApplications, true);
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
            if (prodAppChecklists != null)
                prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
        }

    }


    @Transactional
    public void saveApp() {
        context = FacesContext.getCurrentInstance();
        product.setUseCategories(useCategories);
        try {
//            prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
            RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, userSession.getLoggedINUserID());
            if(retObject.getMsg().equals("persist")) {
                prodApplications = (ProdApplications) retObject.getObj();
                setFieldValues();
                context.addMessage(null, new FacesMessage(bundle.getString("app_save_success")));
            }else{
                context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
        }

    }

    public String removeInn(ProdInn prodInn) {
        context = FacesContext.getCurrentInstance();
        selectedInns.remove(prodInn);
        context.addMessage(null, new FacesMessage(bundle.getString("inn_removed")));
        return null;
    }

    public String removeExcipient(ProdExcipient prodExcipient) {
        context = FacesContext.getCurrentInstance();
        selectedExipients.remove(prodExcipient);
        context.addMessage(null, new FacesMessage(bundle.getString("expnt_removed")));
        return null;
    }

    public String removeAtc(Atc atc) {
        context = FacesContext.getCurrentInstance();
        selectedAtcs.remove(atc);
        context.addMessage(null, new FacesMessage(bundle.getString("atc_removed")));
        return null;
    }

    public String removeDrugPrice(DrugPrice drugPrice) {
        context = FacesContext.getCurrentInstance();
        drugPrices.remove(drugPrice);
        context.addMessage(null, new FacesMessage(bundle.getString("drugprice_removed")));
        return null;
    }

    public void removeCompany(ProdCompany selectedCompany) {
        context = FacesContext.getCurrentInstance();
        companies.remove(selectedCompany);
        companyService.removeProdCompany(selectedCompany);

        context.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
    }

    public void removeAppStatus(ForeignAppStatus foreignAppStatus) {
        context = FacesContext.getCurrentInstance();
        foreignAppStatuses.remove(foreignAppStatus);
        prodApplicationsService.removeForeignAppStatus(foreignAppStatus);
        context.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
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
            prodApplications.setApplicant(applicant);
            prodApplications.setCreatedBy(applicantUser);
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
    }

    public String cancel() {
        userSession.setProdAppID(null);
        context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        return "/public/registrationhome.faces";
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null || userSession.getProdAppID() != null) {
            initProdApps(userSession.getProdAppID());
        }
        return prodApplications;
    }

    @Transactional
    public void setProdApplications(ProdApplications prodApplications) {
//        this.prodApplications = prodApplicationsService.findProdApplications(prodApplications.getId());
//        product = productService.findProduct(prodApplications.getProd().getId());
        this.prodApplications = prodApplications;
        setFieldValues();
    }

    private void initProdApps(Long prodAppID) {
        prodApplications = prodApplicationsService.findProdApplications(prodAppID);
        setFieldValues();
    }

    //used to set all the field values after insert/update operation
    private void setFieldValues() {
//        prodApplications = product.getProdApplications();
        if (prodApplications != null && prodApplications.getProduct() != null) {
            product = productService.findProduct(prodApplications.getProduct().getId());
            selectedInns = product.getInns();
            selectedExipients = product.getExcipients();
            selectedAtcs = product.getAtcs();
            companies = product.getProdCompanies();
//        prodAppChecklists = prodApplications.getProdAppChecklists();
            applicant = prodApplications.getApplicant();
            applicantUser = prodApplications.getCreatedBy();
            pricing = product.getPricing();
            drugPrices = pricing != null ? pricing.getDrugPrices() : null;
//        foreignAppStatuses = prodApplications.getForeignAppStatus();
            useCategories = product.getUseCategories();
        }
    }

    public Applicant getApplicant() {
        if (applicant == null || applicant.getApplcntId() == null) {
            if (prodApplications != null && prodApplications.getApplicant() != null && prodApplications.getApplicant().getApplcntId() != null) {
                applicant = applicantService.findApplicant(prodApplications.getApplicant().getApplcntId());
            } else if (getLoggedInUser().getApplicant() != null) {
                applicant = applicantService.findApplicant(getLoggedInUser().getApplicant().getApplcntId());
            } else
                applicant = new Applicant();
        }
        return applicant;
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
        if (loggedInUser == null)
            loggedInUser = userService.findUser(userSession.getLoggedINUserID());
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

    private void addMessage(FacesMessage message) {
        context.addMessage(null, message);
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
        if(foreignAppStatuses==null){
            foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
            if(foreignAppStatuses==null)
                foreignAppStatuses = new ArrayList<ForeignAppStatus>();
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
        if (attachments == null)
            attachments = (ArrayList<Attachment>) attachmentDAO.findByProdApplications_Id(getProdApplications().getId());

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
}
