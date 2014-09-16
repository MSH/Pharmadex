package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean for the process applicant list page
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ProcessAppListBn {

    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    private Applicant applicant;
    private List<Applicant> pendingApps;
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private GlobalEntityLists globalEntityLists;
    @Autowired
    private UserSession userSession;

    public String goToAppDetail() {
        return "/internal/processapp.faces";
    }

    public List<Applicant> completeApplicantList(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getRegApplicants());
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
        if (pendingApps == null)
            pendingApps = applicantService.getPendingApplicants();
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

}
