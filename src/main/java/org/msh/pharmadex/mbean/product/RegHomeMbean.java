package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.*;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private PharmClassifService pharmClassifService;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InnService innService;

    @Autowired
    private AtcService atcService;

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private AppointmentService appointmentService;

    private static Logger logger = Logger.getLogger(RegHomeMbean.class.getName());
    private List<ProdInn> selectedInns;
    private List<Atc> selectedAtcs;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<Inn> innList;
    private List<Atc> atcList;
    private List<PharmClassif> pharmClassifList;
    private int tabIndex;
    private ProdApplications prodApplications = new ProdApplications();
    private Product product;
    private Applicant applicant;
    private boolean showAppReg = false;
    private PharmClassif selectedPharmClassif;
    private ProdInn prodInn;
    private Atc atc;
    private User loggedInUser;
    private List<Company> companies = new ArrayList<Company>(10);

    private boolean download;

    private ProdInn deleteInn;

    private TreeNode atcTree;
    private TreeNode selAtcTree;

    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    @PostConstruct
    private void init() {
        innList = innService.getInnList();
        product = new Product();
        product.setPharmClassif(new PharmClassif());
        product.setDosForm(new DosageForm());
        product.setDosUnit(new DosUom());
        product.setInns(selectedInns);
        prodApplications.setProd(product);
        prodApplications.setRegState(RegState.SAVED);
        prodAppChecklists = new ArrayList<ProdAppChecklist>();
        prodApplications.setProdAppChecklists(prodAppChecklists);
        List<Checklist> allChecklist = prodApplicationsService.findAllChecklist();
        ProdAppChecklist eachProdAppCheck;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachProdAppCheck = new ProdAppChecklist();
            eachProdAppCheck.setChecklist(allChecklist.get(i));
            eachProdAppCheck.setProdApplications(prodApplications);
            prodAppChecklists.add(eachProdAppCheck);
        }
        if (getLoggedInUser() != null && getLoggedInUser().getApplicant() != null)
            product.setApplicant(getLoggedInUser().getApplicant());
        prodApplications.setUser(getLoggedInUser());
        prodApplications.getProd().setInns(selectedInns);
        prodApplications.getProd().setAtcs(selectedAtcs);
        prodApplications.getProd().setCompanies(companies);
        prodApplications.setProdAppChecklists(prodAppChecklists);

        eventModel = new DefaultScheduleModel();
        for (Appointment app : appointmentService.getAppointments()) {
            eventModel.addEvent(new DefaultScheduleEvent(app.getTile(), app.getStart(), app.getEnd(), true));
        }
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

    public List<Inn> completeInnCodes(String query) {
        List<Inn> suggestions = new ArrayList<Inn>();

        if (query == null || query.equalsIgnoreCase(""))
            return getInnList();

        for (Inn eachInn : getInnList()) {
            if (eachInn.getName().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachInn);
        }
        return suggestions;
    }

    public List<Atc> completeAtcNames(String query) {
        List<Atc> suggestions = new ArrayList<Atc>();

        if (query == null || query.equalsIgnoreCase(""))
            return getAtcList();

        for (Atc eachAtc : getAtcList()) {
            if (eachAtc.getAtcName().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachAtc);
        }
        System.out.println("Suggestions size == " + suggestions.size());
        return suggestions;
    }

    public List<Atc> completeAtcCodes(String query) {
        List<Atc> suggestions = new ArrayList<Atc>();

        if (query == null || query.equalsIgnoreCase(""))
            return getAtcList();

        for (Atc eachAtc : getAtcList()) {
            if (eachAtc.getAtcCode().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachAtc);
        }
        System.out.println("Suggestions size == " + suggestions.size());
        return suggestions;
    }

    public List<Inn> getInnList() {
        return innService.getInnList();
    }

    JasperPrint jasperPrint;

    public void reportinit() throws JRException {
        URL resource = getClass().getResource("/reports/letter.jasper");
        HashMap param = new HashMap();
        param.put("appName", applicant.getAppName());
        param.put("prodName", product.getProdName());
        param.put("subject", "Subject: Application for registering " + product.getProdName() + " ");
        param.put("body", "Thank you for applying to register " + product.getProdName() + " manufactured by " + applicant.getAppName()
                + ". Your application is successfully submitted and the application number is " + prodApplications.getId() + ". " +
                "Please use this application number for any future correspondence.");
        param.put("address1", product.getApplicant().getAddress().getAddress1());
        param.put("address2", product.getApplicant().getAddress().getAddress2());
        param.put("country", product.getApplicant().getAddress().getCountry().getCountryName());
        jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(resource.getFile(), param);
    }

    public void PDF() throws JRException, IOException {
        reportinit();
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();


    }

    public String saveApp() {
        save();
        return "/secure/savedapplications.faces";
    }

    public void save() {
        try {
            prodApplications.setUser(getLoggedInUser());
            prodApplications.setProd(product);
            if (selectedInns != null && selectedInns.size() > 0)
                product.setInns(selectedInns);
            if (selectedAtcs != null && selectedAtcs.size() > 0) {
                List<Atc> tempAtc = new ArrayList<Atc>();
                for (Atc atc : selectedAtcs) {
                    tempAtc.add(atcService.findAtcById(atc.getAtcCode()));
                }
                product.setAtcs(tempAtc);
            }
            if (companies != null && companies.size() > 0) {
                product.setCompanies(companies);
            }


            product.setCreatedBy(getLoggedInUser());
            product.setApplicant(applicant);

            prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedInUserObj()).equalsIgnoreCase("persisted");
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Error", " Reason: " + e.getMessage()));
        }
    }

    public String removeInn() {

        selectedInns.remove(prodInn);
        return null;
    }

    public String submitApp() {
        prodApplications.setRegState(RegState.NEW_APPL);
        TimeLine timeLine = new TimeLine();
        timeLine.setComment("New Application submitted by applicant");
        timeLine.setRegState(RegState.NEW_APPL);
        timeLine.setProdApplications(prodApplications);
        timeLine.setUser(getLoggedInUser());
        timeLine.setStatusDate(new Date());
        save();
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
        prodApplications.getProd().setAtcs(selectedAtcs);
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
        if (getLoggedInUser() != null && getLoggedInUser().getApplicant() != null)
            return false;
        else
            return true;
    }

    public void setShowAppReg(boolean showAppReg) {
        this.showAppReg = showAppReg;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    @Transactional
    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
        this.product = productService.findProductById(prodApplications.getProd().getId());
        this.selectedInns = innService.findInnByProdApp(product.getId());
        for (Atc atc : product.getAtcs()) {
            atc = atcService.findAtcById(atc.getAtcCode());
        }
        this.selectedAtcs = product.getAtcs();
        this.companies = productService.findCompaniesByProd(product.getId());
        this.prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
        this.prodApplications.setProdAppChecklists(prodAppChecklists);
        this.prodApplications.getProd().setInns(selectedInns);
        this.prodApplications.getProd().setAtcs(selectedAtcs);
        this.prodApplications.getProd().setCompanies(companies);

    }

    public Applicant getApplicant() {
        Long id;
        if (applicant == null) {
            if (product != null && product.getApplicant() != null) {
                applicant = applicantService.findApplicant(product.getApplicant().getApplcntId());
            } else {
                if (getLoggedInUser().getApplicant() != null)
                    applicant = applicantService.findApplicant(getLoggedInUser().getApplicant().getApplcntId());
            }
        }
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public void setInnList(List<Inn> innList) {
        this.innList = innList;
    }

    public List<ProdInn> getSelectedInns() {
        if (selectedInns == null) {
            if (product.getInns() != null) {
                selectedInns = product.getInns();
            } else {
                selectedInns = new ArrayList<ProdInn>();
            }
        }
        return selectedInns;
    }

    public void setSelectedInns(List<ProdInn> selectedInns) {
        this.selectedInns = selectedInns;
    }

    public List<PharmClassif> getPharmClassifList() {
        return pharmClassifService.getPharmClassifList();
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

    public List<Atc> getAtcList() {
        return atcService.getAtcList();
    }

    public void setAtcList(List<Atc> atcList) {
        this.atcList = atcList;
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

}
