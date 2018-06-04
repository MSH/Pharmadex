package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.iface.PurProdDAO;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.domain.PurProd;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.PIPReportItemBean;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.util.StrTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by utkarsh on 1/8/15.
 */
@Repository
public class CustomPurOrderDAO {

    @PersistenceContext
    EntityManager entityManager;

    public List<PIPOrderLookUp> findAllPurOrderLookUp(){
        return entityManager.createQuery("select lookup from PIPOrderLookUp lookup where lookup.pip = :pip ")
                .setParameter("pip", false)
                .getResultList();
    }

    public List<PurOrder> findPurOrderByUser(Long userId, Long applcntId) {
        String q= "select porder from PurOrder porder where porder.applicant.applcntId = :applcntId ";
        q += "order by porder.createdDate desc ";
        List<PurOrder> purOrders = entityManager.createQuery(q)
                .setParameter("applcntId", applcntId)
                .getResultList();

        initializePurOrder(purOrders);


        return purOrders;
    }

    public PurOrder findPurOrder(Long pipOrderID) {
        PurOrder pipOrder = (PurOrder) entityManager.createQuery("select porder from PurOrder porder " +
                "left join fetch porder.purProds purprod left join fetch purprod.product prod " +
                "left join fetch prod.adminRoute left join fetch prod.dosForm " +
                "left join fetch prod.dosUnit left join fetch prod.pharmClassif " +
                "left join fetch prod.pricing " +
                "left join fetch porder.applicant app left join fetch app.applicantType left join fetch app.address.country " +
                "left join fetch porder.applicantUser appUser left join fetch appUser.address.country " +
                "left join fetch porder.createdBy user left join fetch user.address.country " +
                "where porder.id = :pipOrderID ")
                .setParameter("pipOrderID", pipOrderID)
                .getSingleResult();
        return pipOrder;
    }


    public List<PurOrder> findAllPurOrder() {
        String q = "select distinct porder from PurOrder porder " +
                "left join fetch porder.purProds purprod left join fetch purprod.product prod " +
                "left join fetch prod.adminRoute left join fetch prod.dosForm " +
                "left join fetch prod.dosUnit left join fetch prod.pharmClassif " +
                "left join fetch prod.pricing " +
                "left join fetch porder.applicant app left join fetch app.applicantType left join fetch app.address.country " +
                "left join fetch porder.applicantUser appUser left join fetch appUser.address.country " +
                "left join fetch porder.createdBy user left join fetch user.address.country " +
                "where porder.state not in (:state)";
        q += "order by porder.createdDate desc ";
        List<PurOrder> purOrders = entityManager.createQuery(q)
                .setParameter("state", AmdmtState.WITHDRAWN)
                .getResultList();
        return purOrders;
    }

    private void initializePurOrder(List<PurOrder> purOrders) {
        for (PurOrder purOrder : purOrders) {
            Hibernate.initialize(purOrder.getPurProds());
        }
    }

    public List<ProdTable> findProdByLH(Long applcntId, String substr) {
        final String today = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
        final String query = "select p.id as id, p.prod_name as prodName, p.gen_name as genName, p.prod_cat as prodCategory, a1.appName, pa.registrationDate, pa.regExpiryDate, c.companyName as manufName, pa.prodRegNo, p.prod_desc " +
                "from prodApplications pa, product p, applicant a1, prod_company pc, company c, lic_holder lh, licholder_prod lp " +
                "where pa.PROD_ID = p.id " +
                "and pa.regState = :regState " +
                "and pa.active = :active " +
                "and pc.companyType = :companyType " +
                "and a1.applcntId = pa.APP_ID " +
                "and c.id = pc.company_id " +
                "and lp.licholder_id = lh.id " +
                "and lp.prod_id = p.id " +
                "and pc.prod_id = p.id " +
                "and pa.regExpiryDate > sysdate() " +
                (!StrTools.isEmptyString(substr) ? "and p.prod_name like '" + substr + "%' " : "") +
                "and lh.id in (select l.id from lic_holder l, agent_info ai, applicant a where ai.licholder_id = l.id " +
                "        and a.applcntId = ai.applicant_applcntId " +
                "        and a.applcntId = :appID " +
                "and l.state = 'ACTIVE' " +
                "and sysdate() BETWEEN ai.startDate and ai.endDate  order by a.appname, ai.agent_type);";
        List<Object[]> products = entityManager.createNativeQuery(query)
                .setParameter("appID", applcntId)
                .setParameter("active", true)
                .setParameter("regState", "" + RegState.REGISTERED)
                .setParameter("companyType", "" + CompanyType.FIN_PROD_MANUF)
                .getResultList();
//        List<Product> products =  entityManager.createQuery("select pa from ProdApplications pa where pa.active = :active and pa.regState = :regstate ")
//                .setParameter("regstate", RegState.REGISTERED)
//                .setParameter("active", true)
//                .getResultList();
        List<ProdTable> prodTables = new ArrayList<ProdTable>();
        ProdTable prodTable;
        for(Object[] objArr : products){
            prodTable = new ProdTable();
            prodTable.setId(Long.valueOf(""+objArr[0]));
            prodTable.setProdName((String) objArr[1]);
            prodTable.setGenName((String) objArr[2]);
            prodTable.setProdCategory(ProdCategory.valueOf((String) objArr[3]));
            prodTable.setAppName((String) objArr[4]);
            prodTable.setRegDate((Date) objArr[5]);
            prodTable.setRegExpiryDate((Date) objArr[6]);
            prodTable.setManufName((String) objArr[7]);
            prodTable.setRegNo((String) objArr[8]);
            prodTable.setProdDesc((String) objArr[9]);
            prodTables.add(prodTable);
        }
        return prodTables;

    }

