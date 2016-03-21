package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.PIPProd;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.util.JsfUtils;
import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Odissey on 15.03.2016.
 * PIP/PO report
 */
@ManagedBean
@ViewScoped
public class PIPReportBean implements Serializable{
    private Date dateStart;
    private Date dateEnd;
    private Applicant selectedApplicant;
    private List<PIPProd> pipProds;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;

    public String startReport(){
        Boolean errorFound = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (dateStart==null){
            //TODO make i18n message
            FacesMessage mes = new FacesMessage("Start date required");
            mes.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null,mes);
            errorFound = true;
        }
        if (dateEnd==null){
            FacesMessage mes = new FacesMessage("End date required");
            mes.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null,mes);
            errorFound = true;
        }
        if (!errorFound){
            if (dateStart.before(dateEnd)){
                FacesMessage mes = new FacesMessage("End date should not before start date");
                mes.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage(null,mes);
                errorFound = true;
            }
        }
        if (!errorFound) return "error";
        getPipProds();
        return "";
    }

    public List<Applicant> completeApplicant(String query){
        return JsfUtils.completeSuggestions(query, globalEntityLists.getApplicants());
    }

    public Date getDateEnd() {
        return dateEnd;
    }
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getDateStart() {
        return dateStart;
    }
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Applicant getSelectedApplicant() { return selectedApplicant; }
    public void setSelectedApplicant(Applicant applicant) {
        this.selectedApplicant = applicant;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }
    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) { this.globalEntityLists = globalEntityLists; }

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
    }

    public List<PIPProd> getPipProds(){
        if ((dateStart!=null && dateEnd!=null)) {
            pipProds = pOrderService.findAllPIPProds(dateStart, dateEnd,selectedApplicant);
        }
        return pipProds;
    }
    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

    public void onSummaryRow(Object filter)
    {
        System.out.print("xxxx");
    }

    public int sortIt(Object it1, Object it2){
        DosageForm form1 = (DosageForm) it1;
        String fn1 = form1.getDosForm();
        DosageForm form2 = (DosageForm) it1;
        String fn2 = form2.getDosForm();
        return fn2.compareTo(fn2);
    }
}
