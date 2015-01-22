package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.service.LicenseHolderService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by usrivastava on 01/14/2015.
 */
@ManagedBean
@ViewScoped
public class RegHomeMbeanET implements Serializable{

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{regHomeMbean}")
    private RegHomeMbean regHomeMbean;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private LicenseHolder licenseHolder;

    @PostConstruct
    private void init() {
        ProdApp prodApp = userSession.getProdApp();
        if (prodApp != null) {
            regHomeMbean.getProdApplications().setProdAppType(prodApp.getProdAppType());
            regHomeMbean.getProdApplications().setSra(prodApp.isSRA());
            regHomeMbean.getProdApplications().setFastrack(prodApp.isEml());
            regHomeMbean.getProdApplications().setFeeAmt(prodApp.getTotalfee());
        }
    }

    public LicenseHolder getLicenseHolder() {
        if(licenseHolder==null){
            licenseHolder = licenseHolderService.findLicHolderByApplicant(regHomeMbean.getApplicant().getApplcntId());
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

    public RegHomeMbean getRegHomeMbean() {
        return regHomeMbean;
    }

    public void setRegHomeMbean(RegHomeMbean regHomeMbean) {
        this.regHomeMbean = regHomeMbean;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
