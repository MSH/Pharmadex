/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import java.io.Serializable;

import javax.annotation.PostConstruct;
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
	
   // private String sender = "";
   // private String prodappno = "";
    
    private ProdApplications productApp;

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
    
    public void buildParametrs(ProdApplications prodApp) {
    	/*if(getProdappno() == null  || getProdappno().trim().equals(""))
        	setProdappno(prodApplicationsServiceMZ.getProdApplicationsService().generateAppNo(prodApp));
    	prodApp.setProdAppNo(getProdappno());
    	prodApp.setUsername(getSender());*/
    }
    
    public void createLetter(ProdApplications prodApp){
    	buildParametrs(prodApp);
    	prodApplicationsServiceMZ.createAckLetter(prodApp);
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

    
	/*public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getProdappno() {
		return prodappno;
	}

	public void setProdappno(String prodappno) {
		this.prodappno = prodappno;
	}*/
}
