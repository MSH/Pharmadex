package org.msh.pharmadex.mbean.applicant;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.util.JsfUtils;

/**
 * Backing bean for the process applicant list page
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProcessAppListBn implements Serializable {

    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    private Applicant applicant;
    private List<Applicant> pendingApps;
    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;
    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

   /* public String goToAppDetail() {
        return "/internal/processapp.faces";
    }*/

    public List<Applicant> completeApplicantList(String query) {
        return JsfUtils.completeSuggestions(query, applicantService.findAllApplicants(null));
    }

    public String searchApplicant() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (applicant != null) {
            userSession.setApplcantID(applicant.getApplcntId());
            return "/internal/processapp.faces";
        } else {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global_fail"), bundle.getString("global_fail")));
            return null;
        }
    }

    public List<Applicant> getPendingApps() {
    	if(userSession.getWorkspaceName().equals("Ethiopia")){
    		if (pendingApps == null)
                pendingApps = applicantService.getApplicantsNotRegistered();
    	}else{
    		if (pendingApps == null)
                pendingApps = applicantService.getPendingApplicants();
    	}
        
        return pendingApps;
    }
    
    public void setPendingApps(List<Applicant> pendingApps) {
    	this.pendingApps = pendingApps;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
