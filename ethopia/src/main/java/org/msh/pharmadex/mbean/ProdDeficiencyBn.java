/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ReportService;

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

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    private List<ProdAppChecklist> prodAppChecklists;
    private String summary;
    private FacesContext context;
    private JasperPrint jasperPrint;


    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.generateDeficiency(prodAppChecklists, summary, userSession.getLoggedInUserObj());
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=deficiency_letter.pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.FOLLOW_UP);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        timeLine.setComment(summary);
        processProdBn.setTimeLine(timeLine);
        processProdBn.addTimeline();
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

    public List<ProdAppChecklist> getProdAppChecklists() {
        if(prodAppChecklists==null){
            prodAppChecklists = processProdBn.getProdAppChecklists();
            for(ProdAppChecklist pacs:prodAppChecklists){
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
}
