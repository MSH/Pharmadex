package org.msh.pharmadex.mbean.product;

import org.bouncycastle.LICENSE;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.service.LicenseHolderService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ResourceBundle;

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

    private LicenseHolder licenseHolder;

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
}
