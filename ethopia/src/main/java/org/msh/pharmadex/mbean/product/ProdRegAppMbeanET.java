package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRegAppMbeanET extends ProdRegAppMbean implements Serializable {

    private LicenseHolder licenseHolder;

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{appSelectMBean}")
    private AppSelectMBean appSelectMBean;

    @PostConstruct
    private void init() {

    }

    public LicenseHolder getLicenseHolder() {
        if(licenseHolder==null){
            if(getApplicant().getApplcntId()!=null) {
                licenseHolder = licenseHolderService.findLicHolderByApplicant(getApplicant().getApplcntId());
            }else{
                licenseHolder = licenseHolderService.findLicHolderByApplicant(appSelectMBean.getSelectedApplicant().getApplcntId());
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
}
