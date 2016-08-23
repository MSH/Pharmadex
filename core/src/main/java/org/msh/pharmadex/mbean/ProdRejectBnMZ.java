/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.UserService;

import net.sf.jasperreports.engine.JRException;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRejectBnMZ implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    private ProdApplicationsServiceMZ prodApplicationsServiceMZ;
    
    @ManagedProperty(value = "#{prodRejectBn}")
    private ProdRejectBn prodRejectBn;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public String rejectProdApp() throws JRException, IOException {
        facesContext = FacesContext.getCurrentInstance();
        if (!getProdRejectBn().getProdApplications().getRegState().equals(RegState.NOT_RECOMMENDED)) {
            facesContext.addMessage(null, new FacesMessage("Invalid operation!", bundle.getString("Error.headNotReject")));
            return "";
        }
        
        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(RegState.REJECTED);
        timeLine.setProdApplications(getProdRejectBn().getProdApplications());
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userService.findUser(userSession.getLoggedINUserID()));
        timeLine.setComment(getProdRejectBn().getSummary());
        processProdBn.getTimeLineList().add(timeLine);
        getProdRejectBn().getProdApplications().setRegState(timeLine.getRegState());

        String s = getProdApplicationsServiceMZ().createRejectCert(getProdRejectBn().getProdApplications(), getProdRejectBn().getSummary(),userSession.getLoggedINUserID());
		if(!s.equals("created")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
		}else{
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("status_change_success")));
		}
		timeLine = new TimeLine();
		return "/internal/processreg";
    }

    
    public ProdRejectBn getProdRejectBn() {
		return prodRejectBn;
	}

	public void setProdRejectBn(ProdRejectBn prodRejectBn) {
		this.prodRejectBn = prodRejectBn;
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

	public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
		return prodApplicationsServiceMZ;
	}

	public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
		this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
	}


	public ProcessProdBn getProcessProdBn() {
		return processProdBn;
	}


	public void setProcessProdBn(ProcessProdBn processProdBn) {
		this.processProdBn = processProdBn;
	}
    
}
