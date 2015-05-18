package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import java.util.Map;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProductDisplay implements Serializable {

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{applicantService}")
    ApplicantService applicantService;

    @ManagedProperty(value = "#{productService}")
    ProductService productService;

    @ManagedProperty(value = "#{innService}")
    InnService innService;

    @ManagedProperty(value = "#{atcService}")
    AtcService atcService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    private Product product;

    private Applicant applicant;

    private ProdApplications prodApplications;

    public String sentToDetail() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("appID", applicant.getApplcntId());
        return "applicantdetail";
    }

    public Product getProduct() {
        if (product == null) {
            FacesContext.getCurrentInstance().getExternalContext().getFlash();
            initFields();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    private void initFields() {
        Long prodAppID = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("prodAppID");
        if(prodAppID!=null) {
            prodApplications = prodApplicationsService.findProdApplicationByProduct(prodAppID);
            product = prodApplications.getProduct();
            applicant = prodApplications.getApplicant();
            FacesContext.getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
        }
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null)
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

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public InnService getInnService() {
        return innService;
    }

    public void setInnService(InnService innService) {
        this.innService = innService;
    }

    public AtcService getAtcService() {
        return atcService;
    }

    public void setAtcService(AtcService atcService) {
        this.atcService = atcService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
