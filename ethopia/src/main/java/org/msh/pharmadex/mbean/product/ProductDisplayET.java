package org.msh.pharmadex.mbean.product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.service.ProdApplicationsServiceET;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class ProductDisplayET implements Serializable{
	
	@ManagedProperty(value = "#{productDisplay}")
    private ProductDisplay productDisplay;	
	
	@ManagedProperty(value = "#{prodApplicationsServiceET}")
    private ProdApplicationsServiceET prodApplicationsServiceET;
	
	 public StreamedContent generateCertificate(){
		 
		 String result = "";
	        if (getProductDisplay().getProdApplications().getRegCert()==null)
	            result = prodApplicationsServiceET.createRegCert(getProductDisplay().getProdApplications());
	        else
	            result = "created";
	        if (!"created".equals(result)){
	            FacesContext.getCurrentInstance().addMessage(
	                    null,
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Certificate didn't create"));
	            return null;
	        }else{
	            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Success","Just open certificate by 'Certificate' button"));
	            InputStream ist = new ByteArrayInputStream(getProductDisplay().getProdApplications().getRegCert());
	            Calendar c = Calendar.getInstance();
	            StreamedContent download = new DefaultStreamedContent(ist, "pdf", "registration_" + getProductDisplay().getProdApplications().getId() + "_" + c.get(Calendar.YEAR)+".pdf");
	            return download;
	        }		 
	 }


	public ProductDisplay getProductDisplay() {
		return productDisplay;
	}

	public void setProductDisplay(ProductDisplay productDisplay) {
		this.productDisplay = productDisplay;
	}


	public ProdApplicationsServiceET getProdApplicationsServiceET() {
		return prodApplicationsServiceET;
	}


	public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
		this.prodApplicationsServiceET = prodApplicationsServiceET;
	}

	 
}
