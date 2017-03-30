/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.FeeSchedule;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.UserService;
import org.primefaces.context.RequestContext;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
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

    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;
    
    @ManagedProperty(value = "#{userService}")
    private UserService userService;

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
    private boolean showVariationType;
    private List<ProdAppType> prodAppTypes;
    private int majorVars;
    private int minorVars;

    @PostConstruct
    public void init() {
        prodAppTypes = new ArrayList<ProdAppType>();
        prodAppTypes.add(ProdAppType.GENERIC);
        prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
        prodAppTypes.add(ProdAppType.RENEW);
        prodAppTypes.add(ProdAppType.VARIATION);
    }

    public void calculate() {
        context = FacesContext.getCurrentInstance();
        try {
            if (prodAppType == null) {
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
        }catch (Exception ex){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));

        }
    }

    private void populateChecklist() {
        ProdApplications prodApplications = new ProdApplications();
        prodApplications.setProdAppType(prodAppType);
        if (prodAppType.equals(ProdAppType.VARIATION)){
            List<Checklist> mjChecklists=null;
            List<Checklist> mnChecklists=null;
            if (minorVars==0&&majorVars==0) {
                checklists = null;
            }else if (minorVars>0&&this.majorVars==0){
                checklists = checklistService.getETChecklists(prodApplications, false);
            }
            else if (majorVars>0&&minorVars==0) {
                checklists = checklistService.getETChecklists(prodApplications, true);
            }else {
                checklists = checklistService.getETChecklists(prodApplications, false);
                mjChecklists = checklistService.getETChecklists(prodApplications, true);
                if (mjChecklists != null && checklists != null) {
                    for (Checklist ch : mjChecklists) {
                        checklists.add(ch);
                    }
                }
            }
        }else if (prodAppType.equals(ProdAppType.RENEW)){
            checklists = checklistService.getChecklists(prodApplications,true);
        }else{
        	prodApplications.setSra(false);
        	if (selSRA!=null)
        		if (selSRA.length > 0) prodApplications.setSra(true);   
        	checklists = checklistService.getChecklists(prodApplications,true);
        }

    }

    public String regApp() {
        ProdAppInit prodAppInit = new ProdAppInit();
        prodAppInit.setEml(eml);
        prodAppInit.setProdAppType(prodAppType);
        prodAppInit.setSelSRA(selSRA);
        prodAppInit.setFee(fee);
        prodAppInit.setPrescreenfee(prescreenfee);
        prodAppInit.setTotalfee(totalfee);
        if (selSRA != null)
            prodAppInit.setSRA(selSRA.length > 0);
        else
            prodAppInit.setSRA(false);
        prodAppInit.setMnVarQnt(minorVars);
        prodAppInit.setMjVarQnt(majorVars);
    
        userSession.setProdAppInit(prodAppInit);
        return "/secure/prodreghome";
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
        eligible = false;
        if (userSession.isAdmin() || userSession.isHead() || userSession.isStaff())
            eligible = true;

        if (userSession.isCompany()) {
            if (userSession.getApplcantID() == null)
                eligible = false;
            else
                eligible = true;
        }
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
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
    

    public void ajaxListener(AjaxBehaviorEvent event){
        isShowVariationType();
        RequestContext.getCurrentInstance().update("reghome");
    }

	public List<ProdAppType> getProdAppTypes() {
		return prodAppTypes;
	}

	public void setProdAppTypes(List<ProdAppType> prodAppTypes) {
		this.prodAppTypes = prodAppTypes;
	}

    public int getMajorVars() {
        return majorVars;
    }

    public void setMajorVars(int majorVars) {
        this.majorVars = majorVars;
    }

    public int getMinorVars() {
        return minorVars;
    }

    public void setMinorVars(int minorVars) {
        this.minorVars = minorVars;
    }
    public boolean isShowVariationType() {
        showVariationType = (prodAppType==ProdAppType.VARIATION);
        return showVariationType;
    }

    public void setShowVariationType(boolean showVariationType) {
        this.showVariationType = showVariationType;
    }
    
	public List<User> getScreeners() {
		return getUserService().findScreeners(true);
	}
    
    public void setScreeners(List<User> dummy){
    	//only to obey bean specifications
    }


}
