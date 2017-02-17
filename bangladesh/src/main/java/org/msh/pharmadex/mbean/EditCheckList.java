package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.enums.CTDModule;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ChecklistService;
/**
 * Back end class for edit check list feature
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@ViewScoped
public class EditCheckList implements Serializable {

	private static final long serialVersionUID = 6347034371287441414L;
	@ManagedProperty(value = "#{checklistService}")
	private ChecklistService checklistService;
	/**
	 * This is full check list for future save
	 */
	private List<Checklist> fullList = new ArrayList<Checklist>();
	/**
	 * This is filtered list for edit
	 */
	private List<Checklist> filteredList=new ArrayList<Checklist>();

	private ProdAppType listType = ProdAppType.GENERIC;

	private FacesContext context = FacesContext.getCurrentInstance();

	private Checklist selectedItem;
	
	CTDModule selectedCTD = CTDModule.ALL;

	@PostConstruct
	public void init(){
		recalcFiltered();
	}
	
	public ChecklistService getChecklistService() {
		return checklistService;
	}
	public void setChecklistService(ChecklistService checklistService) {
		this.checklistService = checklistService;
	}
	public List<Checklist> getFullList() {
		return fullList;
	}

	/**
	 * SEt full list, always not null
	 * @param fullList
	 */
	public void setFullList(List<Checklist> fullList) {
		if(fullList == null){
			this.fullList = new ArrayList<Checklist>();
		}else{
			this.fullList = fullList;
		}
	}
	public List<Checklist> getFilteredList() {
		return filteredList;
	}
	/**
	 * Set filtered list
	 * @param filteredList
	 */
	public void setFilteredList(List<Checklist> filteredList) {
		this.filteredList = filteredList;
	}
	/**
	 * Current list type generic, new_entity, etc..
	 */
	public ProdAppType getListType() {
		return listType;
	}
	/**
	 * Current list type generic, new_entity, etc..
	 * Recalculates filtered list on value change
	 */
	public void setListType(ProdAppType listType) {
		ProdAppType oldValue = getListType();
		this.listType = listType;
		if(listType==null){
			getFilteredList().clear();
		}
		if(oldValue==null && getListType() != null){
			recalcFiltered();
		}else{
			if(oldValue != getListType()){
				recalcFiltered();
			}
		}
	}


	/**
	 * Currently selected item for edit
	 * @return
	 */
	public Checklist getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(Checklist selectedItem) {
		this.selectedItem = selectedItem;
		setSelectedCTD(CTDModule.valueOfList(getSelectedItem().getModule()));
	}
	/**
	 * Recalc filtered list reload full list
	 */
	private void recalcFiltered() {
		if(getListType() != null){
			getFullList().clear();
			setFullList(getChecklistService().findAll());
			filterIt();
		}
	}

	/**
	 * Build only filtered list
	 */
	public void filterIt() {
		getFilteredList().clear();
		for(Checklist item : getFullList()){
			if(item.isGenMed() && getListType().equals(ProdAppType.GENERIC)){
				getFilteredList().add(item);
			}
			if(item.isNewMed() && getListType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)){
				getFilteredList().add(item);
			}
/*			if(item.isRecognizedMed() && getListType().equals(ProdAppType.RECOGNIZED)){
				getFilteredList().add(item);
			}*/
		}
	}
	public FacesContext getContext() {
		return context;
	}
	public void setContext(FacesContext context) {
		this.context = context;
	}
	/**
	 * Does filtered list exist?
	 * @return
	 */
	public boolean isFiltered(){
		return getFilteredList().size()>0;
	}
	/**
	 * Remove item from the current set
	 * @param item
	 */
	public void removeFromSet(Checklist item){
		if(getListType() == ProdAppType.GENERIC){
			item.setGenMed(false);
		}
		if(getListType() == ProdAppType.NEW_CHEMICAL_ENTITY){
			item.setNewMed(false);
		}
/*		if(getListType() == ProdAppType.RECOGNIZED){
			item.setRecognizedMed(false);
		}*/
		save();
		filterIt();
	}
	/**
	 * Save whole checklist, but put in mind possible insert
	 */
	public void saveWithSelected(){
		//process possible insert after!
		if(getSelectedItem().getId() == null){
			if(getSelectedItem().getOrd() != null){
				int i=0;
				for(Checklist item : getFullList()){
					if(item.getOrd().equals(getSelectedItem().getOrd())){
						getFullList().add(i+1, getSelectedItem());
						break;
					}
					i++;
				}
			}
		}
		save();
	}

	/**
	 * Save whole list of items
	 */
	public void save(){
		int i=0;
		for(Checklist item : getFullList()){
			item.setOrd(i);
			getChecklistService().updateList(item);
			i++;
		}
		recalcFiltered();
	}
	/**
	 * Change selection
	 * @param item
	 */
	public void changeSelectedItem(Checklist item){
		setSelectedItem(item);
	}
	/**
	 * Shit down item in the full list and save full list
	 * Really this function swaps itemToShift and a next item, if the next item exists and not header
	 * @param itemToShift
	 */
	public void shiftItemDown(Checklist itemToShift){
		int i = 0;
		for(Checklist item : getFullList()){
			if(item.getId().equals(itemToShift.getId())){
				if(i<getFullList().size()-1){
					Checklist nextItem = getFullList().get(i+1);
					if(nextItem.isHeader()){ //logic is wrong in source :(
						getFullList().set(i, nextItem);
						getFullList().set(i+1, item);
						break;
					}
				}
			}
			i++;
		}
		save();
		recalcFiltered();
	}
	/**
	 * Shit up item in the full list and save full list
	 * Really this function swaps itemToShift and a previous item, if the previous item exists and not header
	 * @param itemToShift
	 */
	public void shiftItemUp(Checklist itemToShift){
		int i = 0;
		for(Checklist item : getFullList()){
			if(item.getId().equals(itemToShift.getId())){
				if(i>0){
					Checklist prevItem = getFullList().get(i-1);
					if(prevItem.isHeader() == item.isHeader()){ //logic is wrong in source :(
						getFullList().set(i, prevItem);
						getFullList().set(i-1, item);
						break;
					}
				}
			}
			i++;
		}
		save();
		recalcFiltered();
	}
	/**
	 * Create new item and set it as selected to future edit
	 */
	public void createNewItemAsSelected(Checklist selectedItem){
		Checklist newItem = new Checklist();
		if(getListType() == ProdAppType.GENERIC){
			newItem.setGenMed(true);
		}
		if(getListType() == ProdAppType.NEW_CHEMICAL_ENTITY){
			newItem.setNewMed(true);
		}
/*		if(getListType() == ProdAppType.RECOGNIZED){
			newItem.setRecognizedMed(true);
		}*/
		if(selectedItem != null){
			newItem.setModule(selectedItem.getModule());
			newItem.setOrd(selectedItem.getOrd());
			newItem.setHeader(selectedItem.isHeader());
		}
		newItem.setName("");
		setSelectedItem(newItem);
	}
	
	public CTDModule getSelectedCTD() {
		return selectedCTD;
	}

	public void setSelectedCTD(CTDModule selectedCTD) {
		if (getSelectedItem() != null){
			getSelectedItem().setModule(selectedCTD.toString());
		}
		this.selectedCTD = selectedCTD;
	}

}
