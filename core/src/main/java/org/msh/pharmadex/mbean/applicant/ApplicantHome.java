package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ApplicantHome implements Serializable {

    @Autowired
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
}
