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
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
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
	private boolean yesclick = false;
	private boolean visibleSave = false;
	private boolean visibleSubmit = false;
	
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
			if(isClickPrevious(currentWizardStep, nextWizardStep)){
				if(currentStep.equals("summary")){
					return nextWizardStep;
				}
				if(yesclick){
					yesclick = false;
					RequestContext.getCurrentInstance().execute("PF('backDlg').hide()");
					currentStep = nextWizardStep;
					return currentStep;
				}else{
					RequestContext.getCurrentInstance().execute("PF('backDlg').show()");
					currentStep = currentWizardStep;
					return currentStep;
				}
			}else{
				prodRegAppMbean.initializeNewApp(nextWizardStep);
				if (currentWizardStep.equals("prodreg")) {
					if (prodRegAppMbean.getApplicant() == null || prodRegAppMbean.getApplicant().getApplcntId()==null) {
						FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant not selected", "Select an Applicant.");
						context.addMessage(null, msg1);
						nextWizardStep = currentWizardStep; // keep wizard on current step if error
					}
					if (prodRegAppMbean.getApplicantUser() == null) {
						FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Applicant user not selected", "Select person responsible for the application");
						context.addMessage(null, msg2);
						nextWizardStep = currentWizardStep; // keep wizard on current step if error
					}

				}
				if (!currentWizardStep.equals("prodreg")){
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
	
	public void buttonAction(ActionEvent actionEvent) {
		yesclick = true;
		RequestContext.getCurrentInstance().execute("PF('wizard').back()");
    }
	
	public ProdRegAppMbean getProdRegAppMbean() {
		return prodRegAppMbean;
	}

	public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
		this.prodRegAppMbean = prodRegAppMbean;
	}

	public boolean isYesclick() {
		return yesclick;
	}

	public void setYesclick(boolean yesclick) {
		this.yesclick = yesclick;
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
}