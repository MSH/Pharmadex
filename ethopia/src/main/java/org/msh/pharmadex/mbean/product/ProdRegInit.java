/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.FeeSchedule;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.LicenseHolderService;
import org.springframework.web.util.WebUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRegInit implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;

    private String[] selSRA;
    private boolean eml = false;
    private boolean displayfeepanel;
    private String fee;
    private String prescreenfee;
    private String totalfee;
    private ProdAppType prodAppType;
    private FacesContext context;
    private boolean eligible;
    private List<Checklist> checklists;

    public void calculate() {
        context = FacesContext.getCurrentInstance();
        if (prodAppType==null) {
            context.addMessage(null, new FacesMessage("prodapptype_null"));
            displayfeepanel = false;
        } else {
            for (FeeSchedule feeSchedule : globalEntityLists.getFeeSchedules()) {
                if (feeSchedule.getAppType().equals(prodAppType.name())) {
                    totalfee = feeSchedule.getTotalFee();
                    fee = feeSchedule.getFee();
                    prescreenfee = feeSchedule.getPreScreenFee();
                    break;
                }
            }
            populateChecklist();
            displayfeepanel = true;
        }

    }

    public void populateChecklist() {
        boolean sra = false;
        if(selSRA!=null)
            sra = selSRA.length > 0;
        checklists = checklistService.getETChecklists(prodAppType, sra);
    }

    public String regApp() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        calculate();

        ProdAppInit prodAppInit = new ProdAppInit();
        prodAppInit.setEml(eml);
        prodAppInit.setProdAppType(prodAppType);
        prodAppInit.setSelSRA(selSRA);
        prodAppInit.setFee(fee);
        prodAppInit.setPrescreenfee(prescreenfee);
        prodAppInit.setTotalfee(totalfee);
        prodAppInit.setSRA(selSRA.length > 0);

        userSession.setProdAppInit(prodAppInit);
        return "/secure/prodreghome";
    }


    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public String[] getSelSRA() {
        return selSRA;
    }

    public void setSelSRA(String[] selSRA) {
        this.selSRA = selSRA;
    }

    public boolean isEml() {
        return eml;
    }

    public void setEml(boolean eml) {
        this.eml = eml;
    }

    public boolean isDisplayfeepanel() {
        return displayfeepanel;
    }

    public void setDisplayfeepanel(boolean displayfeepanel) {
        this.displayfeepanel = displayfeepanel;
    }

    public String getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(String totalfee) {
        this.totalfee = totalfee;
    }

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public boolean isEligible() {
        if (userSession.isAdmin() || userSession.isHead() || userSession.isStaff())
            eligible = true;

        if (userSession.isCompany()) {
            if (userSession.getApplcantID() == null)
                eligible = false;
            else {
                LicenseHolder licenseHolder = licenseHolderService.findLicHolderByApplicant(userSession.getApplcantID());
                if (licenseHolder != null)
                    eligible = true;
                else
                    eligible = false;
            }
        }
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrescreenfee() {
        return prescreenfee;
    }

    public void setPrescreenfee(String prescreenfee) {
        this.prescreenfee = prescreenfee;
    }

    public List<Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<Checklist> checklists) {
        this.checklists = checklists;
    }

    public ChecklistService getChecklistService() {
        return checklistService;
    }

    public void setChecklistService(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }
}
