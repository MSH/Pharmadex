/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.FeeSchedule;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.mbean.product.ProdApp;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.springframework.web.util.WebUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

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

    private String[] selSRA;
    private boolean eml = false;
    private boolean displayfeepanel;
    private String totalfee;
    private ProdAppType prodAppType;
    private FacesContext context;

    public void calculate(AjaxBehaviorEvent event) {
        context = FacesContext.getCurrentInstance();
        if (prodAppType.equals(null)) {
            context.addMessage(null, new FacesMessage("prodapptype_null"));
            displayfeepanel = false;
        } else {
            for (FeeSchedule feeSchedule : globalEntityLists.getFeeSchedules()) {
                if (feeSchedule.getAppType().equals(prodAppType.name())) {
                    totalfee = feeSchedule.getFee();
                    break;
                }
            }
            displayfeepanel = true;
        }

    }

    public String regApp() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        ProdApp prodApp = new ProdApp();
        prodApp.setEml(eml);
        prodApp.setProdAppType(prodAppType);
        prodApp.setSelSRA(selSRA);
        prodApp.setTotalfee(totalfee);
        prodApp.setSRA(selSRA.length > 0);

        userSession.setProdApp(prodApp);
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
}
