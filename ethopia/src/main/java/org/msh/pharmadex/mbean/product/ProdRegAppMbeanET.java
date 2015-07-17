package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.LicenseHolderService;

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

    @PostConstruct
    private void init() {
        ProdAppInit prodAppInit = userSession.getProdAppInit();
        if (prodAppInit != null && prodAppInit.getLicHolderID() != null) {
            licenseHolder = licenseHolderService.findLicHolder(prodAppInit.getLicHolderID());
            Applicant applicant = licenseHolderService.findApplicantByLicHolder(licenseHolder.getId());
            if (applicant != null) {
                prodRegAppMbean.setApplicant(applicant);
                User applicantUser = applicant.getUsers().get(0);
                prodRegAppMbean.setApplicantUser(applicantUser);
                prodRegAppMbean.getProdApplications().setApplicant(applicant);
                prodRegAppMbean.getProdApplications().setApplicantUser(applicantUser);
            }
        }

    }

    public LicenseHolder getLicenseHolder() {
        if(licenseHolder==null){
            List<LicenseHolder> licenseHolders;
            if (prodRegAppMbean.getApplicant().getApplcntId() != null) {
                licenseHolders = licenseHolderService.findLicHolderByApplicant(prodRegAppMbean.getApplicant().getApplcntId());
                if (licenseHolders != null && licenseHolders.size() < 2)
                    licenseHolder = licenseHolders.get(0);
            }else{
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
}
