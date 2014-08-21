package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class CompanyMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CompanyMBean.class);
    private static final long serialVersionUID = 4226719621949851455L;

    @Autowired
    RegHomeMbean regHomeMbean;

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    CountryService countryService;

    @Autowired
    CompanyService companyService;

    private Company selectedCompany;
    private List<String> companyTypes;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private boolean showGMP = false;

    @Transactional
    public void addCompany() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            List<ProdCompany> prodCompanies = companyService.addCompany(regHomeMbean.getProduct(), selectedCompany, companyTypes);
            if(prodCompanies==null){
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_value_req")));
            }else{
                regHomeMbean.setCompanies(prodCompanies);
            }
            regHomeMbean.setShowCompany(false);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("company_add_success")));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), e.getMessage()));
        }
    }

    public void gmpChangeListener() {
        if (selectedCompany.isGmpInsp())
            showGMP = true;
        else
            showGMP = false;
    }

    public void companyChangeEventListener(SelectEvent event) {
        logger.error("inside companyChangeEventListener");
        logger.error("Selected company is " + selectedCompany.getCompanyName());
        logger.error("event "+event.getObject());


    }

    public void companyChangeEventListener(AjaxBehaviorEvent event) {
        logger.error("inside companyChangeEventListener");
        logger.error("Selected company is " + selectedCompany.getCompanyName());
        logger.error("event "+event.getSource());


    }


    public void initAddCompany() {
        selectedCompany = new Company();
        regHomeMbean.setShowCompany(true);
    }


    public String cancelAdd() {

        selectedCompany = null;
        regHomeMbean.setShowCompany(false);
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
}
