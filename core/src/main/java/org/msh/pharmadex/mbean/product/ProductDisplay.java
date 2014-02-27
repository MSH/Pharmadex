package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.Flash;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ProductDisplay {

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

    @ManagedProperty("#{flash}")
    private Flash flash;

    public Product getProduct() {
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setProduct(Product product) {
        this.product = productService.findProductById(product.getId());
        prodApplications = prodApplicationsService.findProdApplicationByProduct(product.getId());
        prodApplications.setProdAppChecklists(prodApplicationsService.findAllProdChecklist(prodApplications.getId()));
        this.product.setInns(innService.findInnByProdApp(prodApplications.getId()));
        this.product.setCompanies(prodApplicationsService.findCompanies(this.product.getId()));
        this.product.setAtcs(productService.findAtcsByProduct(product.getId()));
        prodApplications.setProd(this.product);
        this.product.setProdApplications(prodApplications);
        this.applicant = product.getApplicant();
    }


    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }
}
