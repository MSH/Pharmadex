package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.JsfUtils;


@ManagedBean
@ViewScoped
public class RevchklistsMBean implements Serializable {
	@ManagedProperty(value = "#{reviewService}")
	ReviewService reviewService; 
	FacesContext facesContext = FacesContext.getCurrentInstance(); 
	java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	private List<ReviewQuestion> alllist;
    private boolean edit = true;
    ReviewQuestion listItem;
    boolean showVariation;
    boolean showMajVariation;
	
	  public boolean isShowVariation() {
		return showVariation;
	}

	public void setShowVariation(boolean showVariation) {
		this.showVariation = showVariation;
	}

	public boolean isShowMajVariation() {
		return showMajVariation;
	}

	public void setShowMajVariation(boolean showMajVariation) {
		this.showMajVariation = showMajVariation;
	}

	public List<ReviewQuestion> getAlllist() {
		  if (alllist==null)    alllist=reviewService.findAllrevQ();
		  return alllist;
	}

	public void setAlllist(List<ReviewQuestion> alllist) {
		this.alllist = alllist;
	}

	public ReviewQuestion getListItem() {
		return listItem;
	}

	public ReviewService getReviewService() {
		return reviewService;
	}

	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	public void setListItem(ReviewQuestion listItem) {
		this.listItem = listItem;
	}

	@PostConstruct
	    private void init() {
		  alllist=reviewService.findAllrevQ();
	  }
	  
	 public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void initAdd(){
	 listItem= new ReviewQuestion(); 
	 showVariation=false;
	 showMajVariation=false;
	 }
	 public void saveLst(){
		 FacesContext facesContext = FacesContext.getCurrentInstance();
		  facesContext = FacesContext.getCurrentInstance();
	listItem.setVariation(showVariation);
	listItem.setMajVariation(showMajVariation);
		 if (reviewService.updateRevQList( listItem))
		
			 facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
			 listItem=null;
		 
	 }
	  
	 
	 public void cancel(){
      listItem= new ReviewQuestion();  
	 }
	 
	  public void initUpdate(ReviewQuestion item) {
		  listItem = item;
	        edit = true;
	    	if (item.isVariation()==null) {
	    		showVariation=false;
	    	}else 
	    		showVariation=item.isVariation();
	    	
	     	if (item.isMajVariation()==null) {
	    		showMajVariation=false;
	    	}else 
	    		showMajVariation=item.isMajVariation();
	    }
	   
	  
	  public ProdAppType getGeneric(){
			return ProdAppType.GENERIC;
		}
	public ProdAppType getNewEntity(){
		return ProdAppType.NEW_CHEMICAL_ENTITY;
		} 
	public ProdAppType getRenew(){
		 return ProdAppType.RENEW;
		}
	public String getVariationName(){
			String mes = bundle.getString("variationType")+" " +bundle.getString("variationType_minor");
			return mes;
	}
		public String getMajVariationName(){
			String mes = bundle.getString("variationType")+" " +bundle.getString("variationType_major");
			return mes;
	}
	public String	showVar(ReviewQuestion item){
			if (item.isVariation()==null) return "false";
			return item.isVariation().toString();
	}
	public String	showMajVar(ReviewQuestion item){
		if (item.isMajVariation()==null) return "false";
		return item.isMajVariation().toString();
}
	public List<ReviewQuestion> complete(String query) {
        return JsfUtils.completeSuggestions(query, alllist);
    }
}
