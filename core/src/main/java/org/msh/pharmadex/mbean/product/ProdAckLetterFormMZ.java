/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

/**
 * Author: dudchenko
 */
@ManagedBean
@RequestScoped
public class ProdAckLetterFormMZ implements Serializable {
	
	@ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
	
	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    private ProdApplicationsServiceMZ prodApplicationsServiceMZ;
	
	private FacesContext context;
	private java.util.ResourceBundle bundle;

    public void initParametrs(ProdApplications prodApp) {
    	context = FacesContext.getCurrentInstance();
    	bundle = context.getApplication().getResourceBundle(context, "msgs");
    	
    	String no = prodApp.getProdAppNo();
    	if(no == null || no.trim().equals(""))
    		no = prodApplicationsServiceMZ.getProdApplicationsService().generateAppNo(prodApp);
    	prodApp.setProdAppNo(no);
    	
    	String send = prodApp.getUsername();
    	if(send == null || send.trim().equals(""))
    		send = bundle.getString("gestorDeCTRM_value");
    	prodApp.setUsername(send);
	}
    
    public void createLetter(ProdApplications prodApp){
    	prodApplicationsServiceMZ.createAckLetter(prodApp, userSession.getLoggedINUserID(),userSession.isCompany(),userSession.isStaff());
    }
    
    public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
        return prodApplicationsServiceMZ;
    }

    public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
        this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
    }
    
    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
