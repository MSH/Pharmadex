/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AgentType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.util.JsfUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
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
    private List<ProdAppType> prodAppTypes;
    private FacesContext context;
    private boolean eligible;
    private List<Checklist> checklists;

    private LicenseHolder selLicHolder;
    private List<LicenseHolder> licenseHolders;

    @PostConstruct
    public void init() {
        licenseHolders = licenseHolderService.findLicHolderByApplicant(userSession.getApplcantID());
        if (licenseHolders != null && licenseHolders.size() == 1) {
            selLicHolder = licenseHolders.get(0);
        }
        prodAppTypes = new ArrayList<ProdAppType>();
        prodAppTypes.add(ProdAppType.GENERIC);
        prodAppTypes.add(ProdAppType.GENERIC_NO_BE);
        prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
    }

    public List<LicenseHolder> completeLicHolderList(String query) {
        return JsfUtils.completeSuggestions(query, licenseHolders);
    }

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
        ProdApplications prodApplications = new ProdApplications();
        prodApplications.setProdAppType(prodAppType);
        if (selSRA.length > 0)
            prodApplications.setSra(true);
        else
            prodApplications.setSra(false);
        checklists = checklistService.getETChecklists(prodApplications, true);

    }

    public String regApp() {
        calculate();

        ProdAppInit prodAppInit = new ProdAppInit();
        prodAppInit.setEml(eml);
        prodAppInit.setProdAppType(prodAppType);
        prodAppInit.setSelSRA(selSRA);
        prodAppInit.setFee(fee);
        prodAppInit.setPrescreenfee(prescreenfee);
        prodAppInit.setTotalfee(totalfee);
        prodAppInit.setSRA(selSRA.length > 0);
        if (selLicHolder != null) {
            prodAppInit.setLicHolderID(selLicHolder.getId());
            selLicHolder = licenseHolderService.findLicHolder(selLicHolder.getId());
            if (selLicHolder.getAgentInfos() != null && selLicHolder.getAgentInfos().size() > 0) {
                for (AgentInfo agentInfo : selLicHolder.getAgentInfos()) {
                    if (agentInfo.getAgentType().equals(AgentType.FIRST)) {
                        if (agentInfo.getApplicant() != null && agentInfo.getApplicant().getUsers() != null && agentInfo.getApplicant().getUsers().size() > 0) {
                            userSession.setProdAppInit(prodAppInit);
                            return "/secure/prodreghome";
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "No User associated with the local agent representing " + selLicHolder.getName(),
                                    "No User associated with the local agent representing " + selLicHolder.getName()));
                            return "";
                        }
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "No Local Agent associated with " + selLicHolder.getName(),
                        "No Local Agent associated with " + selLicHolder.getName()));
                return "";
            }
        } else {
            userSession.setProdAppInit(prodAppInit);
            return "/secure/prodreghome";

        }
        return "";

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
                List<LicenseHolder> licenseHolders = licenseHolderService.findLicHolderByApplicant(userSession.getApplcantID());
                if (licenseHolders != null && licenseHolders.size() > 0)
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

    public LicenseHolder getSelLicHolder() {
        return selLicHolder;
    }

    public void setSelLicHolder(LicenseHolder selLicHolder) {
        this.selLicHolder = selLicHolder;
    }

    public List<ProdAppType> getProdAppTypes() {
        return prodAppTypes;
    }

    public void setProdAppTypes(List<ProdAppType> prodAppTypes) {
        this.prodAppTypes = prodAppTypes;
    }
}
