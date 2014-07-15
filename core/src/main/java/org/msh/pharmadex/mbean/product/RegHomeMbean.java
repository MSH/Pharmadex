package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RegistrationUtil;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
import java.util.logging.Logger;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class RegHomeMbean implements Serializable {
    private static final long serialVersionUID = 8349519957756249083L;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AtcService atcService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    @Autowired
    private ChecklistService checklistService;

    @Autowired
    private UserService userService;

    private static Logger logger = Logger.getLogger(RegHomeMbean.class.getName());
    private List<ProdInn> selectedInns;
    private List<Atc> selectedAtcs;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<Company> companies;
    private List<DrugPrice> drugPrices;

    private ProdApplications prodApplications;
    private Product product;
    private Applicant applicant;
    private PharmClassif selectedPharmClassif;
    private ProdInn prodInn;
    private Atc atc;
    private User loggedInUser;
    private ProdInn deleteInn;
    private Pricing pricing;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    private JasperPrint jasperPrint;
    private RegATCHelper regATCHelper;

    private boolean showAppReg = false;
    private boolean showCompany = false;
    private boolean showDrugPrice = false;
    private boolean showNCE = false;

    private User applicantUser;

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
            product.setPharmClassif(new PharmClassif());
            product.setAdminRoute(new AdminRoute());

            //being a new application. set regstate as saved
            prodApplications.setRegState(RegState.SAVED);

            if (selectedInns == null) {
                //Initialize Inns
                selectedInns = new ArrayList<ProdInn>();
                product.setInns(selectedInns);
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
                companies = new ArrayList<Company>();
                product.setCompanies(companies);
            }

            //Set logged in user company as the company.
            if (userSession.isCompany()) {
                applicantUser = getLoggedInUser();
                product.setApplicant(applicantUser.getApplicant());
                prodApplications.setUser(applicantUser);
            }
//            eventModel = new DefaultScheduleModel();
//            for (Appointment app : appointmentService.getAppointments()) {
//                eventModel.addEvent(new DefaultScheduleEvent(app.getTile(), app.getStart(), app.getEnd(), true));
//            }
        }
    }


    public void PDF() throws JRException, IOException {
        FacesContext context = FacesContext.getCurrentInstance();
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
        String currentWizardStep = event.getOldStep();
        String nextWizardStep = event.getNewStep();
        try {
            initializeNewApp(currentWizardStep);
            if (!currentWizardStep.equals("prodreg"))
                saveApp();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            FacesContext.getCurrentInstance().addMessage(null, msg);
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
                for (int i = 0; allChecklist.size() > i; i++) {
                    eachProdAppCheck = new ProdAppChecklist();
                    eachProdAppCheck.setChecklist(allChecklist.get(i));
                    eachProdAppCheck.setProdApplications(prodApplications);
                    prodAppChecklists.add(eachProdAppCheck);
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
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
        prodApplications.setUser(applicantUser);
        product.setProdApplications(prodApplications);
//        product.setApplicant(applicantUser.getApplicant());
        if (product.getId() == null)
            product.setCreatedBy(getLoggedInUser());
        try {
            product = productService.updateProduct(product);
            prodApplications = product.getProdApplications();
            setFieldValues();
            context.addMessage(null, new FacesMessage("Application saved successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage("Error saving application."));
        }

    }

    public String removeInn(ProdInn prodInn) {
        selectedInns.remove(prodInn);
        return null;
    }

    public String removeAtc(Atc atc) {
        selectedAtcs.remove(atc);
        return null;
    }

    public void removeCompany(Company selectedCompany) {
        if (selectedCompany.getId() != null)
            companyService.removeCompany(selectedCompany);
        companies.remove(selectedCompany);
    }

    public void nceChangeListener() {
        if (product.isNewChemicalEntity())
            showNCE = true;
        else
            showNCE = false;
    }

    @Transactional
    public String submitApp() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

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

        context.addMessage(null, new FacesMessage("Application submitted successfully."));


//        timelineService.saveTimeLine(timeLine);
        return "/secure/prodregack.faces";
    }


    public String addProdInn() {
        System.out.println("Inside addinn");
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
            prodInn = null;
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Inn cannot be empty."));

        }
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
        prodInn = null;
        return null;
    }

    public String cancel() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        return "/public/registrationhome.faces";
    }

    public boolean isShowAppReg() {
        return !(userSession.isCompany());
    }

    public void setShowAppReg(boolean showAppReg) {
        this.showAppReg = showAppReg;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    @Transactional
    public void setProdApplications(ProdApplications prodApplications) {
//        this.prodApplications = prodApplicationsService.findProdApplications(prodApplications.getId());
        product = productService.findProduct(prodApplications.getProd().getId());
        this.prodApplications = product.getProdApplications();
        setFieldValues();
    }

    //used to set all the field values after insert/update operation
    private void setFieldValues() {
        selectedInns = product.getInns();
        selectedAtcs = product.getAtcs();
        companies = product.getCompanies();
        prodAppChecklists = prodApplications.getProdAppChecklists();
        applicant = product.getApplicant();
//        drugPrices = prodApplications.getPricing().getDrugPrices();
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

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
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
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    private Appointment app = new Appointment();

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

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }


    public void cancelAddApplicant() {
        applicant = new Applicant();
    }

    @Transactional
    public void addApptoRegistration() {
        applicant = applicantService.findApplicant(applicant.getApplcntId());
        product.setApplicant(applicant);
        if (applicant.getUsers().size() > 0) {
            User u = applicant.getUsers().get(0);
            setApplicantUser(userService.findUser(u.getUserId()));
        } else {
            setApplicantUser(null);
        }
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

    private TreeNode selAtcTree;

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

    public List<Atc> completeAtcNames(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getAtcs());
    }

    public List<Atc> completeAtcCodes(String query) {
        List<Atc> suggestions = new ArrayList<Atc>();

        if (query == null || query.equalsIgnoreCase(""))
            return globalEntityLists.getAtcs();

        for (Atc eachAtc : globalEntityLists.getAtcs()) {
            if (eachAtc.getAtcCode().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachAtc);
        }
        System.out.println("Suggestions size == " + suggestions.size());
        return suggestions;
    }

    public List<Inn> completeInnCodes(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getInns());
    }

    public List<Applicant> completeApplicantList(String query) {
        List<Applicant> applicants = applicantService.findAllApplicants();
        return JsfUtils.completeSuggestions(query, applicants);
    }

    public List<PharmClassif> completePharmClassif(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getPharmClassifs());
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }
}
