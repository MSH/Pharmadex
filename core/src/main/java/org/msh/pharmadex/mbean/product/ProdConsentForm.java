/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.omnifaces.util.Faces;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentForm implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;

    private String password;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext context;
    private JasperPrint jasperPrint;
    java.util.ResourceBundle bundle;

    @PostConstruct
    private void init(){
        Long prodAppID = userSession.getProdAppID();
        if(prodAppID!=null){
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            product = prodApplications.getProduct();
        }

    }

    @Transactional
    public String submitApp() {
        context = FacesContext.getCurrentInstance();
        bundle = context.getApplication().getResourceBundle(context, "msgs");
        User user = userService.findUser(userSession.getLoggedINUserID());

        if (!userService.verifyUser(userSession.getLoggedINUserID(), password)) {
            context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));

        }

        prodApplications.setProdAppNo(prodApplicationsService.generateAppNo(prodApplications));

        List<TimeLine> timeLines = new ArrayList<TimeLine>();
        TimeLine timeLine = new TimeLine();
        timeLine.setComment(bundle.getString("timeline_newapp"));
        timeLine.setRegState(RegState.NEW_APPL);
        timeLine.setProdApplications(prodApplications);
        timeLine.setUser(user);
        timeLine.setStatusDate(new Date());
        timeLines.add(timeLine);

//        prodApplications.setTimeLines(timeLines);
        prodApplications.setRegState(RegState.NEW_APPL);
        prodApplications.setSubmitDate(new Date());
        prodApplicationsService.updateProdApp(prodApplications, userSession.getLoggedINUserID());
//        saveApp();

        context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));


//        timelineService.saveTimeLine(timeLine);
        return "/secure/prodregack.faces";
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.reportinit(prodApplications);
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

    }

    public String cancel() {
        context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        return "/public/registrationhome.faces";
    }



    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
