package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.msh.pharmadex.domain.enums.CTDModule;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.EditReviewService;
/**
 * Back end class for edit review questions feature
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@ViewScoped
public class EditReview implements Serializable{
	private static final long serialVersionUID = 2632730080133420949L;

	@ManagedProperty(value = "#{editReviewService}")
	EditReviewService editReviewService;
	/**
	 * This is a full list of questions regardless of type
	 */
	List<ReviewQuestion> fullList = new ArrayList<ReviewQuestion>();
	/**
	 * Edit will be here
	 */
	List<ReviewQuestion> filteredList = new ArrayList<ReviewQuestion>();

	ReviewQuestion selectedItem=null;
	/**
	 * CTD module has a type String for ReviewQuestion, so take it apart and syncronize! 
	 */
	CTDModule selectedCTD = null;

	/**
	 * initial filter value
	 */
	private ProdAppType listType = ProdAppType.GENERIC;

	private FacesContext context = FacesContext.getCurrentInstance();



	public EditReviewService getEditReviewService() {
		return editReviewService;
	}



	public void setEditReviewService(EditReviewService editReviewService) {
		this.editReviewService = editReviewService;
	}



	public List<ReviewQuestion> getFullList() {
		return fullList;
	}



	public void setFullList(List<ReviewQuestion> fullList) {
		this.fullList = fullList;
	}



	public List<ReviewQuestion> getFilteredList() {
		return filteredList;
	}



	public void setFilteredList(List<ReviewQuestion> filteredList) {
		this.filteredList = filteredList;
	}



	public ProdAppType getListType() {
		return listType;
	}



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



	public FacesContext getContext() {
		return context;
	}



	public void setContext(FacesContext context) {
		this.context = context;
	}



	public ReviewQuestion getSelectedItem() {
		return selectedItem;
	}


	public void setSelectedItem(ReviewQuestion selectedItem) {
		this.selectedItem = selectedItem;
		setSelectedCTD(CTDModule.valueOf(getSelectedItem().getCtdModule()));
	}


	public CTDModule getSelectedCTD() {
		return selectedCTD;
	}

	public void setSelectedCTD(CTDModule selectedCTD) {
		if (getSelectedItem() != null){
			getSelectedItem().setCtdModule(selectedCTD.toString());
		}
		this.selectedCTD = selectedCTD;
	}



	@PostConstruct
	public void init(){
		recalcFiltered();
	}
	/**
	 * Fetch question headers for first column
	 * @param question
	 * @return
	 */
	public String formatQuestionHeaders(ReviewQuestion question){
		return "<b>" + question.getCtdModule() +"</b>"
				+"<p style='margin-left: 20px'>"+question.getHeader1()+ "</p>"
				+"<p style='margin-left: 40px'>" +question.getHeader2() + "</p>";
	}

	/**
	 * recalculate filtered list based on listType value
	 */
	private void recalcFiltered() {
		if(getListType() != null){
			getFullList().clear();
			setFullList(getEditReviewService().findAll());
			filterIt();
		}
	}

	/**
	 * Build only filtered list
	 */
	public void filterIt() {
		getFilteredList().clear();
		for(ReviewQuestion item : getFullList()){
			if(item.isGenMed() && getListType().equals(ProdAppType.GENERIC)){
				getFilteredList().add(item);
			}
			if(item.isNewMed() && getListType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)){
				getFilteredList().add(item);
			}
		}
	}

	/**
	 * Remove question from the set
	 * @param question
	 */
	public void removeFromSet(ReviewQuestion question){
		if(getListType() == ProdAppType.GENERIC){
			question.setGenMed(false);
		}
		if(getListType() == ProdAppType.NEW_CHEMICAL_ENTITY){
			question.setNewMed(false);
		}
		save();
		filterIt();
	}

	/**
	 * Change selected item for operations on it
	 * @param question
	 */
	public void changeSelectedItem(ReviewQuestion question){
		setSelectedItem(question);
	}

	/**
	 * Shift this item down
	 * @param question
	 */
	public void shiftItemDown(ReviewQuestion question){
		int i = 0;
		boolean success = false; //pessimist
		for(ReviewQuestion item : getFullList()){
			if(item.getId().equals(question.getId())){
				if(i<getFullList().size()-1){
					ReviewQuestion nextItem = getFullList().get(i+1);
					if(allowShift(nextItem, question)){
						getFullList().set(i, nextItem);
						getFullList().set(i+1, item);
						success=true;
						break;
					}else{
						FacesContext.getCurrentInstance().addMessage(null, 
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible, Module and headers must be equial", "")); //TODO messages not a good idea
						return;
					}
				}
			}
			i++;
		}
		if (success){
			save();
			recalcFiltered();
		}else{
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible. It is the last question", "")); //TODO messages not a good idea
		}
	}
	/**
	 * Check can we allow shift the question to the position of item
	 * Strict rule currently commented out!
	 * @param item
	 * @param question
	 * @return
	 */
	private boolean allowShift(ReviewQuestion item, ReviewQuestion question) {
		/*boolean ret = item.getCtdModule().equals(question.getCtdModule())
				&& item.getHeader1().trim().equals(question.getHeader1().trim())
				&& item.getHeader2().trim().equals(question.getHeader2());
		return ret;*/
		return true;
	}



	/**
	 * Shift this item up
	 * @param question
	 */
	public void shiftItemUp(ReviewQuestion question){
		int i = 0;
		boolean success = false; //pessimist
		for(ReviewQuestion item : getFullList()){
			if(item.getId().equals(question.getId())){
				if(i>0){
					ReviewQuestion prevItem = getFullList().get(i-1);
					if(allowShift(prevItem, question)){
						getFullList().set(i, prevItem);
						getFullList().set(i-1, item);
						success=true;
						break;
					}else{
						FacesContext.getCurrentInstance().addMessage(null, 
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible, Module and headers must be equial", "")); //TODO messages not a good idea
						return;
					}
				}
			}
			i++;
		}
		if (success){
			save();
			recalcFiltered();
		}else{
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible. It is the first question", "")); //TODO messages not a good idea
		}
	}
	/**
	 * Create new item and make it to selected
	 * @param question
	 */
	public void createNewItemAsSelected(ReviewQuestion question){
		ReviewQuestion newItem = new ReviewQuestion();
		newItem.setCtdModule(question.getCtdModule());
		newItem.setHeader1(question.getHeader1());
		newItem.setHeader2(question.getHeader2());
		newItem.setOrd(question.getOrd());
		if(getListType() == ProdAppType.GENERIC){
			newItem.setGenMed(true);
		}
		if(getListType() == ProdAppType.NEW_CHEMICAL_ENTITY){
			newItem.setNewMed(true);
		}
		newItem.setQuestion("");
		setSelectedItem(newItem);
	}
	/**
	 * save all list with selected item placed to the right place
	 */
	public void saveWithSelected(){
		//process possible insert after!
		if(getSelectedItem().getId() == null){
			if(getSelectedItem().getOrd() != null){
				int i=0;
				for(ReviewQuestion item : getFullList()){
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
		for(ReviewQuestion item : getFullList()){
			item.setOrd(i);
			getEditReviewService().updateList(item);
			i++;
		}
		recalcFiltered();
	}
}
