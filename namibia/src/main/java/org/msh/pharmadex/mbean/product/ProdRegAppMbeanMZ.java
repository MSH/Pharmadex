package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.primefaces.event.FlowEvent;

/**
 * Author: usrivastava
 * Update: Odissey
 */
@ManagedBean
@ViewScoped
public class ProdRegAppMbeanMZ implements Serializable {

	FacesContext context = FacesContext.getCurrentInstance();
	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

	@ManagedProperty(value = "#{prodRegAppMbean}")
	private ProdRegAppMbean prodRegAppMbean;

	private String currentStep = "";
	private boolean visibleSave = false;
	private boolean visibleSubmit = false;
	private boolean visibleForeignTab = false;
	private boolean visibleTabsByEdit = true;
	
	private static Map<String, Integer> mapNumTabs = new HashMap<String, Integer>(){
		{
			put("prodreg", 1);
			put("proddetails", 2);
			put("appdetails", 3);
			put("applicationStatus", 4);
			put("manufdetail", 5);
			put("pricing", 6);
			put("payment", 7);
			put("attach", 8);
			put("prodAppChecklist", 9);
			put("summary", 10);
		}
	};

	//fires everytime you click on next or prev button on the wizard
	public String onFlowProcess(FlowEvent event) {
		context = FacesContext.getCurrentInstance();
		String currentWizardStep = event.getOldStep();
		String nextWizardStep = event.getNewStep();
		try {
	
			if(!isClickPrevious(currentWizardStep, nextWizardStep)){
				prodRegAppMbean.initializeNewApp(nextWizardStep);
				if (currentWizardStep.equals("prodreg")) {
					if (prodRegAppMbean.getApplicant() == null || prodRegAppMbean.getApplicant().getApplcntId()==null) {
						FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant not selected", "Select an Applicant.");
						context.addMessage(null, msg1);
						nextWizardStep = currentWizardStep; // keep wizard on current step if error
					}
					if (prodRegAppMbean.getApplicantUser() == null) {
						if(!prodRegAppMbean.getProdApplications().getRegState().equals(RegState.REGISTERED)){
							FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant user not selected", "Select person responsible for the application");
							context.addMessage(null, msg2);
							nextWizardStep = currentWizardStep; // keep wizard on current step if error
						}
					}
					if(prodRegAppMbean.getProdApplications().getProdAppType() != null){
						if (prodRegAppMbean.getProdApplications().getProdAppType().equals(ProdAppType.VARIATION)||prodRegAppMbean.getProdApplications().getProdAppType().equals(ProdAppType.RENEW)){
							if (prodRegAppMbean.getProduct().getProdName()==null){
								FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product not selected", "Select a product.");
								context.addMessage(null, msg1);
								nextWizardStep = currentWizardStep; 
							}
						}
					}
				}
				if (!currentWizardStep.equals("prodreg")){
					if(currentWizardStep.equals("proddetails")){
						DosageForm df = getProdRegAppMbean().findDosageFormByItem();
						if(df == null){
							FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dosage Form not selected", "Not found selected Dosage For");
							context.addMessage(null, msg2);
							nextWizardStep = currentWizardStep; // keep wizard on current step if error
							return nextWizardStep;
						}
					}
					prodRegAppMbean.saveApp();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
			context.addMessage(null, msg);
			nextWizardStep = currentWizardStep; // keep wizard on current step if error
		}
		currentStep = nextWizardStep;
		return nextWizardStep; // return new step if all ok
	}

	private boolean isClickPrevious(String curWizStep, String nextWizStep){
		int cur = mapNumTabs.get(curWizStep);
		int next = mapNumTabs.get(nextWizStep);
		if(cur > next)
			return true;
		return false;
	}
	
	public ProdRegAppMbean getProdRegAppMbean() {
		return prodRegAppMbean;
	}

	public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
		this.prodRegAppMbean = prodRegAppMbean;
	}

	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public boolean isVisibleSave() {
		visibleSave = !currentStep.equals("") && !currentStep.equals("prodreg");
		return visibleSave;
	}

	public void setVisibleSave(boolean visibleSave) {
		this.visibleSave = visibleSave;
	}

	public boolean isVisibleSubmit() {
		visibleSubmit = currentStep.equals("summary");
		return visibleSubmit;
	}

	public void setVisibleSubmit(boolean visibleSubmit) {
		this.visibleSubmit = visibleSubmit;
	}

	public boolean isVisibleForeignTab() {
		if(prodRegAppMbean.getApplicant() != null){
			if(prodRegAppMbean.getApplicant().getApplicantType() != null){
				if(prodRegAppMbean.getApplicant().getApplicantType().getName().equalsIgnoreCase("Importer"))
					visibleForeignTab = true;
			}
		}
		return visibleForeignTab;
	}

	public void setVisibleForeignTab(boolean visibleForeignTab) {
		this.visibleForeignTab = visibleForeignTab;
	}

	public boolean isVisibleTabsByEdit() {
		if(prodRegAppMbean.getUserSession().isAdmin() && prodRegAppMbean.getProdApplications() != null
				&& prodRegAppMbean.getProdApplications().getRegState().equals(RegState.REGISTERED))
			visibleTabsByEdit = false;
		return visibleTabsByEdit;
	}

	public void setVisibleTabsByEdit(boolean vis) {
		this.visibleTabsByEdit = vis;
	}
}
