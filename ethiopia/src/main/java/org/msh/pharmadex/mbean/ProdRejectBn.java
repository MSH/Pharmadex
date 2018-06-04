/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.UserService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRejectBn implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    private String summary;
    private ProdApplications prodApplications;


    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    @PostConstruct
    public void init() {
        try {
            if (prodApplications == null) {
                String prodAppID = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("prodAppID");
                if (prodAppID != null && !prodAppID.equals("")) {
                    prodApplications = prodApplicationsService.findProdApplications(Long.valueOf(prodAppID));

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void PDF() throws JRException, IOException {
        facesContext = FacesContext.getCurrentInstance();
        if (!prodApplications.getRegState().equals(RegState.NOT_RECOMMENDED)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("register_fail")));
//            return "";
        }
        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.REJECTED);
//        prodApplications.setRegistrationDate(new Date());

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userService.findUser(userSession.getLoggedINUserID()));
        timeLine.setComment(summary);
        processProdBn.getTimeLineList().add(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        prodApplications.setRegState(timeLine.getRegState());
//        prodApplications = prodApplicationsService.updateProdApp(prodApplications);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("status_change_success")));

        prodApplicationsService.createRejectCert(prodApplications, summary);
        timeLine = new TimeLine();
//        return "";
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }
}
