package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
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

    private static Logger logger = Logger.getLogger(RegHomeMbean.class.getName());
    private List<ProdInn> selectedInns;
    private List<Atc> selectedAtcs;
    private List<ProdAppChecklist> prodAppChecklists;
    private ProdApplications prodApplications;
    private Product product;
    private Applicant applicant;
    private boolean showAppReg = false;
    private PharmClassif selectedPharmClassif;
    private ProdInn prodInn;
    private Atc atc;
    private User loggedInUser;
    private List<Company> companies;
    private ProdInn deleteInn;
    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private JasperPrint jasperPrint;
    private RegATCHelper regATCHelper;
    private boolean showCompany = false;
    private boolean showDrugPrice = false;
    private List<DrugPrice> drugPrices;

    @PostConstruct
    private void init() {
        if (prodApplications == null) {
            prodApplications = new ProdApplications();
            if (product == null)
                product = new Product(prodApplications);
            product.setDosForm(new DosageForm());
            product.setDosUnit(new DosUom());
            product.setPharmClassif(new PharmClassif());
            product.setAdminRoute(new AdminRoute());
            selectedInns = new ArrayList<ProdInn>();
            product.setInns(selectedInns);
            prodApplications.setProd(product);
            product.setProdApplications(prodApplications);
            prodApplications.setRegState(RegState.SAVED);
            prodAppChecklists = new ArrayList<ProdAppChecklist>();
            prodApplications.setProdAppChecklists(prodAppChecklists);
            List<Checklist> allChecklist = globalEntityLists.getChecklists();
            ProdAppChecklist eachProdAppCheck;
            for (int i = 0; allChecklist.size() > i; i++) {
                eachProdAppCheck = new ProdAppChecklist();
                eachProdAppCheck.setChecklist(allChecklist.get(i));
                eachProdAppCheck.setProdApplications(prodApplications);
                prodAppChecklists.add(eachProdAppCheck);
            }
            if (userSession.isCompany()) {
                product.setApplicant(getLoggedInUser().getApplicant());
                prodApplications.setUser(getLoggedInUser());
            }
            product.setInns(selectedInns);
            selectedAtcs = new ArrayList<Atc>();
            product.setAtcs(selectedAtcs);
            companies = new ArrayList<Company>(10);
            product.setCompanies(companies);
            prodApplications.setProdAppChecklists(prodAppChecklists);
            Pricing pricing = new Pricing(new ArrayList<DrugPrice>());
            prodApplications.setPricing(pricing);

            eventModel = new DefaultScheduleModel();
            for (Appointment app : appointmentService.getAppointments()) {
                eventModel.addEvent(new DefaultScheduleEvent(app.getTile(), app.getStart(), app.getEnd(), true));
            }
        }
        regATCHelper = new RegATCHelper(atc, globalEntityLists);
    }

    public List<Inn> completeInnCodes(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getInns());
    }

    public List<Applicant> completeApplicantList(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getRegApplicants());
    }

    public List<PharmClassif> completePharmClassif(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getPharmClassifs());
    }

    public void PDF() throws JRException, IOException {
        jasperPrint = reportService.reportinit(product);
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
    }

    @Transactional
    public void saveApp() {
        prodApplications.setUser(getLoggedInUser());
        if (product.getId() == null)
            product.setCreatedBy(getLoggedInUser());
        productService.updateProduct(product);
    }

    public String removeInn(ProdInn prodInn) {
        selectedInns.remove(prodInn);
        return null;
    }

    public void removeCompany(Company selectedCompany) {
        if (selectedCompany.getId() != null)
            companyService.removeCompany(selectedCompany);
        companies.remove(selectedCompany);
    }

    @Transactional
    public String submitApp() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

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

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
//        timelineService.saveTimeLine(timeLine);
        return "/secure/prodregack.faces";
    }

    public String addProdInn() {
        System.out.println("Inside addinn");
        prodInn.setProduct(product);
        selectedInns.add(prodInn);
        prodApplications.getProd().setInns(selectedInns);

        List<Atc> a = atcService.findAtcByName(prodInn.getInn().getName());
        if (a != null) {
            if (selectedAtcs == null)
                selectedAtcs = new ArrayList<Atc>();
            selectedAtcs.addAll(a);
            prodApplications.getProd().setAtcs(selectedAtcs);
        }
        prodInn = null;
        return null;
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
        selectedInns = product.getInns();
        selectedAtcs = product.getAtcs();
        companies = product.getCompanies();
        prodAppChecklists = prodApplications.getProdAppChecklists();
        applicant = product.getApplicant();
        drugPrices = prodApplications.getPricing().getDrugPrices();
    }

    public Applicant getApplicant() {
        if (applicant == null) {
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

    public void addApptoRegistration() {
        product.setApplicant(applicant);

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


}
