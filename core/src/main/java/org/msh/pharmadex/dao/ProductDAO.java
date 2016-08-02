package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class ProductDAO implements Serializable {

    private static final long serialVersionUID = 6366730721078424594L;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private WorkspaceDAO workspaceDAO;

    @Transactional
    public Product findProduct(long id) {
        return (Product) entityManager.createQuery("select p from Product p where p.id = :prodId")
                .setParameter("prodId", id)
                .getSingleResult();
    }

    @Transactional
    public String saveProduct(Product product) {
    	if(product != null && product.getDosUnit() != null){
    		if(product.getDosUnit().getId() == 0){
    			DosUom du = product.getDosUnit();
    			entityManager.persist(du);
    			product.setDosUnit(du);
    		}
    	}
        entityManager.persist(product);
        return "persisted";
    }

    @Transactional
    public Product updateProduct(Product product) {
        try {
        	Product prod = entityManager.merge(product);
        	return prod;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Transactional
    public int removeProduct(Product product){
        if (product==null) return 0;
        if (product.getId()==null) return 0;
        Long id = product.getId();
        int deleted = entityManager.createQuery("select p from Product p where p.id = :prodId")
                .setParameter("prodId", id)
                .executeUpdate();
        return deleted;
    }

    /**
     * In DB Bangladesh in table prodApplications no field 'active'
     * @return
     */
    public List<ProdTable> findProductsByState(RegState regState) {
    	String q = "select p.id as id, p.prod_name as prodName, p.gen_name as genName, "
				+ "p.prod_cat as prodCategory, a.appName, pa.registrationDate, pa.regExpiryDate, "
				+ "c.companyName as manufName, pa.prodRegNo, p.prod_desc, pa.id as prodAppID " +
        "from prodApplications pa, product p, applicant a, prod_company pc, company c " +
        "where pa.PROD_ID = p.id " +
        "and a.applcntId = pa.APP_ID " +
        "and c.id = pc.company_id " +
        "and pc.prod_id = p.id " +
        "and pa.regState = :regState " +
        "and pc.companyType = :companyType";

    	Workspace w = workspaceDAO.findAll().get(0);
    	String workspName = (w != null ? w.getName():"");
    	
        List<Object[]> products = null;
        if(workspName.equals("Bangladesh")){
        	q += " ;";
        	products = entityManager
            		.createNativeQuery(q)
                    .setParameter("regState", "" + regState)
                    .setParameter("companyType", "" + CompanyType.FIN_PROD_MANUF)
                    .getResultList();
        }else{
        	q += " and pa.active = :active ;";
        	products = entityManager
            		.createNativeQuery(q)
            		.setParameter("active", true)
                    .setParameter("regState", "" + regState)
                    .setParameter("companyType", "" + CompanyType.FIN_PROD_MANUF)
                    .getResultList();
        }

        List<ProdTable> prodTables = new ArrayList<ProdTable>();
        ProdTable prodTable;
        for (Object[] objArr : products) {
            prodTable = new ProdTable();
            prodTable.setId(Long.valueOf("" + objArr[0]));
            prodTable.setProdName((String) objArr[1]);
            prodTable.setGenName((String) objArr[2]);
            prodTable.setProdCategory(ProdCategory.valueOf((String) objArr[3]));
            prodTable.setAppName((String) objArr[4]);
            prodTable.setRegDate((Date) objArr[5]);
            prodTable.setRegExpiryDate((Date) objArr[6]);
            prodTable.setManufName((String) objArr[7]);
            prodTable.setRegNo((String) objArr[8]);
            prodTable.setProdDesc((String) objArr[9]);
            prodTable.setProdAppID(Long.valueOf("" + objArr[10]));
            prodTables.add(prodTable);
        }
        return prodTables;
    }

    public List<Product> findProductByFilter(HashMap<String, Object> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate p = cb.conjunction();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            p = cb.and(p, cb.equal(root.get(param.getKey()), param.getValue()));
        }

        if (params.size() > 0)
            query.select(root).where(p);
        else
            query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Atc> findAtcsByProduct(Long id) {
        return (List<Atc>) entityManager.createQuery("select atc from Atc atc join atc.products p where p.id = :prodId ")
                .setParameter("prodId", id)
                .getResultList();
    }

    public List<Product> findRegProductByApp(Long appID) {
        return (List<Product>) entityManager.createQuery(" select p from Product p left join p.prodApplicationses pa left join pa.applicant a where a.applcntId = :appID")
                .setParameter("appID", appID)
                .getResultList();
    }

    @Transactional
    public Product findProductEager(Long prodId) {
/*
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = cb.equal(root.get("id"), prodId);
        predicateList.add(p);

        query.where(p);
        Product prod = entityManager.createQuery(query).getSingleResult();
*/
        Product prod = findProduct(prodId);
        Hibernate.initialize(prod.getInns());
        Hibernate.initialize(prod.getAtcs());
        Hibernate.initialize(prod.getExcipients());
        Hibernate.initialize(prod.getProdCompanies());
        Hibernate.initialize(prod.getDosUnit());
        Hibernate.initialize(prod.getDosForm());
        Hibernate.initialize(prod.getPricing());
        Hibernate.initialize(prod.getUseCategories());
        
        if (prod.getPricing() != null) {
            Hibernate.initialize(prod.getPricing().getDrugPrices());
        }
        return prod;


    }

    public int findCountRegProduct() {
        List<RegState> regStates = new ArrayList<RegState>();
        regStates.add(RegState.REGISTERED);
        regStates.add(RegState.DISCONTINUED);
        regStates.add(RegState.XFER_APPLICANCY);

        Long count = (Long) entityManager.createQuery("select count(p.id) from Product p left join p.prodApplicationses pa where pa.regState in :regStates and pa.active = true")
                .setParameter("regStates", regStates).getSingleResult();

        return count.intValue();
    }

}

