/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ProdApplicationsService;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentFormNA extends ProdConsentFormMZ implements Serializable {

    java.util.ResourceBundle bundle;
    private FacesContext context;

    @Override
    public String submitApp() {
    	ProdApplications curA=getProdApplications();
/* Excluded 2017-05-11 AK   	
 * curA.setProdAppNo(getProdApplicationsService().generateAppNo(getProdApplications()));
    	curA.setProdSrcNo(getProdApplicationsService().getSrcNumber(curA.getProdAppNo()));*/
    	    	
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
  		  if (oldPr.getProdDesc()==null){
  			comment=comment+"prodDesc";
  		  } else {
  			  if (!product.getProdDesc().equalsIgnoreCase(oldPr.getProdDesc())) comment=comment+"prodDesc";
  		  }
  		// check inns
  		  List<ProdInn> inns = product.getInns();
  		  List<ProdInn> oldInns = oldPr.getInns();
  		  if (!inns.isEmpty() & !oldInns.isEmpty()){
  			  if (inns.size()!=oldInns.size()) {
  				  comment=comment+"inns"; 
  			  } else{
  				  for (int i=0; i<inns.size();i++) {
  					  if(findDifferenceIn(inns.get(i),oldInns.get(i))){
  						  comment=comment+"inns"; 
  						  break;

  					  }
  				  }
  			  }
  		  } else
  		  { comment=comment+"inns"; 
  		  }
  		  //check excipients
  		  List<ProdExcipient> ex = product.getExcipients();
  		  List<ProdExcipient> oldex = oldPr.getExcipients();
  		  if (ex.isEmpty() & oldex.isEmpty()){  

  		  } else if   (!ex.isEmpty() & !oldex.isEmpty()){
  			  if (ex.size()!=oldex.size()) {
  				  comment=comment+"excipients"; 
  			  } else{
  				  for (int i=0; i<inns.size();i++) {
  					  if(findDifferenceEx(ex.get(i),oldex.get(i))){
  						  comment=comment+"excipients"; 
  						  break;

  					  }
  				  }
  			  }
  			  
  		  } else {
  			comment=comment+"excipients";  
  		  }
  		//check Atc
  		List<Atc> a = product.getAtcs();
  		List<Atc> olda = oldPr.getAtcs();
  		if 	(a.isEmpty() & olda.isEmpty()){

  		} else if (!a.isEmpty() & !olda.isEmpty()){
  			if (a.size()!=olda.size()) {
  				comment=comment+"Atc"; 
  			} else{
  				for (int i=0; i<inns.size();i++) {
  					if(findDifferenceAtc(a.get(i),olda.get(i))){
  						comment=comment+"Atc"; 
  						break;

  					}
  				}
  			}
  		} else {
  			comment=comment+"Atc"; 
  		} 
  		if (oldPr.getShelfLife()==null){
  			 comment=comment+"ShelfLife"; 
  		} else {
  		if(!product.getShelfLife().equalsIgnoreCase(oldPr.getShelfLife())) comment=comment+"ShelfLife"; 
  		}
  		if (oldPr.getContType()==null){
  			comment=comment+"ContType";
  		}else{
  			if(!product.getContType().equalsIgnoreCase(oldPr.getContType())) comment=comment+"ContType";
  		}
  		if (oldPr.getPackSize()==null){
  			comment=comment+"PackSize";
  		} else{
  			if(!product.getPackSize().equalsIgnoreCase(oldPr.getPackSize())) comment=comment+"PackSize";
  		}
  		if (oldPr.getIndications()==null){
  			comment=comment+"Indications";
  		} else{
  			if(!product.getIndications().equalsIgnoreCase(oldPr.getIndications())) comment=comment+"Indications";
  		}
  		if (oldPr.getStorageCndtn()==null){
  			comment=comment+"Storage";
  		} else{
  			if(!product.getStorageCndtn().equalsIgnoreCase(oldPr.getStorageCndtn())) comment=comment+"Storage";
  		}
  		if (oldPr.getPosology()==null){
  			comment=comment+"Posology";
  		} else{
  			if(!product.getStorageCndtn().equalsIgnoreCase(oldPr.getStorageCndtn())) comment=comment+"Posology";
  		}
  	//check manufacturer
  		List<ProdCompany> colist = product.getProdCompanies();
  		List<ProdCompany> oldCoList = oldPr.getProdCompanies();
  		if 	(colist.isEmpty() & oldCoList.isEmpty()){

  		} else if (!colist.isEmpty() & !oldCoList.isEmpty()){
  			if (a.size()!=oldCoList.size()) {
  				comment=comment+"ProdCompanies"; 
  			} else{
  				for (int i=0; i<inns.size();i++) {
  					if(findDifferenceCo(colist.get(i),oldCoList.get(i))){
  						comment=comment+"ProdCompanies"; 
  						break;

  					}
  				}
  			}
  		} else {
  			comment=comment+"ProdCompanies"; 
  		} 
  		curA.setAppComment(comment);
  	  }
        return super.submitApp();
    }
    
    private boolean findDifferenceIn(ProdInn in,ProdInn oldin ){
    	  if(!in.getDosStrength().equalsIgnoreCase(oldin.getDosStrength())) return true;
     	  if(in.getDosUnit()!=oldin.getDosUnit()) return true;	
     	  if(in.getInn()!=oldin.getInn()) return true;	
    	  return false;
    }
    private boolean findDifferenceEx(ProdExcipient in,ProdExcipient oldin ){
  	  if(!in.getDosStrength().equalsIgnoreCase(oldin.getDosStrength())) return true;
   	  if(in.getDosUnit()!=oldin.getDosUnit()) return true;	
   	  if(in.getExcipient()!=oldin.getExcipient()) return true;	
  	  return false;
  }
    private boolean findDifferenceAtc(Atc a,Atc olda){
    	if (a.getAtcCode()!=olda.getAtcCode()) return true;
    	return false;
    }
    private boolean findDifferenceCo(ProdCompany co, ProdCompany oldCo){
    	if (co.getCompany()!=oldCo.getCompany()) return true;
    	return false;
    }
}
