package org.msh.pharmadex.mbean.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.Hibernate;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.ReRegistrationDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.processes.ReRegistration;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Odissey on 16/06/2016.
 */
@ManagedBean
@ViewScoped
public class ReRegBean implements Serializable{
    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{reRegistrationDAO}")
    private ReRegistrationDAO reRegistrationDAO;
    @ManagedProperty(value = "#{productDAO}")
    private ProductDAO productDAO;
    @ManagedProperty(value = "#{innService}")
    private InnService innService;
    @ManagedProperty(value = "#{attachmentDAO}")
    private AttachmentDAO attachmentDAO;
    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;
    @ManagedProperty(value = "#{atcService}")
    private AtcService atcService;
    @ManagedProperty(value = "#{companyService}")
    CompanyService companyService;

    private FacesContext facesContext;
    java.util.ResourceBundle bundle;

    private ReRegistration reApp;
    private ProdApplications prodApp;
    private Product product;
    private LicenseHolder licenseHolder;

    private ProdTable prodTable;
    private User curUser;
    private int selectedTab;
    private List<ProdInn> selectedInns = new ArrayList<ProdInn>();
    private List<ProdExcipient> selectedExipients = new ArrayList<ProdExcipient>();
    private List<ForeignAppStatus> foreignAppStatuses = new ArrayList<ForeignAppStatus>();
    private List<Atc> selectedAtcs = new ArrayList<Atc>();
    private List<ProdCompany> companies = new ArrayList<ProdCompany>();
    private ForeignAppStatus selForeignAppStatus = new ForeignAppStatus();
    private ProdInn prodInn = new ProdInn();
    private ProdExcipient prodExcipient = new ProdExcipient();
    private Atc atc;
    private Company selectedCompany;
    private List<String> companyTypes;
    private boolean showGMP;

    @PostConstruct
    private void init() {
        Long procId = getParam("ProcID");
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
        if (procId==null) { //this is new process
            Long prodAppId = getParam("prodAppId");
            if (prodAppId==null) return;
            initNew(prodAppId);
        }else{
            reApp = reRegistrationDAO.findOne(procId);
            prodApp = reApp.getProdApplications();
            companyService.findCompanyByProdID(product.getId());
        }
        readProdAppEager();
        initAddForeignStatus();
    }

    private void initNew(Long prodId){
        reApp = new ReRegistration();
        Date dt = Calendar.getInstance().getTime();
       // reApp = (ReRegistration) Scrooge.updateRecordInfo(reApp,curUser);
        reApp.setSubmitDate(dt);
        reApp.setCreator(curUser);
        Product reg_product;
        reg_product = productDAO.findProductEager(prodId);
        //prodApp = reg_product.getProdApplicationses().get(0);
        prodApp = prodApplicationsService.findActiveProdAppByProd(reg_product.getId());
        if (prodApp.getCreatedBy()==null)
            prodApp.setCreatedBy(prodApp.getApplicantUser());
        reApp.setProdApplications(prodApp);
        product = new Product();
        Long id =  prodApp.getProduct().getId();
        Scrooge.copyData(reg_product,product);
    }

    private void readProdAppEager(){
        Hibernate.initialize(prodApp.getProdAppAmdmts());
        Hibernate.initialize(prodApp.getReviewInfos());
        Hibernate.initialize(prodApp.getReviews());
        Hibernate.initialize(prodApp.getCreatedBy().getRoles());
        Hibernate.initialize(prodApp.getProduct().getProdApplicationses());
    }

    public LicenseHolder getLicenseHolder() {
        if(licenseHolder==null){
            List<LicenseHolder> licenseHolders = licenseHolderService.findLicHolderByApplicant(prodApp.getApplicant().getApplcntId());
            licenseHolder = licenseHolders.get(0);
        }
        return licenseHolder;
    }

    private Long getParam(String parameter){
        facesContext = FacesContext.getCurrentInstance();
        String procId=null;
        if (facesContext.getExternalContext().getFlash()!=null)
            procId = (String) facesContext.getExternalContext().getFlash().get(parameter);
        if (procId==null){
            if (facesContext.getExternalContext().getRequestParameterMap()!=null)
                procId = facesContext.getExternalContext().getRequestParameterMap().get(parameter);
        }
        if (procId!=null){
            return Long.parseLong(procId);
        }
        return null;
    }

    /**
     * Saves renew application to process data field as text
     */
    public String saveApp(){
        ObjectMapper mapper = new ObjectMapper();
        String dataJSON = null;
        try {
            reApp.setCreator(curUser);
            reApp.setLastModifiedBy(curUser);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            dataJSON = ow.writeValueAsString(product);

            reApp.setData(dataJSON);
            reApp = reRegistrationDAO.saveAndFlush(reApp);
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "error"+e.getMessage();
        }
    }

