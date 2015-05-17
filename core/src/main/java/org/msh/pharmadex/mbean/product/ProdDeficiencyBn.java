/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
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
@ViewScoped
public class ProdDeficiencyBn implements Serializable {

    @ManagedProperty(value = "#{prodAppChecklistService}")
    ProdAppChecklistService prodAppChecklistService;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;
    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    private List<ProdAppChecklist> prodAppChecklists;
    private String summary;
    private FacesContext context;
    private JasperPrint jasperPrint;
    private ProdApplications prodApplications;

    public void PDF() throws JRException, IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("prodAppID", prodApplications.getId());
        context = FacesContext.getCurrentInstance();
        User user = userService.findUser(userSession.getLoggedINUserID());
        jasperPrint = reportService.generateDeficiency(prodAppChecklists, summary);
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=deficiency_letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        context.responseComplete();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.FOLLOW_UP);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(user);
        timeLine.setComment(summary);
        timeLine.setProdApplications(prodApplications);
        prodApplications.setRegState(timeLine.getRegState());
        RetObject retObject = timelineService.saveTimeLine(timeLine);
        if (!retObject.getMsg().equals("persist")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
        }
    }

    public String sendToHome(){
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("prodAppID", prodApplications.getId());
        return "/internal/processreg";
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        if (prodAppChecklists == null) {
            initDefBn();
        }
        return prodAppChecklists;
    }

    private void initDefBn() {
        Long prodAppID = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("prodAppID");
        if(prodAppID!=null) {
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            prodAppChecklists = prodAppChecklistService.findProdAppChecklistByProdApp(prodAppID);
            for (ProdAppChecklist pacs : prodAppChecklists) {
                pacs.setSendToApp(!pacs.isStaffValue());
            }
            FacesContext.getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
        }
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public ProdAppChecklistService getProdAppChecklistService() {
        return prodAppChecklistService;
    }

    public void setProdAppChecklistService(ProdAppChecklistService prodAppChecklistService) {
        this.prodAppChecklistService = prodAppChecklistService;
    }

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public ProdApplications getProdApplications() {
        if(prodApplications==null)
            initDefBn();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
