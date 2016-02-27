package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.InvoiceService;
import org.msh.pharmadex.service.ProdApplicationsService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ProdAppMBean implements Serializable {
    private static final long serialVersionUID = -900861644263726931L;
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    protected List<ProdApplications> prodApplicationsList;
    protected List<ProdApplications> submmittedAppList;
    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{invoiceService}")
    private InvoiceService invoiceService;
    private ProdApplications selectedApplication = new ProdApplications();
    private List<ProdApplications> savedAppList;
    private boolean showAdd = false;
    private List<ProdApplications> allApplicationForProcess;
    private List<ProdApplications> filteredApps;
    private List<ProdApplications> pendingRenewals;


    public String onRowSelect() {
//        setShowAdd(true);
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedApplication.getProd().getProdName()));
        return "/internal/processreg.faces";
    }

    @PostConstruct
    private void init() {
        savedAppList = prodApplicationsService.findSavedApps(userSession.getLoggedINUserID());
        pendingRenewals = invoiceService.findPendingByApplicant(userSession.getApplcantID());
        prodApplicationsList = prodApplicationsService.getSubmittedApplications(userSession);
        submmittedAppList = prodApplicationsService.getSubmittedApplications(userSession);
        allApplicationForProcess = prodApplicationsService.getApplications();
    }


    public String cancelApp() {
        setShowAdd(false);
//        selectedApplicant = new applicant();
        System.out.print("inside cancelUser");
        return "/secure/applicantlist.faces";
    }

    public List<ProdApplications> getPendingRenewals() {
        return pendingRenewals;
    }

    public void setPendingRenewals(List<ProdApplications> pendingRenewals) {
        this.pendingRenewals = pendingRenewals;
    }

    public ProdApplications getSelectedApplication() {
        return selectedApplication;
    }

    public void setSelectedApplication(ProdApplications selectedApplication) {
        this.selectedApplication = selectedApplication;
    }

    public List<ProdApplications> getProdApplicationsList() {
        return prodApplicationsList;
    }

    public void setProdApplicationsList(List<ProdApplications> prodApplicationsList) {
        this.prodApplicationsList = prodApplicationsList;
    }

    public List<ProdApplications> getSavedAppList() {
        return savedAppList;
    }

    public void setSavedAppList(List<ProdApplications> savedAppList) {
        this.savedAppList = savedAppList;
    }

    public List<ProdApplications> getSubmmittedAppList() {
        return submmittedAppList;
    }

    public void setSubmmittedAppList(List<ProdApplications> submmittedAppList) {
        this.submmittedAppList = submmittedAppList;
    }

    public List<ProdApplications> getAllApplicationForProcess() {
        return allApplicationForProcess;
    }

    public void setAllApplicationForProcess(List<ProdApplications> allApplicationForProcess) {
        this.allApplicationForProcess = allApplicationForProcess;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
}
