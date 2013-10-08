package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class CompanyMBean {
    private static final Logger logger = LoggerFactory.getLogger(CompanyMBean.class);

    @Autowired
    RegHomeMbean regHomeMbean;

    private Company selectedCompany;


    @PostConstruct
    private void init() {
        logger.debug("------------------------------------------------------------------------------------");
        logger.debug(" Company Bean instantiating ");
        logger.debug("------------------------------------------------------------------------------------");

        selectedCompany = new Company();
        selectedCompany.getAddress().setCountry(new Country());
        selectedCompany.setCompanyType(CompanyType.MANUFACTURER);
    }

    public String addCompany() {
        selectedCompany.setProduct(regHomeMbean.getProduct());
        regHomeMbean.getCompanies().add(selectedCompany);
        return null;
    }

    public String removeCompany() {
        regHomeMbean.getCompanies().remove(selectedCompany);
        return null;
    }

    public String cancelAdd() {
        return null;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }


}
