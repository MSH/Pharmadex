/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.FeeSchedule;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.primefaces.context.RequestContext;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
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
   
    private int minorQuantity=0;
    private int majorQuantity=0;
    private List<ProdAppType> prodAppTypes;
    
   

	
 public int getMinorQuantity() {
		return minorQuantity;
	}

	public void setMinorQuantity(int minorQuantity) {
		this.minorQuantity = minorQuantity;
	}

	public int getMajorQuantity() {
		return majorQuantity;
	}

	public void setMajorQuantity(int majorQuantity) {
		this.majorQuantity = majorQuantity;
	}
	public boolean isShowVariationType() {
		  showVariationType = (prodAppType==ProdAppType.VARIATION);
	        return showVariationType;
	}

	public void setShowVariationType(boolean showVariationType) {
		this.showVariationType = showVariationType;
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
        if (selSRA != null && selSRA.length > 0)
            prodApplications.setSra(true);
        else
            prodApplications.setSra(false);
        checklists = checklistService.getChecklists(prodApplications, true);
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
        prodAppInit.setMnVarQnt(minorQuantity);
        prodAppInit.setMjVarQnt(majorQuantity);
    
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
    
    @PostConstruct
    public void init() {
    	prodAppTypes = new ArrayList<ProdAppType>();
    		prodAppTypes.add(ProdAppType.GENERIC);
    		prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
    		prodAppTypes.add(ProdAppType.RENEW);
    		prodAppTypes.add(ProdAppType.VARIATION);
    }	
    public void ajaxListener(AjaxBehaviorEvent event){
        isShowVariationType();
         this.majorQuantity=0;
        this.minorQuantity=0;
      
           RequestContext.getCurrentInstance().update("reghome");
    }

	public List<ProdAppType> getProdAppTypes() {
		return prodAppTypes;
	}

	public void setProdAppTypes(List<ProdAppType> prodAppTypes) {
		this.prodAppTypes = prodAppTypes;
	}
}
