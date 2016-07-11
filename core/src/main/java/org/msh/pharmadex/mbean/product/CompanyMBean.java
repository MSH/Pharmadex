package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class CompanyMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CompanyMBean.class);
    private static final long serialVersionUID = 4226719621949851455L;

    @ManagedProperty(value = "#{prodRegAppMbean}")
    ProdRegAppMbean prodRegAppMbean;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{countryService}")
    CountryService countryService;

    @ManagedProperty(value = "#{companyService}")
    CompanyService companyService;

    private Company selectedCompany;
    private List<String> companyTypes;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private boolean showGMP = false;

    @Transactional
    public String addCompany() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            List<ProdCompany> prodCompanies = companyService.addCompany(prodRegAppMbean.getProduct(), selectedCompany, companyTypes);
            if (prodCompanies == null) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_value_req")));
            } else {
                prodRegAppMbean.setCompanies(prodCompanies);
            }
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("company_add_success")));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), e.getMessage()));
        }
        return null;
    }

    public void gmpChangeListener() {
        if (selectedCompany!=null)
            if (selectedCompany.isGmpInsp())
                showGMP = true;
            else
                showGMP = false;
    }

    public void companyChangeEventListener(SelectEvent event) {
//        logger.error("inside companyChangeEventListener");
//        logger.error("Selected company is " + selectedCompany.getCompanyName());
//        logger.error("event " + event.getObject());
        gmpChangeListener();


    }

    public void companyChangeEventListener(AjaxBehaviorEvent event) {
//        logger.error("inside companyChangeEventListener");
//        logger.error("Selected company is " + selectedCompany.getCompanyName());
//        logger.error("event " + event.getSource());
        gmpChangeListener();


    }


    public void initAddCompany() {
        selectedCompany = new Company();
//        regHomeMbean.setShowCompany(true);
    }


    public String cancelAdd() {

        selectedCompany = null;
//        regHomeMbean.setShowCompany(false);
        return null;
    }

    public Company getSelectedCompany() {
        if (selectedCompany == null)
            selectedCompany = new Company();
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<Company> completeCompany(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getManufacturers());
    }

    public List<String> getCompanyTypes() {
        return companyTypes;
    }

    public void setCompanyTypes(List<String> companyTypes) {
        this.companyTypes = companyTypes;
    }

    public boolean isShowGMP() {
        return showGMP;
    }

    public void setShowGMP(boolean showGMP) {
        this.showGMP = showGMP;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public ProdRegAppMbean getProdRegAppMbean() {
        return prodRegAppMbean;
    }

    public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
        this.prodRegAppMbean = prodRegAppMbean;
    }
}
