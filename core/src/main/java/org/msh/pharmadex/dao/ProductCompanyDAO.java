package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.iface.CompanyDAO;
import org.msh.pharmadex.dao.iface.ProdCompanyDAO;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: dudchenko
 */
@Repository
public class ProductCompanyDAO implements Serializable {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private CompanyDAO companyDAO;
    
    @Autowired
    private ProdCompanyDAO prodCompanyDAO;
    
    @Transactional
    public ProdCompany findCompanyByProdCompany(long prodCompanyID) {
    	//Company company = null;
    	ProdCompany prodComp = prodCompanyDAO.findOne(prodCompanyID);
    	if(prodComp != null){
    		Hibernate.initialize(prodComp);
    		Hibernate.initialize(prodComp.getCompany());
    		Hibernate.initialize(prodComp.getCompany().getAddress());
    	}
    	//Hibernate.initialize(company);
    	return prodComp;
    	/*List<Object[]> list = entityManager.createQuery("select pc.company_id from prod_company pc where pc.id = " + prodCompanyID).getResultList();
    	if(list != null && list.size() > 0){
    		Object[] massiv = list.get(0);
    		Long idCompany = (Long) massiv[0];
    		Company company = companyDAO.findOne(idCompany);
    		return company;
    	}
    	return null;*/

       // Hibernate.initialize(prodApp);
       // Hibernate.initialize(prodApp.getProduct());
//        Hibernate.initialize(prodApp.getProduct().getDosForm());
//        Hibernate.initialize(prodApp.getProduct().getDosUnit());
//        Hibernate.initialize(prodApp.getProduct().getAdminRoute());
//        Hibernate.initialize(prodApp.getProduct().getInns());
//        Hibernate.initialize(prodApp.getProduct().getExcipients());
//        Hibernate.initialize(prodApp.getProduct().getAtcs());
//        Hibernate.initialize(prodApp.getProduct().getProdCompanies());
//        Hibernate.initialize(prodApp.getProduct().getPricing());
//        if(prodApp.getProduct().getPricing()!=null) {
//            Hibernate.initialize(prodApp.getProduct().getPricing());
//            Hibernate.initialize(prodApp.getProduct().getPricing().getDrugPrices());
//        }


          
    }

    
}
