package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ApplicantHome implements Serializable {

    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;

    private Applicant applicant;

    private List<Product> products;

    public List<Product> getProducts() {
        if (products == null)
            products = applicantService.findRegProductForApplicant(getApplicant().getApplcntId());
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Applicant getApplicant() {
        if (applicant == null) {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String applicantID = params.get("applicantID");
            applicant = applicantService.findApplicant(Long.valueOf(applicantID));
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
