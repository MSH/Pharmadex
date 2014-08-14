package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.Product;
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

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @Transactional
    public void addCompany() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            Country c = selectedCompany.getAddress().getCountry();
            c = countryService.findCountryById(c.getId());
            selectedCompany.getAddress().setCountry(c);

            if(selectedCompany.getId()==null)
                selectedCompany = companyService.saveCompany(selectedCompany);
            else
                selectedCompany = companyService.findCompanyById(selectedCompany.getId());

            if(selectedCompany.getProducts()==null)
                selectedCompany.setProducts(new ArrayList<Product>());
            selectedCompany.getProducts().add(regHomeMbean.getProduct());

            List<Company> companyList = regHomeMbean.getProduct().getCompanyList();
            if(companyList==null) {
                companyList = new ArrayList<Company>();
                regHomeMbean.getProduct().setCompanyList(companyList);
            }
            companyList.add(selectedCompany);
            regHomeMbean.setShowCompany(false);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("company_add_success")));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), e.getMessage()));
        }
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



}
