package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;

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

    private Applicant applicant;

    private List<ProdApplications> prodApplicationses;

    public String sentToDetail(Long id) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        return "productdetail";
    }

    public List<ProdApplications> getProdApplicationses() {
        if (prodApplicationses == null&&getApplicant()!=null)
            prodApplicationses = applicantService.findRegProductForApplicant(getApplicant().getApplcntId());

        return prodApplicationses;
    }

    public void setProdApplicationses(List<ProdApplications> prodApplicationses) {
        this.prodApplicationses = prodApplicationses;
    }

    public Applicant getApplicant() {
        if (applicant == null) {
            Long applicantID = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("appID");
            if(applicantID!=null)
                applicant = applicantService.findApplicant(applicantID);
        }

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
}
