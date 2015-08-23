/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.ProdApplicationsServiceET;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentFormET extends ProdConsentForm implements Serializable {

    java.util.ResourceBundle bundle;
    private FacesContext context;
    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    private ProdApplicationsServiceET prodApplicationsServiceET;

    @Override
    public String submitApp() {
        getProdApplications().setProdAppNo(prodApplicationsServiceET.generateAppNo(getProdApplications()));
        return super.submitApp();
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }
}