    @Autowired
    private PurProdDAO purProdDAO;
    
// call from popreport
    public List<PurProd> findSelectedPurProds(Date startD, Date endD, Applicant applicant) {
        if (applicant==null) {
            List<PurProd> purProds = entityManager.createQuery("select distinct p from PurProd p " +
                    "left join fetch p.purOrder prod " +
                    "where (prod.state = :state) AND (prod.approvalDate BETWEEN :startD AND :endD) "
            )
                    .setParameter("state", AmdmtState.APPROVED)
                    .setParameter("startD", startD)
                    .setParameter("endD", endD)
                    .getResultList();
            return purProds;
        }else{
            List<PurProd> purProds = entityManager.createQuery("select distinct p from PurProd p " +
                    "left join fetch p.purOrder prod " +
                    "where (prod.state = :state)" +
                    " AND (prod.approvalDate BETWEEN :startD AND :endD) AND prod.applicant.applcntId = :appId" +
                    " order by p.productName" )
                    .setParameter("state", AmdmtState.APPROVED)
                    .setParameter("startD", startD)
                    .setParameter("endD", endD)
                    .setParameter("appId", applicant.getApplcntId())
                    .getResultList();
            return purProds;
        }
    }
    
    public List<PIPReportItemBean> findAllPurProds(Map<String, Object> map) {
    	Date startD = (Date) map.get("startDate");
    	Date endD = (Date) map.get("endDate");
    	Long applicantID = map.get("applicant") != null ? (Long)map.get("applicant") : null;
    	String company = map.get("company") != null ? (String)map.get("company") : null;
    	Long countryID = map.get("country") != null ? (Long)map.get("country") : null;
    	String port = map.get("port") != null ? (String)map.get("port") : null;
    	
    	String query = "select p.productName, p.dosStrength, dunit.uom, dform.dosForm, p.productDesc, "
    					+ "count(p.productName), sum(p.quantity), sum(p.totalPrice) from PurProd p "
    					+ "left join p.purOrder ord "
    					+ "left join p.dosUnit dunit "
    					+ "left join p.dosForm dform ";
    	
    	String query_where = "where (ord.state = :state) AND (ord.approvalDate BETWEEN :startD AND :endD) ";
    	if(applicantID != null && applicantID > 0)
    		query_where +=  " AND ord.applicant.applcntId = " + applicantID;
    	if(company != null && !company.equals(""))
    		query_where +=  " AND p.manufName like '%" + company + "%'";
    	if(countryID != null && countryID > 0){
    		//query +=  "left join p.country cntr ";
    		query_where +=  " AND p.country.id = " + countryID;
    	}
    	if(port != null && !port.equals(""))
    		query_where +=  " AND ord.entryPort like '%" + port + "%'";
    			
    	query += query_where + " group by p.productName, p.dosStrength, dunit.id, dform.uid "
    			+ "order by p.productName";

    	List<Object[]> list = entityManager.createQuery(query)
							.setParameter("state", AmdmtState.APPROVED)
							.setParameter("startD", startD)
							.setParameter("endD", endD)
							.getResultList();

    	List<PIPReportItemBean> purProds = new ArrayList<PIPReportItemBean>();
    	if(list != null){
    		int n = 1;
    		for(Object obj:list){
    			Object[] lst = (Object[]) obj;
    			int s = lst.length;
    			if(s > 0){
    				PIPReportItemBean item = new PIPReportItemBean();
    				item.setNn(n);
    				item.setProdName((String)lst[0]);
    				item.setDosStren((String)lst[1]);
    				item.setUnit((String)lst[2]);
    				item.setDosForm((String)lst[3]);
    				item.setDescript((String)lst[4]);
    				item.setCount((Long)lst[5]);
    				item.setTotalQuan((Long)lst[6]);
    				item.setTotalPrice((Double)lst[7]);
    				purProds.add(item);
    				
    				n++;
    			}
    		}
    	}
    	return purProds;
    }
}
