package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

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
    CountryService countryService;

    private Company selectedCompany;


    @PostConstruct
    private void init() {
        System.out.println("inside companymbean");
        logger.debug("------------------------------------------------------------------------------------");
        logger.debug(" Company Bean instantiating ");
        logger.debug("------------------------------------------------------------------------------------");


//        selectedCompany = new Company();
//        selectedCompany.getAddress().setCountry(new Country());
//        selectedCompany.setCompanyType(CompanyType.MANUFACTURER);
    }

    public void addCompany() {
        try {
            Country c = selectedCompany.getAddress().getCountry();
            c = countryService.findCountryById(c.getId());
            selectedCompany.getAddress().setCountry(c);

            selectedCompany.setProduct(regHomeMbean.getProduct());
            regHomeMbean.getProduct().getCompanies().add(selectedCompany);
            regHomeMbean.setShowCompany(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Company added"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getMessage()));
        }
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

}
