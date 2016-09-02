/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ExecSummaryBnMZ implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    private ProdApplicationsServiceMZ prodApplicationsServiceMZ;
    
    @ManagedProperty(value = "#{execSummaryBn}")
    private ExecSummaryBn execSummaryBn;

    public String submit(){
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

            if (getExecSummaryBn().getProdApplications().getRegState().equals(RegState.RECOMMENDED)) {
                for (ReviewInfo ri : getExecSummaryBn().getReviewInfos()) {
                	if(ri.getRecomendType() == null){
                		facesContext.addMessage(null, new FacesMessage("Invalid operation!", resourceBundle.getString("Error.moderatorNotRecomended")));
                        return "";
                	}
                    if (!ri.getRecomendType().equals(RecomendType.RECOMENDED)) {
                        facesContext.addMessage(null, new FacesMessage("Invalid operation!", "Cannot recommend a product which is not recommended by the assessors. Send feedback to assessor to change there recommendation."));
                        return "";
                    }
                }
            } else if (getExecSummaryBn().getProdApplications().getRegState().equals(RegState.NOT_RECOMMENDED)) {
                for (ReviewInfo ri : getExecSummaryBn().getReviewInfos()) {
                	if(ri.getRecomendType() == null){
                		facesContext.addMessage(null, new FacesMessage("Invalid operation!", resourceBundle.getString("Error.moderatorNotRecomended")));
                        return "";
                	}
                    if (!ri.getRecomendType().equals(RecomendType.NOT_RECOMENDED)) {
                        facesContext.addMessage(null, new FacesMessage("Invalid operation!", "Cannot not recommend a product which is recommended by the assessors. Send feedback to assessor to change there recommendation."));
                        return "";
                    }
                }
            }else if (getExecSummaryBn().getProdApplications().getRegState().equals(RegState.FOLLOW_UP)) {
            	prodApplicationsServiceMZ.changeStateReviewInfo(getExecSummaryBn().getProdApplications().getId());
            }
            
            String result = prodApplicationsServiceMZ.submitExecSummary(getExecSummaryBn().getProdApplications(), userSession.getLoggedINUserID(), getExecSummaryBn().getReviewInfos());
            if (result.equals("persist")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
                return "processreg";
            } else if (result.equals("state_error")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please accept the reviews before submitting the executive summary", ""));
                return null;
            } else if (result.equals("clinical_review")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clinical review not received or verified.", ""));
                return null;
            } else if (result.equals("lab_status")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Lab result not verified.", ""));
                return null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return "";
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
        return prodApplicationsServiceMZ;
    }

    public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
        this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
    }

	public ExecSummaryBn getExecSummaryBn() {
		return execSummaryBn;
	}

	public void setExecSummaryBn(ExecSummaryBn execSummaryBn) {
		this.execSummaryBn = execSummaryBn;
	}
}
