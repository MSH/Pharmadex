/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReportService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdRejectBn implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    private String summary;

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void PDF() throws JRException, IOException {
        facesContext = FacesContext.getCurrentInstance();
        ProdApplications prodApplications = processProdBn.getProdApplications();
        if (!prodApplications.getRegState().equals(RegState.NOT_RECOMMENDED)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("register_fail")));
//            return "";
        }
        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.REJECTED);
//        prodApplications.setRegistrationDate(new Date());

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        processProdBn.getTimeLineList().add(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        prodApplications.getProd().setRegState(timeLine.getRegState());
//        prodApplications = prodApplicationsService.updateProdApp(prodApplications);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("status_change_success")));

        prodApplicationsService.createRejectCert(prodApplications);
        timeLine = new TimeLine();
//        return "";
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