    public String cancel() {
        try {
            return "/internal/processrereglist.faces";
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
            return "";
        }
    }

    public String onFlowProcess(FlowEvent event) {
        String currentWizardStep = event.getOldStep();
        String nextWizardStep = event.getNewStep();
        String saveRes = saveApp();
        if ("".equals(saveRes))
            return nextWizardStep;
        else
            return currentWizardStep;
    }


    public String removeInn(ProdInn prodInn) {
        facesContext = FacesContext.getCurrentInstance();
        try {
            selectedInns.remove(prodInn);
            innService.removeProdInn(prodInn);
            facesContext.addMessage(null, new FacesMessage(bundle.getString("inn_removed")));
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }
        return null;
    }

    public String removeExcipient(ProdExcipient prodExcipient) {
        facesContext = FacesContext.getCurrentInstance();
        try {
            selectedExipients.remove(prodExcipient);
            innService.removeExcipient(prodExcipient);
            facesContext.addMessage(null, new FacesMessage(bundle.getString("expnt_removed")));
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }
        return null;
    }

    public void onRowEdit(RowEditEvent event) {
        ProdExcipient prodExcipient;
        ProdInn prodInn;
        FacesMessage msg = null;
        try {
            if (event.getObject() instanceof ProdExcipient) {
                prodExcipient = (ProdExcipient) event.getObject();
                msg = new FacesMessage(prodExcipient.getExcipient().getName() + " updated");
            } else if (event.getObject() instanceof ProdInn) {
                prodInn = (ProdInn) event.getObject();
                msg = new FacesMessage(prodInn.getInn().getName() + " updated");
            }

            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }

    }

    public void onRowCancel(RowEditEvent event) {
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

    public void openAddATC() {
        RegATCHelper regATCHelper = new RegATCHelper(atc, globalEntityLists);
    }
    public String addProdInn() {
        try {
            if (prodInn.getInn().getId() == null)
                prodInn.setInn(innService.saveInn(prodInn.getInn()));
            else{
                int id = prodInn.getDosUnit().getId();
                prodInn.setDosUnit(dosageFormService.findDosUom(id));
            }

            prodInn.setProduct(product);
            prodInn.setDosUnit(dosageFormService.findDosUom(prodInn.getDosUnit().getId()));
            List<ProdInn> si = getSelectedInns();
            si.add(prodInn);
            setSelectedInns(si);
            List<Atc> a = atcService.findAtcByName(prodInn.getInn().getName());
            if (a != null) {
                if (selectedAtcs == null)
                    selectedAtcs = new ArrayList<Atc>();
                selectedAtcs.addAll(a);
            }
            prodInn = new ProdInn();
            prodInn.setDosUnit(new DosUom());
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("rereg:inntable");
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            facesContext.addMessage(null, new FacesMessage(bundle.getString("product_innname_valid")));

        }
        return null;
    }

    public String addProdExcipient() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            if (prodExcipient.getExcipient().getId() == null)
                prodExcipient.setExcipient(innService.saveExcipient(prodExcipient.getExcipient()));
            else
                prodExcipient.setDosUnit(dosageFormService.findDosUom(prodExcipient.getDosUnit().getId()));

            prodExcipient.setProduct(product);
            prodExcipient.setDosUnit(dosageFormService.findDosUom(prodExcipient.getDosUnit().getId()));
            selectedExipients.add(prodExcipient);
            prodExcipient = new ProdExcipient();
            prodExcipient.setDosUnit(new DosUom());
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("rereg:expnttable");
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }

