/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReportService;
import org.msh.pharmadex.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentFormET extends ProdConsentForm implements Serializable {

    private FacesContext context;
    java.util.ResourceBundle bundle;

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @Override
    public String submitApp() {
        context = FacesContext.getCurrentInstance();
        bundle = context.getApplication().getResourceBundle(context, "msgs");

        String retValue = licenseHolderService.saveProduct(getProdApplications().getApplicant().getApplcntId(), getProduct());
        if(!retValue.equals("persist"))
            context.addMessage(null, new FacesMessage(bundle.getString("global_fail")));
        return super.submitApp();
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }
}
