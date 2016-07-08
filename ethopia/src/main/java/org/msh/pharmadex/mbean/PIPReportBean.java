package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.PurProd;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.util.JsfUtils;

/**
 * Created by Odissey on 15.03.2016.
 * PIP/PO report
 */
@ManagedBean
@ViewScoped
public class PIPReportBean implements Serializable{
	private static final long serialVersionUID = -7454217017553117902L;
	
	private Date dateStart;
    private Date dateEnd;
    private Applicant selectedApplicant;
    private Country selectedCountry = new Country();
    private Company selectedCompany = new Company();
    private String port;
    
    private List<PIPReportItemBean> pipProds;
    private List<PurProd> purProds;
    private LicenseHolder lic;
    
    private Double totalPrice = new Double(0);
    private Integer totalNumbers = 0;

    public LicenseHolder getLic() {
        return lic;
    }

    public void setLic(LicenseHolder lic) {
        this.lic = lic;
    }

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public LicenseHolderService getLicenseHolderService() {

        return licenseHolderService;
    }

    @ManagedProperty(value = "#{licenseHolderService}")
    LicenseHolderService licenseHolderService;
     @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;

    //for pipreport
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
    //for POreport
    public String startReportPO(){
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
        getPurProds();
        return "";
    }

    public List<Applicant> completeApplicant(String query){
        return JsfUtils.completeSuggestions(query, globalEntityLists.getApplicants());
    }
    
    public List<Company> completeCompany(String query){
        return JsfUtils.completeSuggestions(query, globalEntityLists.getManufacturers());
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

    public Applicant getSelectedApplicant(){
        return selectedApplicant;
    }
    public void setSelectedApplicant(Applicant applicant) {
        this.selectedApplicant = applicant;
    }
    
    public Country getSelectedCountry(){
        return selectedCountry;
    }
    public void setSelectedCountry(Country cont) {
        this.selectedCountry = cont;
    }
    
    public Company getSelectedCompany(){
        return selectedCompany;
    }
    public void setSelectedCompany(Company comp) {
        this.selectedCompany = comp;
    }
    
    public String getPort(){
        return port;
    }
    public void setPort(String p) {
        this.port = p;
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

    public List<PIPReportItemBean> getPipProds(){
        if ((dateStart != null && dateEnd != null)) {
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("startDate", dateStart);
        	map.put("endDate", dateEnd);
        	if(selectedApplicant != null)
        		map.put("applicant", selectedApplicant.getApplcntId());
        	if(selectedCompany != null)
        		map.put("company", selectedCompany.getCompanyName());
        	if(selectedCountry != null)
        		map.put("country", selectedCountry.getId());
        	map.put("port", port);
        	
            pipProds = pOrderService.findAllPIPProds(map);
            
            calculateTotals();
        }
        return pipProds;
    }
    
    private void calculateTotals(){
    	totalPrice = new Double(0);
    	totalNumbers = 0;
    	Double sum = new Double(0);
    	if(pipProds != null){
    		for(PIPReportItemBean it:pipProds){
    			totalPrice += it.getTotalPrice();
    			sum += it.getCount();
    		}
    	}
    	
    	if(sum > 0)
    		totalNumbers = sum.intValue();
    }
    
    public void setPipProds(List<PIPReportItemBean> pipProds) {
        this.pipProds = pipProds;
    }
    public List<PurProd> getPurProds(){
        if ((dateStart!=null && dateEnd!=null)) {
            purProds = pOrderService.findSelectedPurProds(dateStart, dateEnd, selectedApplicant);
        }
        return purProds;
    }
    public void setPurProds(List<PurProd> purProds) {
        this.purProds = purProds;
    }

   /* public void onSummaryRow(Object filter)
    {
        System.out.print("xxxx");
    }*/

    public int sortIt(Object it1, Object it2){
        DosageForm form1 = (DosageForm) it1;
        String fn1 = form1.getDosForm();
        DosageForm form2 = (DosageForm) it1;
        String fn2 = form2.getDosForm();
        return fn2.compareTo(fn2);
    }

    public String findLicHolderByApplicant(Long id){
        setLic(null);
        List<LicenseHolder> res = licenseHolderService.findLicHolderByApplicant(id);
        String s="";
        if (res!=null)
            if (res.size()!=0)
           if (res.get(0)!=null ) {
               s=res.get(0).getName();
           setLic(res.get(0));
           }

        return s;
    }

    public String findFirstAgent(){
       if (lic==null) return "";
        return pOrderService.findFirstAgent(lic.getId());
    }
    
    public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double total) {
		this.totalPrice = total;
	}

	public Integer getTotalNumbers() {
		return totalNumbers;
	}

	public void setTotalNumbers(Integer total) {
		this.totalNumbers = total;
	}
}
