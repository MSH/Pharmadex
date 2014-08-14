package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.CompanyDAO;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class CompanyService implements Serializable {
    private static final long serialVersionUID = 4206260587337054237L;


    @Autowired
    CompanyDAO companyDAO;

    public List<Company> findAllManufacturers() {
        return companyDAO.findByCompanyTypeOrderByCompanyNameAsc(CompanyType.FIN_PROD_MANUF);
    }

    @Transactional
    public String removeCompany(Company company) {
        companyDAO.delete(company);
        return "removed";
    }

    @Transactional
    public Company saveCompany(Company company){
        return companyDAO.save(company);
    }

    @Transactional
    public Company findCompanyById(Long id){
        return companyDAO.findOne(id);
    }
}
