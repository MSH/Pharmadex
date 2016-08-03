/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.ProdApplicationsServiceET;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentFormET extends ProdConsentForm implements Serializable {

    java.util.ResourceBundle bundle;
    private FacesContext context;
    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    private ProdApplicationsServiceET prodApplicationsServiceET;

    @Override
    public String submitApp() {
    	ProdApplications curA=getProdApplications();
    	curA.setProdAppNo(prodApplicationsServiceET.generateAppNo(getProdApplications()));
    	
  	  if (curA.getParentApplication()!=null){
  		  Product oldPr=null;
  		  Product product =curA.getProduct();
  		  String comment="";
  		  Long parentAppId = curA.getParentApplication().getId();
  		  ProdApplications pa=getProdApplicationsService().findProdApplications(parentAppId);
       	  if (pa!=null)  oldPr=pa.getProduct();
       	  if (!product.getProdName().equalsIgnoreCase(oldPr.getProdName())) comment=comment+"prodName";
       	  if (product.getProdCategory()!=oldPr.getProdCategory()) comment=comment+"prodCategory";
        	  if (!product.getGenName().equalsIgnoreCase(oldPr.getGenName())) comment=comment+"genName";
        	  if (product.getDosForm()!=oldPr.getDosForm()) comment=comment+"dosForm";
        	  if (product.getDosStrength()!=oldPr.getDosStrength()) comment=comment+"dosStrength"; 
        	  if (product.getDosUnit()!=oldPr.getDosUnit()) comment=comment+"dosUnit"; 
            if (product.getAdminRoute()!=oldPr.getAdminRoute()) comment=comment+"adminRoute"; 
            if (product.getAgeGroup()!=oldPr.getAgeGroup()) comment=comment+"ageGroup"; 
            if (product.getPharmClassif()!=oldPr.getPharmClassif()) comment=comment+"pharmClassif"; 
            if (!product.getProdDesc().equalsIgnoreCase(oldPr.getProdDesc())) comment=comment+"prodDesc";
          
            curA.setAppComment(comment);
  	  }
        return super.submitApp();
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }
}
