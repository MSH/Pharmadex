package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ApplicantHome implements Serializable {

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    WebSession webSession;

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
            applicant = webSession.getApplicant();
        }

        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
