package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ProductDisplay implements Serializable {

    @Autowired
    ProdApplicationsService prodApplicationsService;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    ProductService productService;

    @Autowired
    InnService innService;

    @Autowired
    AtcService atcService;

    private Product product;

    private Applicant applicant;

    private ProdApplications prodApplications;

    public Product getProduct() {
        if(product==null){
            initFields();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    private void initFields() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String prodID = params.get("id");
        product = productService.findProduct(Long.valueOf(prodID));
        this.prodApplications = this.product.getProdApplications();
        this.applicant = this.product.getApplicant();
    }

    public ProdApplications getProdApplications() {
        if(prodApplications==null)
            initFields();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
