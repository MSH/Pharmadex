package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.Inn;
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
	@Autowired
	private InnDAO innDAO;

	@Transactional
	public Product findProduct(long id) {
		return (Product) entityManager.createQuery("select p from Product p where p.id = :prodId")
				.setParameter("prodId", id)
				.getSingleResult();
	}

	@Transactional
	public String saveProduct(Product product) {
		if(product != null){
			if(product.getDosUnit() != null){
				if(product.getDosUnit().getId() == 0){
					DosUom du = product.getDosUnit();
					entityManager.persist(du);
					product.setDosUnit(du);
				}
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


	public List<Product> findByName(String name){
		String q = "select p.id as id, p.prod_name as prodName, p.gen_name as genName "
				+ "from product as p "
				+ "where p.gen_name = :prodName or p.prod_name = :prodName";
		List<Product> products = entityManager
				.createNativeQuery(q)
				.setParameter("prodName", name)
				.getResultList();
		return products;
	}
	/**
	 * In DB Bangladesh in table prodApplications no field 'active'
	 * @return
	 */
	public List<ProdTable> findProductsByState(RegState regState) {
		String q = "select p.id as id, p.prod_name as prodName, p.gen_name as genName, "
				+ "p.prod_cat as prodCategory, a.appName, pa.registrationDate, pa.regExpiryDate, "
				+ "c.companyName as manufName, pa.prodRegNo, p.prod_desc, pa.id as prodAppID, p.fnm as fnm, a.applcntId " +
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
		//q += " and pa.active = :active ;";
		products = entityManager
				.createNativeQuery(q)
				//.setParameter("active", true)
				.setParameter("regState", "" + regState)
				.setParameter("companyType", "" + CompanyType.FIN_PROD_MANUF)
				.getResultList();

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
			prodTable.setFnm((String) objArr[11]);
			prodTable.setAppId(Long.valueOf("" + objArr[12]));
			prodTables.add(prodTable);
		}
		return prodTables;
	}

	/**
	 * In DB Bangladesh in table prodApplications no field 'active'
	 * @return
	 */
	public List<ProdTable> findProductsByStateBD(RegState regState) {
		String q = "select distinct p.id as id, p.prod_name as prodName, p.gen_name as genName, "
				+ "p.prod_cat as prodCategory, a.appName, pa.registrationDate, pa.regExpiryDate, "
				+ "p.manuf_name as manufName, pa.prodRegNo, p.prod_desc, pa.id as prodAppID, p.fnm as fnm " +
				"from prodApplications pa, product p, applicant a, prod_company pc, company c " +
				"where pa.PROD_ID = p.id " +
				"and a.applcntId = pa.APP_ID " +
				"and pc.prod_id = p.id " +
				"and pa.regState = :regState ";
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.setParameter("regState", "" + regState)
				.getResultList();

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
			prodTable.setFnm((String) objArr[11]);
			prodTables.add(prodTable);
		}
		return prodTables;
	}

	public String getMinRegistrationYearInDB() {
		String year = "";
		String q = "select min(pa.registrationDate)" +
				" from prodApplications pa" +
				" where pa.regState = :regState ";
		
		Object y = entityManager
				.createNativeQuery(q)
				.setParameter("regState", "" + RegState.REGISTERED)
				.getSingleResult();
		
		Date d = (Date) y;
		year = (d.getYear() + 1900) + "";
		return year;
	}
	/**
	 * Register products by filter
	 * @return
	 */
	public List<ProdTable> findProductsByFilter(RegState regState, Long innId, Date start, Date end) {
		String addJoin = "", addAnd = "";
		
		if(innId != null && innId > 0){
			Inn inn = innDAO.findInnById(innId);
			addJoin = " left join p.inns inn";
			addAnd = " and (inn.inn = " + innId + " or p.dosStrength like '%" + inn.getName() + "%'" + ")";
		}
		
		String ctype = CompanyType.FIN_PROD_MANUF + "";
		String rstate = regState + "";
		
		String sql = "select p.id, p.prodName, p.genName, p.prodCategory, a.appName, "
				+ "pa.registrationDate, pa.regExpiryDate, p.manufName, pa.prodRegNo, p.prodDesc, pa.id, p.fnm "
				+ " from Product p"
				+ " join p.prodApplicationses pa"
				+ " join pa.applicant a"
				+ " join p.prodCompanies pc"
				+ addJoin
				+ " where pc.companyType like '" + ctype + "'"
				+ " and pa.active = 1 "
				+ " and pa.regState like '" + rstate + "'"
				+ addAnd;

		SimpleDateFormat dt = new SimpleDateFormat("yyyyy-MM-dd");
		if(start != null && end != null){
			String st = dt.format(start);
			String en = dt.format(end);
			sql += " and (pa.registrationDate between '" + st + "' and '" + en + "')";
		}else if(start != null && end == null){
			String st = dt.format(start);
			sql += " and pa.registrationDate >= '" + st + "'";
		}else if(start == null && end != null){
			String en = dt.format(end);
			sql += " and pa.registrationDate <= '" + en + "'";
		}
		
		List<Object[]> products = entityManager.createQuery(sql).getResultList();
		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		ProdTable prodTable;
		for (Object[] objArr : products) {
			prodTable = new ProdTable();
			prodTable.setId(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setGenName((String) objArr[2]);
			prodTable.setProdCategory((ProdCategory) objArr[3]);
			prodTable.setAppName((String) objArr[4]);
			prodTable.setRegDate((Date) objArr[5]);
			prodTable.setRegExpiryDate((Date) objArr[6]);
			prodTable.setManufName((String) objArr[7]);
			prodTable.setRegNo((String) objArr[8]);
			prodTable.setProdDesc((String) objArr[9]);
			prodTable.setProdAppID(Long.valueOf("" + objArr[10]));
			prodTable.setFnm((String) objArr[11]);
			prodTables.add(prodTable);
		}
		
		Collections.sort(prodTables, new Comparator<ProdTable>() {
			@Override
			public int compare(ProdTable o1, ProdTable o2) {
				return o1.getProdName().compareTo(o2.getProdName());
			}
		});
		
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
	/**
	 * Find all products that currently 
	 * @return
	 */
	public List<ProdTable> findProcessingProducts() {
		String q = "select distinct p.id as id, p.prod_name as prodName, p.gen_name as genName, "
				+ "p.prod_cat as prodCategory, a.appName, pa.registrationDate, pa.regExpiryDate, "
				+ "p.manuf_name as manufName, pa.prodRegNo, p.prod_desc, pa.id as prodAppID, p.fnm as fnm, pa.regState " +
				"from prodApplications pa, product p, applicant a, prod_company pc, company c " +
				"where pa.PROD_ID = p.id " +
				"and a.applcntId = pa.APP_ID " +
				"and pc.prod_id = p.id " +
				"and pa.regState in('FEE', 'VERIFY', 'SCREENING','FOLLOW_UP','REVIEW_BOARD','RECOMMENDED','NOT_RECOMMENDED') ";
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.getResultList();

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
			prodTable.setFnm((String) objArr[11]);
			prodTable.setRegState(RegState.valueOf((String) objArr[12]));
			prodTables.add(prodTable);
		}
		return prodTables;
	}

	/**
	 * Find applications that have completed review, however have not Executive Summary
	 * @param moderId
	 * @return
	 */
	public List<ProdTable> findReadyForModer(Long moderId) {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, max(ri.updatedDate) "
				+"FROM prodapplications pa "
				+"left join review_info ri on ri.prod_app_id=pa.id and ri.reviewStatus = 'ACCEPTED' "
				+"join review_info rim on rim.prod_app_id=pa.id "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.regState in ('FOLLOW_UP', 'REVIEW_BOARD') and pa.MODERATOR_ID=:MODER "
				+"group by pa.id "
				+"having count(ri.id) = count(rim.id);";
				
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q).setParameter("MODER", moderId)
				.getResultList();

		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		ProdTable prodTable;
		for (Object[] objArr : products) {
			prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTables.add(prodTable);
		}
		return prodTables;
	}
	/**
	 * Find all application that have assigned Moderator, however 
	 * @param moderId
	 * @return
	 */
	public List<ProdTable> findReadyToAssignReviewers(Long moderId) {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, pa.updatedDate "
				+"FROM prodapplications pa "
				+"left join review_info ri on ri.prod_app_id=pa.id "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.MODERATOR_ID=:MODER and pa.RegState <> 'REGISTERED'"
				+"group by pa.id "
				+"having count(ri.id)=0 ;";
				
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q).setParameter("MODER", moderId)
				.getResultList();

		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		ProdTable prodTable;
		for (Object[] objArr : products) {
			prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTables.add(prodTable);
		}
		return prodTables;
	}
	
	/**
	 * select all applications with regState RECOMMNDED, NOT_RECOMMENDED
	 * @return
	 */
	public List<ProdTable> findReadyForHead() {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, pa.updatedDate, pa.regState "
				+"FROM prodapplications pa "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.regState in ('RECOMMENDED', 'NOT_RECOMMENDED');";
				
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.getResultList();

		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		ProdTable prodTable;
		for (Object[] objArr : products) {
			prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTable.setRegState(RegState.valueOf((String) objArr[5]));
			prodTables.add(prodTable);
		}
		return prodTables;
	}
	/**
	 * Find application that will be expire in next 45 days
	 * @param applId if null, for all, otherwise only for this applicant
	 * @return
	 */
	public List<ProdTable> findAboutToExpire(Long applId) {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, pa.registrationDate, pa.regExpiryDate, datediff(pa.regExpiryDate, now()) "
				+"FROM prodapplications pa "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.regState='REGISTERED' and datediff(pa.regExpiryDate, now())<45 and datediff(now(), pa.regExpiryDate)<0";
		
		if(applId != null){
			q=q+" and pa.APP_ID='"+applId +"';";
		}else{
			q=q+";";
		}
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.getResultList();
		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		for (Object[] objArr : products) {
			ProdTable prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTable.setRegExpiryDate((Date) objArr[5]);
			prodTable.setDaysToExpire(((BigInteger)objArr[6]).intValue());
			prodTables.add(prodTable);
		}
		return prodTables;
	}
	
	/**
	 * Find products registered in last 45 days
	 * @return
	 */
	public List<ProdTable> findJustRegistered() {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, pa.registrationDate, datediff(now(), pa.registrationDate) "
				+"FROM prodapplications pa "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.regState='REGISTERED' and datediff(now(), pa.registrationDate)<45; ";
		
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.getResultList();
		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		for (Object[] objArr : products) {
			ProdTable prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTable.setDaysFromRegistration(((BigInteger)objArr[5]).intValue());
			prodTables.add(prodTable);
		}
		return prodTables;
	}
	/**
	 * Find products that lost registration in last 45 days
	 * @return
	 */
	public List<ProdTable> findJustLost() {
		String q = "SELECT pa.id, p.prod_name, a.applcntId, a.appName, pa.registrationDate, pa.regExpiryDate, datediff(now(), pa.registrationDate) "
				+"FROM prodapplications pa "
				+"join product p on pa.PROD_ID=p.id "
				+"join applicant a on pa.APP_ID=a.applcntId "
				+"where pa.regState='REGISTERED' and datediff(now(), pa.regExpiryDate)<45 and datediff(now(), pa.regExpiryDate)>0; ";
		
		List<Object[]> products = null;
		products = entityManager
				.createNativeQuery(q)
				.getResultList();
		List<ProdTable> prodTables = new ArrayList<ProdTable>();
		for (Object[] objArr : products) {
			ProdTable prodTable = new ProdTable();
			prodTable.setProdAppID(Long.valueOf("" + objArr[0]));
			prodTable.setProdName((String) objArr[1]);
			prodTable.setAppId(Long.valueOf("" + objArr[2]));
			prodTable.setAppName((String) objArr[3]);
			prodTable.setRegDate((Date) objArr[4]);
			prodTable.setRegExpiryDate((Date) objArr[5]);
			prodTable.setDaysFromRegistration(((BigInteger)objArr[6]).intValue());
			prodTables.add(prodTable);
		}
		return prodTables;
	}

}

