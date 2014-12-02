package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RegistrationUtil;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class RegHomeMbean implements Serializable {
    private static final long serialVersionUID = 8349519957756249083L;
    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

    @ManagedProperty(value = "#{dosUomDAO}")
    DosUomDAO dosUomDAO;
    private Logger logger = LoggerFactory.getLogger(RegHomeMbean.class);
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;
    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{atcService}")
    private AtcService atcService;
    @ManagedProperty(value = "#{appointmentService}")
    private AppointmentService appointmentService;
    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;
    @ManagedProperty(value = "#{companyService}")
    private CompanyService companyService;
    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{innService}")
    private InnService innService;
    private List<ProdInn> selectedInns;
    private List<ProdExcipient> selectedExipients;
    private List<Atc> selectedAtcs;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<ProdCompany> companies;
    private List<ForeignAppStatus> foreignAppStatuses;
    private List<DrugPrice> drugPrices;
    private ProdApplications prodApplications;
    private Product product;
    private Applicant applicant;
    private PharmClassif selectedPharmClassif;
    private ProdInn prodInn;
    private ProdExcipient prodExcipient;
    private Atc atc;
    private User loggedInUser;
    private ProdInn deleteInn;
    private Pricing pricing;
    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private JasperPrint jasperPrint;
    private RegATCHelper regATCHelper;
    private boolean showCompany = false;
    private boolean showDrugPrice = false;
    private boolean showNCE = false;
    private User applicantUser;
    private Appointment app = new Appointment();
    private TreeNode selAtcTree;

    @PostConstruct
    private void init() {
        if (prodApplications == null) {
            prodApplications = new ProdApplications();
            product = new Product(prodApplications);
            prodApplications.setProd(product);
            product.setProdApplications(prodApplications);

            //Initialize associated product entities
            product.setDosForm(new DosageForm());
            product.setDosUnit(new DosUom());
            product.setAdminRoute(new AdminRoute());

            //being a new application. set regstate as saved
            prodApplications.setRegState(RegState.SAVED);

            if (selectedInns == null) {
                //Initialize Inns
                selectedInns = new ArrayList<ProdInn>();
                product.setInns(selectedInns);
            }
            if (selectedExipients == null) {
                //Initialize Excipients
                selectedExipients = new ArrayList<ProdExcipient>();
                product.setExcipients(selectedExipients);
            }
            if (selectedAtcs == null) {
                //Initialize Atcs if null
                selectedAtcs = new ArrayList<Atc>();
                product.setAtcs(selectedAtcs);
            }

            if (pricing == null) {
                pricing = new Pricing(new ArrayList<DrugPrice>());
                prodApplications.setPricing(pricing);
            }

            //Initialize companies if null
            if (companies == null) {
                companies = new ArrayList<ProdCompany>();
//                product.setCompanies(companies);
            }

            //Set logged in user company as the company.
            if (userSession.isCompany()) {
                applicantUser = userService.findUser(getLoggedInUser().getUserId());
                product.setApplicant(applicantUser.getApplicant());
                prodApplications.setUser(applicantUser);
            }

            prodInn = new ProdInn();
//            prodInn.setDosUnit(new DosUom());

            prodExcipient = new ProdExcipient();
//            prodExcipient.setDosUnit(new DosUom());
//            eventModel = new DefaultScheduleModel();
//            for (Appointment app : appointmentService.getAppointments()) {
//                eventModel.addEvent(new DefaultScheduleEvent(app.getTile(), app.getStart(), app.getEnd(), true));
//            }
            if (userSession.getProdApplications() != null) {
                initProdApps();
            }
        }
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.reportinit(product);
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

    }

    //fires everytime you click on next or prev button on the wizard
    @Transactional
    public String onFlowProcess(FlowEvent event) {
        context = FacesContext.getCurrentInstance();
        String currentWizardStep = event.getOldStep();
        String nextWizardStep = event.getNewStep();
        try {
            initializeNewApp(currentWizardStep);
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
            if (prodAppChecklists == null || prodAppChecklists.size() < 1) {
                prodAppChecklists = new ArrayList<ProdAppChecklist>();
                prodApplications.setProdAppChecklists(prodAppChecklists);
                List<Checklist> allChecklist = checklistService.getChecklists(prodApplications.getProdAppType(), true);
                ProdAppChecklist eachProdAppCheck;
                if (allChecklist != null && allChecklist.size() > 0) {
                    for (int i = 0; allChecklist.size() > i; i++) {
                        eachProdAppCheck = new ProdAppChecklist();
                        eachProdAppCheck.setChecklist(allChecklist.get(i));
                        eachProdAppCheck.setProdApplications(prodApplications);
                        prodAppChecklists.add(eachProdAppCheck);
                    }
                }
                prodApplications.setProdAppChecklists(prodAppChecklists);

            }
        } else if (currentWizardStep.equals("appdetails")) {
        } else if (currentWizardStep.equals("manufdetail")) {
        } else if (currentWizardStep.equals("pricing")) {
        } else if (currentWizardStep.equals("payment")) {
        } else if (currentWizardStep.equals("prodAppChecklist")) {

        } else if (currentWizardStep.equals("appointment")) {

        } else if (currentWizardStep.equals("summary")) {

        }

    }

    @Transactional
    public void saveApp() {
        context = FacesContext.getCurrentInstance();
        product.setApplicant(applicant);
        prodApplications.setUser(applicantUser);
        prodApplications.setForeignAppStatus(foreignAppStatuses);
        product.setProdApplications(prodApplications);
        if (product.getId() == null) {
            product.setCreatedBy(getLoggedInUser());
        }
//        try {
        product = productService.updateProduct(product);
        prodApplications = product.getProdApplications();
        setFieldValues();
        context.addMessage(null, new FacesMessage(bundle.getString("app_save_success")));
//        } catch (Exception e) {
//            e.printStackTrace();
//            context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
//        }

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

    @Transactional
    public String submitApp() {
        context = FacesContext.getCurrentInstance();
        RegistrationUtil registrationUtil = new RegistrationUtil();
        prodApplications.setProdAppNo(registrationUtil.generateAppNo(prodApplications.getId()));

        List<TimeLine> timeLines = new ArrayList<TimeLine>();
        TimeLine timeLine = new TimeLine();
        timeLine.setComment(bundle.getString("timeline_newapp"));
        timeLine.setRegState(RegState.NEW_APPL);
        timeLine.setProdApplications(prodApplications);
        timeLine.setUser(getLoggedInUser());
        timeLine.setStatusDate(new Date());
        timeLines.add(timeLine);

        prodApplications.setTimeLines(timeLines);
        prodApplications.setRegState(RegState.NEW_APPL);
        prodApplications.setSubmitDate(new Date());

        saveApp();

        context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));


//        timelineService.saveTimeLine(timeLine);
        return "/secure/prodregack.faces";
    }

    public String addProdInn() {
        context = FacesContext.getCurrentInstance();
        if (prodInn.getInn().getId() == null)
            prodInn.setInn(innService.saveInn(prodInn.getInn()));
        else
            prodInn.setDosUnit(dosUomDAO.findOne(prodInn.getDosUnit().getId()));

        prodInn.setProduct(product);
        selectedInns.add(prodInn);
        product.setInns(selectedInns);


        try {
            List<Atc> a = atcService.findAtcByName(prodInn.getInn().getName());
            if (a != null) {
                if (selectedAtcs == null)
                    selectedAtcs = new ArrayList<Atc>();
                selectedAtcs.addAll(a);
                product.setAtcs(selectedAtcs);
            }
            prodInn = new ProdInn();
            prodInn.setDosUnit(new DosUom());
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            context.addMessage(null, new FacesMessage(bundle.getString("product_innname_valid")));

        }
        return null;
    }

    public String addProdExcipient() {
        context = FacesContext.getCurrentInstance();
        if (prodExcipient.getExcipient().getId() == null)
            prodExcipient.setExcipient(innService.saveExcipient(prodExcipient.getExcipient()));
        else
            prodExcipient.setDosUnit(dosUomDAO.findOne(prodExcipient.getDosUnit().getId()));

        prodExcipient.setProduct(product);
        selectedExipients.add(prodExcipient);
        product.setExcipients(selectedExipients);


        prodExcipient = new ProdExcipient();
        prodExcipient.setDosUnit(new DosUom());
        return null;
    }


    public void openAddATC() {
        regATCHelper = new RegATCHelper(atc, globalEntityLists);
    }

    public String addAtc() {
        if (selectedAtcs == null)
            selectedAtcs = new ArrayList<Atc>();
        selectedAtcs.add(atc);
//        prodApplications.getProd().setAtcs(selectedAtcs);
        atc = null;
        return null;
    }

    public String cancelAddInn() {
        selectedInns.remove(prodInn);
        prodInn = new ProdInn();
        prodInn.setDosUnit(new DosUom());
        return null;
    }

    public String cancelAddExcipient() {
        selectedExipients.remove(prodExcipient);
        prodExcipient = new ProdExcipient();
        prodExcipient.setDosUnit(new DosUom());
        return null;
    }

    public String cancel() {
        context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        return "/public/registrationhome.faces";
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null || userSession.getProdApplications() != null) {
            initProdApps();
            userSession.setProdApplications(null);
        }
        return prodApplications;
    }

    @Transactional
    public void setProdApplications(ProdApplications prodApplications) {
//        this.prodApplications = prodApplicationsService.findProdApplications(prodApplications.getId());
        product = productService.findProduct(prodApplications.getProd().getId());
        this.prodApplications = product.getProdApplications();
        setFieldValues();
    }

    private void initProdApps() {
        String prodID = "" + userSession.getProdApplications().getProd().getId();
        userSession.setProduct(null);
        userSession.setProdApplications(null);
        product = productService.findProduct(Long.valueOf(prodID));
        setFieldValues();
    }

    //used to set all the field values after insert/update operation
    private void setFieldValues() {
        prodApplications = product.getProdApplications();
        selectedInns = product.getInns();
        selectedExipients = product.getExcipients();
        selectedAtcs = product.getAtcs();
        companies = product.getProdCompanies();
        prodAppChecklists = prodApplications.getProdAppChecklists();
        applicant = product.getApplicant();
        applicantUser = prodApplications.getUser();
        drugPrices = prodApplications.getPricing().getDrugPrices();
        foreignAppStatuses = prodApplications.getForeignAppStatus();
    }

    public Applicant getApplicant() {
        if (applicant == null || applicant.getApplcntId() == null) {
            if (product != null && product.getApplicant() != null && product.getApplicant().getApplcntId() != null) {
                applicant = applicantService.findApplicant(product.getApplicant().getApplcntId());
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

    public String openAddInn() {
        prodInn = new ProdInn();
        prodInn.setInn(new Inn());
        prodInn.setDosUnit(new DosUom());
        return null;
    }

    public String openAddExp() {
        prodExcipient = new ProdExcipient();
        prodExcipient.setExcipient(new Excipient());
        prodExcipient.setDosUnit(new DosUom());
        return null;
    }

    public ProdInn getProdInn() {
        return prodInn;
    }

    public void setProdInn(ProdInn prodInn) {
        this.prodInn = prodInn;
    }

    public List<Atc> getSelectedAtcs() {
        return selectedAtcs;
    }

    public void setSelectedAtcs(List<Atc> selectedAtcs) {
        this.selectedAtcs = selectedAtcs;
    }

    public Atc getAtc() {
        return atc;
    }

    public void setAtc(Atc atc) {
        this.atc = atc;
    }

    public ProdInn getDeleteInn() {
        return deleteInn;
    }

    public void setDeleteInn(ProdInn deleteInn) {
        this.deleteInn = deleteInn;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getLoggedInUser() {
        if (loggedInUser == null)
            loggedInUser = userSession.getLoggedInUserObj();
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

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        context.addMessage(null, message);
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            eventModel.addEvent(event);
            app.setCreatedDate(new Date());
        } else {
            app.setUpdatedDate(new Date());
            eventModel.updateEvent(event);
        }
        app.setStart(event.getStartDate());
        app.setEnd(event.getEndDate());
        app.setTile(prodApplications.getProd().getApplicant().getAppName() + "-" + prodApplications.getProd().getProdName());
        setAppointment();
        prodApplications.setAppointment(app);
        event = new DefaultScheduleEvent();
    }

    private void setAppointment() {
        app.setAllday(true);
        app.setEnd(event.getEndDate());
        app.setStart(event.getStartDate());
        app.setProdApplications(prodApplications);
        app.setTile(applicant.getAppName() + " " + product.getProdName());
    }

    public RegATCHelper getRegATCHelper() {
        return regATCHelper;
    }

    public void setRegATCHelper(RegATCHelper regATCHelper) {
        this.regATCHelper = regATCHelper;
    }

    public boolean isShowCompany() {
        return showCompany;
    }

    public void setShowCompany(boolean showCompany) {
        this.showCompany = showCompany;
    }

    public List<DrugPrice> getDrugPrices() {
        if (drugPrices == null)
            drugPrices = getProduct().getProdApplications().getPricing().getDrugPrices();
        return drugPrices;
    }

    public void setDrugPrices(List<DrugPrice> drugPrices) {
        this.drugPrices = drugPrices;
    }

    public boolean isShowDrugPrice() {
        return showDrugPrice;
    }

    public void setShowDrugPrice(boolean showDrugPrice) {
        this.showDrugPrice = showDrugPrice;
    }

    public boolean isShowNCE() {
        return showNCE;
    }

    public void setShowNCE(boolean showNCE) {
        this.showNCE = showNCE;
    }

    public TreeNode getSelAtcTree() {
        if (selAtcTree == null) {
            populateSelAtcTree();
        }
        return selAtcTree;
    }

    private void populateSelAtcTree() {
        selAtcTree = new DefaultTreeNode("selAtcTree", null);
        selAtcTree.setExpanded(true);
        if (atc != null) {
            List<Atc> parentList = atc.getParentsTreeList(true);
            TreeNode[] nodes = new TreeNode[parentList.size()];
            for (int i = 0; i < parentList.size(); i++) {
                if (i == 0) {
                    nodes[i] = new DefaultTreeNode(parentList.get(i).getAtcCode() + ": " + parentList.get(i).getAtcName(), selAtcTree);
                    nodes[i].setExpanded(true);
                } else {
                    nodes[i] = new DefaultTreeNode(parentList.get(i).getAtcCode() + ": " + parentList.get(i).getAtcName(), nodes[i - 1]);
                    nodes[i].setExpanded(true);
                }
            }
        }
    }

    public void updateAtc() {
        populateSelAtcTree();
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

    public ProdExcipient getProdExcipient() {
        return prodExcipient;
    }

    public void setProdExcipient(ProdExcipient prodExcipient) {
        this.prodExcipient = prodExcipient;
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

    public AtcService getAtcService() {
        return atcService;
    }

    public void setAtcService(AtcService atcService) {
        this.atcService = atcService;
    }

    public AppointmentService getAppointmentService() {
        return appointmentService;
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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

    public InnService getInnService() {
        return innService;
    }

    public void setInnService(InnService innService) {
        this.innService = innService;
    }

    public DosUomDAO getDosUomDAO() {
        return dosUomDAO;
    }

    public void setDosUomDAO(DosUomDAO dosUomDAO) {
        this.dosUomDAO = dosUomDAO;
    }
}
