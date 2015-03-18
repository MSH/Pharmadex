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
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdAppChecklistService;
import org.msh.pharmadex.service.ReportService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.util.RetObject;

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
public class ProdDeficiencyBn implements Serializable {

    @ManagedProperty(value = "#{prodAppChecklistService}")
    ProdAppChecklistService prodAppChecklistService;
    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;
    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    private List<ProdAppChecklist> prodAppChecklists;
    private String summary;
    private FacesContext context;
    private JasperPrint jasperPrint;


    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.generateDeficiency(prodAppChecklists, summary, userSession.getLoggedInUserObj());
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=deficiency_letter.pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        context.responseComplete();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        ProdApplications prodApplications;
        prodApplications = processProdBn.getProdApplications();
        prodApplications.setProdAppChecklists(prodAppChecklists);
        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.FOLLOW_UP);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        timeLine.setComment(summary);
        timeLine.setProdApplications(prodApplications);
        prodApplications.setRegState(timeLine.getRegState());
        prodApplications.getProd().setRegState(timeLine.getRegState());
        RetObject retObject = timelineService.saveTimeLine(timeLine);
        if (retObject.getMsg().equals("persist")) {
            timeLine = (TimeLine) retObject.getObj();
            processProdBn.setTimeLine(timeLine);
            processProdBn.getTimeLineList().add(timeLine);
            processProdBn.setProdApplications(timeLine.getProdApplications());
            processProdBn.setProduct(timeLine.getProdApplications().getProd());
//            processProdBn.save();
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
        }
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        if (prodAppChecklists == null) {
            prodAppChecklists = prodAppChecklistService.findProdAppChecklistByProdApp(processProdBn.getProdApplications().getId());
            for (ProdAppChecklist pacs : prodAppChecklists) {
                pacs.setSendToApp(!pacs.isStaffValue());
            }
        }
        return prodAppChecklists;
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
}