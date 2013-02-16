package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class ProdAppMBean implements Serializable{
    private static final long serialVersionUID = -900861644263726931L;

    @Autowired
    ProdApplicationsService prodApplicationsService;

    @Autowired
    private UserSession userSession;

    private ProdApplications selectedApplication = new ProdApplications();
    private List<ProdApplications> prodApplicationsList;
    private List<ProdApplications> submmittedAppList;
    private List<ProdApplications> savedAppList;
    private boolean showAdd = false;
    private List<ProdApplications> allApplicationForProcess;



    public void onRowSelect(){
        setShowAdd(true);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedApplication.getProd().getProdName()));
    }

    @PostConstruct
    private void init(){
    }

    public String cancelApp(){
        setShowAdd(false);
//        selectedApplicant = new applicant();
        System.out.print("inside cancelUser");
        return "/secure/applicantlist.faces";
    }

    public ProdApplications getSelectedApplication() {
        return selectedApplication;
    }

    public void setSelectedApplication(ProdApplications selectedApplication) {
        this.selectedApplication = selectedApplication;
    }

    public List<ProdApplications> getProdApplicationsList() {
        if(prodApplicationsList==null)
            prodApplicationsList = prodApplicationsService.getSubmittedApplications(userSession.getLoggedInUserObj().getUserId());
        return prodApplicationsList;
    }

    public List<ProdApplications> getSavedAppList() {
        System.out.println("user id = "+userSession.getLoggedInUserObj());
        if(savedAppList==null)
            savedAppList = prodApplicationsService.getSavedApplications(userSession.getLoggedInUserObj().getUserId());
        return savedAppList;
    }

    public List<ProdApplications> getSubmmittedAppList() {
        if(submmittedAppList==null)
            submmittedAppList = prodApplicationsService.getSubmittedApplications(-1);
        return submmittedAppList;
    }

    public void setSubmmittedAppList(List<ProdApplications> submmittedAppList) {
        this.submmittedAppList = submmittedAppList;
    }

    public List<ProdApplications> getAllApplicationForProcess() {
        allApplicationForProcess =  prodApplicationsService.getApplications();
        return allApplicationForProcess;
    }

    public void setAllApplicationForProcess(List<ProdApplications> allApplicationForProcess) {
        this.allApplicationForProcess = allApplicationForProcess;
    }

    public void setSavedAppList(List<ProdApplications> savedAppList) {
        this.savedAppList = savedAppList;
    }

    public void setProdApplicationsList(List<ProdApplications> prodApplicationsList) {
        this.prodApplicationsList = prodApplicationsList;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }


}
