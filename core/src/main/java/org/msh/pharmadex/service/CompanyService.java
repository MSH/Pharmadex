package org.msh.pharmadex.service;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.iface.CompanyDAO;
import org.msh.pharmadex.dao.iface.ProdCompanyDAO;
import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	@Autowired
	ProductService productService;

	public List<Company> findAllManufacturers() {
		List<Company> list = companyDAO.findAll();
		if(list != null && list.size() > 0)
			Collections.sort(list, new Comparator<Company>() {
				@Override
				public int compare(Company o1, Company o2) {
					String n1 = (o1 != null ? o1.getCompanyName() : "");
					String n2 = (o2 != null ? o2.getCompanyName() : "");
					return n1.compareTo(n2);
				}
			});
		return list;
	}

	@Transactional
	public String removeCompany(Company company) {
		companyDAO.delete(company);
		return "removed";
	}

	@Transactional
	public List<ProdCompany> addCompany(Product prod, Company selectedCompany, List<String> companyTypes) {
		if (companyTypes.size() < 1)
			return null;

		if (prod == null)
			return null;

		if (selectedCompany == null)
			return null;
		Address address = selectedCompany.getAddress();
		if (address != null && address.getCountry() != null) {
			selectedCompany.getAddress().setCountry(countryService.findCountryById(address.getCountry().getId()));
		}

		List<ProdCompany> prodCompanies = prod.getProdCompanies();
		if (prodCompanies == null) {
			prodCompanies = new ArrayList<ProdCompany>();
		}

		if(selectedCompany.getId() != null)
			selectedCompany = saveCompany(selectedCompany);// findCompanyById(selectedCompany.getId());
		else {
			selectedCompany = saveCompany(selectedCompany);
			globalEntityLists.setManufacturers(null);
		}

		for (String ct : companyTypes) {
			if(ct != null&&ct != "" && CompanyType.valueOf(ct).equals(CompanyType.FIN_PROD_MANUF))
				prod.setManufName(selectedCompany.getCompanyName());
			ProdCompany prodCompany = new ProdCompany(prod, selectedCompany, CompanyType.valueOf(ct));
			if(!containsProdCompany(prodCompanies, prodCompany))
				prodCompanies.add(prodCompany);
		}
		prod.setProdCompanies(prodCompanies);
		
		prodCompanyDAO.flush();
		return prod.getProdCompanies();
	}

	private boolean containsProdCompany(List<ProdCompany> list, ProdCompany value){
		if(list != null && list.size() > 0){
			if(value != null){
				for(ProdCompany pc: list){
					if(pc.getCompany().getId().intValue() == value.getCompany().getId().intValue()
							&& 
							pc.getCompanyType().equals(value.getCompanyType()))
						return true;
				}
			}
		}
		return false;
	}

	@Transactional
	public Company saveCompany(Company company) {
		return companyDAO.save(company);
	}

	@Transactional
	public Company findCompanyById(Long id) {
		return companyDAO.findOne(id);
	}
	
	@Transactional
	public Company findCompanyByName(String name) {
		List<Company> list = companyDAO.findAll();
		
		for (Company c : list) {
            if (c.getCompanyName().equalsIgnoreCase(name))
                return c;
        }
		return null;
	}

	public String removeProdCompany(ProdCompany selectedCompany) {
		prodCompanyDAO.delete(selectedCompany);
		return "removed";
	}

	public List<ProdCompany> findCompanyByProdID(Long prodID) {
		List<ProdCompany> list = prodCompanyDAO.findByProduct_Id(prodID);
		if(list != null && list.size() > 0){
			for(ProdCompany pc:list){
				Hibernate.initialize(pc.getCompany());
			}
		}
		return list;
	}
}
