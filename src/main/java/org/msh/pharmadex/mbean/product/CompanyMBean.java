package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class CompanyMBean {

    @Autowired
    RegHomeMbean regHomeMbean;

    private CompanyType[] companyType;
    private Company selectedCompany;

    @PostConstruct
    private void init(){
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println(" Company Bean instantiating ");
        System.out.println("------------------------------------------------------------------------------------");

        selectedCompany = new Company();
        selectedCompany.getAddress().setCountry(new Country());
        selectedCompany.setCompanyType(CompanyType.MANUFACTURER);
    }

    public String addCompany(){
        selectedCompany.setProduct(regHomeMbean.getProduct());
        regHomeMbean.getCompanies().add(selectedCompany);
        return null;
    }

    public String removeCompany(){
        regHomeMbean.getCompanies().remove(selectedCompany);
        return null;
    }

    public String cancelAdd(){
        return null;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<CompanyType> getCompanyType() {
        return Arrays.asList(CompanyType.values());

    }

    public void setCompanyType(CompanyType[] companyType) {
        this.companyType = companyType;
    }
}
