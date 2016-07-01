package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ApplicantHome implements Serializable {

    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;

    @ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
    
    private Applicant applicant;

    private List<ProdApplications> prodApplicationses;
    private List<ProdApplications> prodNotRegApplicationses;
    
    private List<ProdApplications> filteredProdApplicationses;
    private List<ProdApplications> filteredProdNotRegApplicationses;
    
    public String sentToDetail(Long id) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        return "productdetail";
    }

    public List<ProdApplications> getProdApplicationses() {
        if (prodApplicationses == null && getApplicant() != null)
            prodApplicationses = applicantService.findRegProductForApplicant(getApplicant().getApplcntId());

        return prodApplicationses;
    }

    public void setProdApplicationses(List<ProdApplications> prodApplicationses) {
        this.prodApplicationses = prodApplicationses;
    }
    
    public List<ProdApplications> getProdNotRegApplicationses() {
        if (prodNotRegApplicationses == null && getApplicant() != null)
        	prodNotRegApplicationses = applicantService.findProductNotRegForApplicant(getApplicant().getApplcntId());

        return prodNotRegApplicationses;
    }

    public void setProdNotRegApplicationses(List<ProdApplications> prodNotRegApplicationses) {
        this.prodNotRegApplicationses = prodNotRegApplicationses;
    }
    
    public List<ProdApplications> getFilteredProdApplicationses() {
        return filteredProdApplicationses;
    }

    public void setFilteredProdApplicationses(List<ProdApplications> filteredProdApplicationses) {
        this.filteredProdApplicationses = filteredProdApplicationses;
    }
    
    public List<ProdApplications> getFilteredProdNotRegApplicationses() {
        return filteredProdNotRegApplicationses;
    }

    public void setFilteredProdNotRegApplicationses(List<ProdApplications> filteredProdNotRegApplicationses) {
        this.filteredProdNotRegApplicationses = filteredProdNotRegApplicationses;
    }

    public Applicant getApplicant() {
    	if (applicant == null) {
			if (userSession.getApplcantID() != null) {
				applicant = applicantService.findApplicant(userSession.getApplcantID());
			} else {
				applicant = null;
			}
		}
        /*if (applicant == null) {
            Long applicantID = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("appID");
            if(applicantID!=null)
                applicant = applicantService.findApplicant(applicantID);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().keep("appID");
        }*/

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
    
    public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
}