        return null;
    }

    public void removeAppStatus(ForeignAppStatus foreignAppStatus) {
        facesContext = FacesContext.getCurrentInstance();
        try {
            foreignAppStatuses.remove(foreignAppStatus);
            prodApplicationsService.removeForeignAppStatus(foreignAppStatus);
            facesContext.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
        try {
            if (foreignAppStatuses == null) {
                foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApp.getId());
                if (foreignAppStatuses == null)
                    foreignAppStatuses = new ArrayList<ForeignAppStatus>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }

        return foreignAppStatuses;
    }

    public String addForStatus() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            if (foreignAppStatuses == null) {
                foreignAppStatuses = new ArrayList<ForeignAppStatus>();
            }
            selForeignAppStatus.setProdApplications(prodApp);
            RetObject retObject = prodApplicationsService.saveForeignAppStatus(selForeignAppStatus);
            if(retObject.getMsg().equals("persist")) {
                selForeignAppStatus = (ForeignAppStatus) retObject.getObj();
                foreignAppStatuses.add(selForeignAppStatus);
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                initAddForeignStatus();
            }else{
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), retObject.getMsg()));
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
        }
        return "";
    }

    public void initAddForeignStatus() {
        selForeignAppStatus = new ForeignAppStatus();
        selForeignAppStatus.setCountry(new Country());
    }

    public String cancelAdd() {
        selForeignAppStatus = null;
        return null;
    }

    public ForeignAppStatus getSelForeignAppStatus() {
        if (selForeignAppStatus == null)
            initAddForeignStatus();
        return selForeignAppStatus;
    }

    public void setSelForeignAppStatus(ForeignAppStatus selForeignAppStatus) {
        this.selForeignAppStatus = selForeignAppStatus;
    }

    public List<Company> completeCompany(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getManufacturers());
    }

    public void initAddCompany() {
        selectedCompany = new Company();
    }

    public void removeCompany(ProdCompany selectedCompany) {
        facesContext = FacesContext.getCurrentInstance();
        try {
            companies.remove(selectedCompany);
            companyService.removeProdCompany(selectedCompany);

            facesContext.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
        }

    }

    public String addCompany() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            if (selectedCompany!=null){
                for(int i=0;i<companyTypes.size();i++) {
                    ProdCompany prodCompany = new ProdCompany();
                    prodCompany.setProduct(product);
                    prodCompany.setCompany(selectedCompany);
                    CompanyType ct = CompanyType.valueOf(companyTypes.get(i));
                    prodCompany.setCompanyType(ct);
                    companies.add(prodCompany);
                }
                setCompanies(companies);
            }else{
                throw new Exception(bundle.getString("valid_value_req"));
            }
            List<ProdCompany> prodCompanies = companyService.addCompany(getProduct(), selectedCompany, companyTypes);
            if (prodCompanies == null) {
                facesContext.addMessage(null, new FacesMessage(bundle.getString("valid_value_req")));
            } else {
                setCompanies(prodCompanies);
            }
            facesContext.addMessage(null, new FacesMessage(bundle.getString("company_add_success")));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
        }
        return null;
    }
    public void companyChangeEventListener(SelectEvent event) {
        gmpChangeListener();


    }

    public void companyChangeEventListener(AjaxBehaviorEvent event) {
        gmpChangeListener();
    }

    public void gmpChangeListener() {
        if (selectedCompany!=null)
            if (selectedCompany.isGmpInsp())
                showGMP = true;
            else
                showGMP = false;
    }

    public void  setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatus){
        this.foreignAppStatuses = foreignAppStatuses;
    }

    public void setLicenseHolder(LicenseHolder licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public ProdApplications getProdApp() {
        return prodApp;
    }

    public void setProdApp(ProdApplications prodApp) {
        this.prodApp = prodApp;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public ReRegistrationDAO getReRegistrationDAO() {
        return reRegistrationDAO;
    }

    public void setReRegistrationDAO(ReRegistrationDAO reRegistrationDAO) {
        this.reRegistrationDAO = reRegistrationDAO;
    }

    public ReRegistration getReApp() {
        return reApp;
    }

    public void setReApp(ReRegistration reApp) {
        this.reApp = reApp;
    }

    public ProdTable getProdTable() {
        return prodTable;
    }

    public void setProdTable(ProdTable prodTable) {
        this.prodTable = prodTable;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<ProdInn> getSelectedInns() {
        return selectedInns;
    }

    public void setSelectedInns(List<ProdInn> selectedInns) {
        this.selectedInns = selectedInns;
    }

    public List<ProdExcipient> getSelectedExipients() {
        return selectedExipients;
    }

    public void setSelectedExipients(List<ProdExcipient> selectedExipients) {
        this.selectedExipients = selectedExipients;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
    }

    public InnService getInnService() {
        return innService;
    }

    public void setInnService(InnService innService) {
        this.innService = innService;
    }

    public ProdInn getProdInn() {
        return prodInn;
    }

    public void setProdInn(ProdInn prodInn) {
        this.prodInn = prodInn;
    }

    public ProdExcipient getProdExcipient() {
        return prodExcipient;
    }

    public void setProdExcipient(ProdExcipient prodExcipient) {
        this.prodExcipient = prodExcipient;
    }

    public AtcService getAtcService() {
        return atcService;
    }

    public void setAtcService(AtcService atcService) {
        this.atcService = atcService;
    }

    public List<Atc> getSelectedAtcs() {
        return selectedAtcs;
    }

    public void setSelectedAtcs(List<Atc> selectedAtcs) {
        this.selectedAtcs = selectedAtcs;
    }

    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public Atc getAtc() {
        return atc;
    }

    public void setAtc(Atc atc) {
        this.atc = atc;
    }

    public List<ProdCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(List<ProdCompany> companies) {
        this.companies = companies;
    }

    public List<String> getCompanyTypes() {
        return companyTypes;
    }

    public void setCompanyTypes(List<String> companyTypes) {
        this.companyTypes = companyTypes;
    }

    public boolean isShowGMP() {
        return showGMP;
    }

    public void setShowGMP(boolean showGMP) {
        this.showGMP = showGMP;
    }
}
