package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.InvoiceService;
import org.msh.pharmadex.service.ProdApplicationsService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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
@RequestScoped
public class ProdAppMBean implements Serializable {
    private static final long serialVersionUID = -900861644263726931L;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{invoiceService}")
    private InvoiceService invoiceService;
    private ProdApplications selectedApplication = new ProdApplications();
    private List<ProdApplications> prodApplicationsList;
    private List<ProdApplications> submmittedAppList;
    private List<ProdApplications> savedAppList;
    private boolean showAdd = false;
    private List<ProdApplications> allApplicationForProcess;
    private List<ProdApplications> filteredApps;
    private List<ProdApplications> pendingRenewals;

    public String onRowSelect() {
//        setShowAdd(true);
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedApplication.getProd().getProdName()));
        processProdBn = null;
        return "/internal/processreg.faces";
    }

    @PostConstruct
    private void init() {
    }

    public String cancelApp() {
        setShowAdd(false);
//        selectedApplicant = new applicant();
        System.out.print("inside cancelUser");
        return "/secure/applicantlist.faces";
    }

    public List<ProdApplications> getPendingRenewals() {
        if (pendingRenewals == null)
            pendingRenewals = invoiceService.findPendingByApplicant(userSession.getLoggedInUserObj());
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
        if (prodApplicationsList == null)
            prodApplicationsList = prodApplicationsService.getSubmittedApplications(userSession);
        return prodApplicationsList;
    }

    public void setProdApplicationsList(List<ProdApplications> prodApplicationsList) {
        this.prodApplicationsList = prodApplicationsList;
    }

    public List<ProdApplications> getSavedAppList() {
        if (savedAppList == null)
            savedAppList = prodApplicationsService.findSavedApps(userSession.getLoggedInUserObj());
        return savedAppList;
    }

    public void setSavedAppList(List<ProdApplications> savedAppList) {
        this.savedAppList = savedAppList;
    }

    public List<ProdApplications> getSubmmittedAppList() {
        if (submmittedAppList == null)
            submmittedAppList = prodApplicationsService.getSubmittedApplications(userSession);
        return submmittedAppList;
    }

    public void setSubmmittedAppList(List<ProdApplications> submmittedAppList) {
        this.submmittedAppList = submmittedAppList;
    }

    public List<ProdApplications> getAllApplicationForProcess() {
        allApplicationForProcess = prodApplicationsService.getApplications();
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

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
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
