package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.CompanyDAO;
import org.msh.pharmadex.dao.iface.ProdCompanyDAO;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class CompanyService implements Serializable {
    private static final long serialVersionUID = 4206260587337054237L;


    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    ProdCompanyDAO prodCompanyDAO;

    @Autowired
    CountryService countryService;

    @Autowired
    GlobalEntityLists globalEntityLists;

    public List<Company> findAllManufacturers() {
        return companyDAO.findAll();
    }

    @Transactional
    public String removeCompany(Company company) {
        companyDAO.delete(company);
        return "removed";
    }

    @Transactional
    public List<ProdCompany> addCompany(Product prod, Company selectedCompany, List<String> companyTypes){
        if(companyTypes.size()<1)
            return null;

        if(prod==null)
            return null;

        if(selectedCompany==null)
            return null;

        List<ProdCompany> prodCompanies;
        List<ProdCompany> companyProds;
        Country c = selectedCompany.getAddress().getCountry();
        c = countryService.findCountryById(c.getId());
        selectedCompany.getAddress().setCountry(c);

        if(prod.getProdCompanies()==null){
            prod.setProdCompanies(new ArrayList<ProdCompany>());
        }
        prodCompanies = prod.getProdCompanies();

        if(selectedCompany.getProdCompanies()==null) {
            selectedCompany.setProdCompanies(new ArrayList<ProdCompany>());
        }else{
            selectedCompany = findCompanyById(selectedCompany.getId());
        }

        companyProds = selectedCompany.getProdCompanies();


        for(String ct : companyTypes) {
            ProdCompany prodCompany = new ProdCompany(prod, selectedCompany, CompanyType.valueOf(ct));
            prodCompanies.add(prodCompany);
            companyProds.add(prodCompany);
        }


        if(selectedCompany.getId()==null) {
            selectedCompany = saveCompany(selectedCompany);
            globalEntityLists.setManufacturers(null);
        }else {
            selectedCompany = findCompanyById(selectedCompany.getId());
        }
        return prodCompanies;
    }

    @Transactional
    public Company saveCompany(Company company){
        return companyDAO.save(company);
    }

    @Transactional
    public Company findCompanyById(Long id){
        return companyDAO.findOne(id);
    }

    public String removeProdCompany(ProdCompany selectedCompany) {
        prodCompanyDAO.delete(selectedCompany);
        return "removed";
    }
}
