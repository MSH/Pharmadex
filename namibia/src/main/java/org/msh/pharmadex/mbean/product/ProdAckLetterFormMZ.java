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
import org.msh.pharmadex.service.ProdApplicationsService;
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
		String send = prodApp.getUsername();
		if(send == null || send.trim().equals(""))
			send = bundle.getString("gestorDeCTRM_value");
		prodApp.setUsername(send);
	}

	public String getCreateLetterBtnName(String val){    
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		if(userSession.isStaff())
			return val;
		else
			return bundle.getString("btn_create_ackletter");
	}

	public void createLetter(ProdApplications prodApp){
		String no = prodApp.getProdSrcNo();
		ProdApplicationsService paser = prodApplicationsServiceMZ.getProdApplicationsService();
		if(no == null || no.trim().equals("")){    	
			prodApp.setProdSrcNo(paser.getSrcNumber(paser.generateAppNo(prodApp)));
		}
		initParametrs(prodApp);
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
	
	/**
	 * Is generation of this letter allowed
	 * @return
	 */
	public boolean isAllowed(ProdApplications prodApp){
		if(prodApp == null){
			return false;
		}
		return !getUserSession().isCompany() && prodApp.isScreeningNum();
	}
}
