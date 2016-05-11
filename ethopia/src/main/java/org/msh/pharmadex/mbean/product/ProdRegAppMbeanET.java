package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.DosageFormService;
import org.msh.pharmadex.service.FastTrackMedService;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.util.RetObject;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRegAppMbeanET implements Serializable {

    private LicenseHolder licenseHolder;

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{prodRegAppMbean}")
    private ProdRegAppMbean prodRegAppMbean;

    @ManagedProperty(value = "#{appSelectMBean}")
    private AppSelectMBean appSelectMBean;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{fastTrackMedService}")
    private FastTrackMedService fastTrackMedService;

    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;

    private Product product;

    private List<DosUom> dosUoms;

    private DosUom dosUom;

    @PostConstruct
    private void init() {
        ProdAppInit prodAppInit = userSession.getProdAppInit();
        if (prodAppInit != null && prodAppInit.getLicHolderID() != null) {
            licenseHolder = licenseHolderService.findLicHolder(prodAppInit.getLicHolderID());
            Applicant applicant = licenseHolderService.findApplicantByLicHolder(licenseHolder.getId());
            User applicantUser = null;
            if (applicant != null) {
                prodRegAppMbean.setApplicant(applicant);
                if (applicant != null && applicant.getUsers() != null && applicant.getUsers().size() > 0) {
                    applicantUser = applicant.getUsers().get(0);
                }
                prodRegAppMbean.setApplicantUser(applicantUser);
                prodRegAppMbean.getProdApplications().setApplicant(applicant);
                prodRegAppMbean.getProdApplications().setApplicantUser(applicantUser);
            }
            prodAppInit = null;
        } else {
            if (prodRegAppMbean.getProdApplications() != null) {
                licenseHolder = licenseHolderService.findLicHolderByProduct(prodRegAppMbean.getProdApplications().getProduct().getId());
            }
        }
        product = prodRegAppMbean.getProduct();
        dosUom = new DosUom();
    }

    @Transactional
    public void saveApp() {
        prodRegAppMbean.saveApp();
        RetObject retObject;
        try {
            ProdApplications prodApplications = prodRegAppMbean.getProdApplications();
            Product product = prodRegAppMbean.getProduct();
            if (licenseHolder != null && prodRegAppMbean.getProduct() != null) {
                retObject = licenseHolderService.saveProduct(licenseHolder, product);
                licenseHolder = (LicenseHolder) retObject.getObj();

            }
            if (fastTrackMedService.genmedExists(product.getGenName())) {
                prodApplications.setFastrack(true);
            } else {
                prodApplications.setFastrack(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public LicenseHolder getLicenseHolder() {
        if (licenseHolder == null) {
            List<LicenseHolder> licenseHolders;
            if (prodRegAppMbean.getApplicant().getApplcntId() != null) {
                licenseHolders = licenseHolderService.findLicHolderByApplicant(prodRegAppMbean.getApplicant().getApplcntId());
                if (licenseHolders != null && licenseHolders.size() > 0)
                    licenseHolder = licenseHolders.get(0);
            } else {
                if (appSelectMBean.getSelectedApplicant() != null && appSelectMBean.getSelectedApplicant().getApplcntId() != null) {
                    licenseHolders = licenseHolderService.findLicHolderByApplicant(appSelectMBean.getSelectedApplicant().getApplcntId());
                    if (licenseHolders != null && licenseHolders.size() < 2)
                        licenseHolder = licenseHolders.get(0);
                }
            }
        }
        return licenseHolder;
    }

    public void setLicenseHolder(LicenseHolder licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public AppSelectMBean getAppSelectMBean() {
        return appSelectMBean;
    }

    public void setAppSelectMBean(AppSelectMBean appSelectMBean) {
        this.appSelectMBean = appSelectMBean;
    }

    public ProdRegAppMbean getProdRegAppMbean() {
        return prodRegAppMbean;
    }

    public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
        this.prodRegAppMbean = prodRegAppMbean;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public FastTrackMedService getFastTrackMedService() {
        return fastTrackMedService;
    }

    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setFastTrackMedService(FastTrackMedService fastTrackMedService) {
        this.fastTrackMedService = fastTrackMedService;
    }

    public List<DosUom> getDosUoms() {
        if (dosUoms==null)
            dosUoms = prodRegAppMbean.getGlobalEntityLists().getDosUoms();
        return dosUoms;
    }

    public void setDosUoms(List<DosUom> dosUoms) {
        this.dosUoms = dosUoms;
    }

    public void initNewUom() {
        if (product.getDosUnit()==null){
            dosUom = new DosUom();
            product.setDosUnit(dosUom);
        }else if (product.getDosUnit().getUom()==null){
            dosUom = new DosUom();
            product.setDosUnit(dosUom);
        }
    }

    public void uomSave(){
        //if (dosUom.getUom()==null) return;
        DosUom uom = dosageFormService.saveDosUom(product.getDosUnit().getUom());
        prodRegAppMbean.getGlobalEntityLists().getDosUoms();
        dosUoms.add(uom);
        product.setDosUnit(uom);
    }

    public DosUom getDosUom() {
        return dosUom;
    }
    public void setDosUom(DosUom dosUom) {
        this.dosUom = dosUom;
    }


}
