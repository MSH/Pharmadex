package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.FastTrackMed;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.util.RetObject;

@ManagedBean
@ViewScoped
public class ChecklistMBean implements Serializable {
	@ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;
	FacesContext facesContext = FacesContext.getCurrentInstance(); 
	java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	private List<Checklist> alllist;
    private boolean edit = true;
	Checklist listItem;
	
	  public Checklist getListItem() {
		return listItem;
	}

	public void setListItem(Checklist listItem) {
		this.listItem = listItem;
	}

	@PostConstruct
	    private void init() {
		  alllist=checklistService.findAll();
	  }
	  
	 public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void initAdd(){
	 listItem= new Checklist(); 
		 
	 }
	 public void saveLst(){
		 FacesContext facesContext = FacesContext.getCurrentInstance();
		 listItem =checklistService.updateList( listItem);
		 if (listItem!=null){
			 facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
			 listItem=null;
		 }
	 }
	  
	 
	 public void cancel(){
 listItem= new Checklist();  
	 }
	 
	  public void initUpdate(Checklist item) {
		  listItem = item;
	        edit = true;
	    }
	 
	public ChecklistService getChecklistService() {
		return checklistService;
	}
	public void setChecklistService(ChecklistService checklistService) {
		this.checklistService = checklistService;
	}
	public List<Checklist> getAlllist() {
		if (alllist==null)  alllist=checklistService.findAll();
		return alllist;
	}
	public void setAlllist(List<Checklist> alllist) {
		this.alllist = alllist;
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
	public String getVariation(){
		String mes = bundle.getString("variationType")+" " +bundle.getString("variationType_minor");
		return mes;
	}
	public String getMajVariation(){
		String mes = bundle.getString("variationType")+" " +bundle.getString("variationType_major");
		return mes;
	}
}
