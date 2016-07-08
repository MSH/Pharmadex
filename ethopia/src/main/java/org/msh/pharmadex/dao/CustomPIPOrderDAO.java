package org.msh.pharmadex.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.mbean.PIPReportItemBean;
import org.springframework.stereotype.Repository;

/**
 * Created by utkarsh on 1/8/15.
 */
@Repository
public class CustomPIPOrderDAO {

    @PersistenceContext
    EntityManager entityManager;


    public List<PIPOrder> findPIPOrderByUser(Long userId, Long applcntId) {
        List<PIPOrder> pipOrders = entityManager.createQuery("select distinct porder from PIPOrder porder " +
                "left join fetch porder.pipProds left join fetch porder.applicant left join fetch porder.applicantUser " +
                "left join fetch porder.createdBy " +
                "where porder.applicant.applcntId = :applcntId ")
                .setParameter("applcntId", applcntId)
                .getResultList();

        return pipOrders;
    }

    public List<PIPOrderLookUp> findAllPIPOrderLookUp(Long applicantType, boolean pip) {
        return entityManager.createQuery("select lookup from PIPOrderLookUp lookup where lookup.pip = :pip and lookup.applicantType.id = :applicantType")
                .setParameter("pip", pip)
                .setParameter("applicantType", applicantType)
                .getResultList();
    }

    public PIPOrder findPIPOrder(Long pipOrderID) {
        PIPOrder pipOrder = (PIPOrder) entityManager.createQuery("select porder from PIPOrder porder " +
                "inner join fetch porder.pipProds prod left join fetch prod.dosForm left join fetch prod.dosUnit left join fetch prod.country " +
                "left join fetch porder.applicant app left join fetch app.applicantType left join fetch app.address.country " +
                "left join fetch porder.applicantUser appUser left join fetch appUser.address.country " +
                "left join fetch porder.createdBy user left join fetch user.address.country " +
                "where porder.id = :pipOrderID ")
                .setParameter("pipOrderID", pipOrderID)
                .getSingleResult();
        return pipOrder;
    }

    public List<PIPOrder> findAllPIPOrder() {
        List<PIPOrder> pipOrders = entityManager.createQuery("select distinct porder from PIPOrder porder " +
                "left join fetch porder.pipProds prod " +
                "left join fetch porder.applicant app left join fetch app.applicantType left join fetch app.address.country " +
                "left join fetch porder.applicantUser appUser left join fetch appUser.address.country " +
                "left join fetch porder.createdBy user left join fetch user.address.country " +
                "where porder.state not in (:state) ")
                .setParameter("state", AmdmtState.WITHDRAWN)
                .getResultList();
        return pipOrders;
    }
// call from piporderreport
    public List<PIPReportItemBean> findAllPIPProds(Map<String, Object> map/*Date startD, Date endD, Applicant applicant*/) {
    	Date startD = (Date) map.get("startDate");
    	Date endD = (Date) map.get("endDate");
    	Long applicantID = map.get("applicant") != null ? (Long)map.get("applicant") : null;
    	String company = map.get("company") != null ? (String)map.get("company") : null;
    	Long countryID = map.get("country") != null ? (Long)map.get("country") : null;
    	String port = map.get("port") != null ? (String)map.get("port") : null;
    	
    	String query = "select p.productName, p.dosStrength, dunit.uom, dform.dosForm, p.productDesc, "
    					+ "count(p.productName), sum(p.quantity), sum(p.totalPrice) from PIPProd p "
    					+ "left join p.pipOrder ord "
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

    	List<PIPReportItemBean> pipProds = new ArrayList<PIPReportItemBean>();
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
    				pipProds.add(item);
    				
    				n++;
    			}
    		}
    	}
        /*if (applicant == null) {
            List<PIPProd> pipProds = entityManager.createQuery("select distinct p from PIPProd p " +
                    "left join fetch p.pipOrder prod " +
                    "where (prod.state = :state) AND (prod.approvalDate BETWEEN :startD AND :endD) "+
                    " order by p.productName"
                    )
                    .setParameter("state", AmdmtState.APPROVED)
                    .setParameter("startD", startD)
                    .setParameter("endD", endD)
                    .getResultList();
            return pipProds;
        }else{
            List<PIPProd> pipProds = entityManager.createQuery("select distinct p from PIPProd p " +
                    "left join fetch p.pipOrder prod " +
                    "where (prod.state = :state)" +
                    " AND (prod.approvalDate BETWEEN :startD AND :endD) AND prod.applicant.applcntId = :appId" +
                    " order by p.productName" )
                    .setParameter("state", AmdmtState.APPROVED)
                    .setParameter("startD", startD)
                    .setParameter("endD", endD)
                    .setParameter("appId", applicant.getApplcntId())
                    .getResultList();
            return pipProds;
        }*/
    	return pipProds;
    }
    /*SElECT pp.id, lic.id, lic.name
    FROM pdx_et.pip_prod pp
    INNER JOIN  pdx_et.product p ON pp.productName=p.prod_name
    INNER JOIN pdx_et.licholder_prod lp ON lp.prod_id=p.id
    INNER JOIN pdx_et.lic_holder lic ON lic.id=lp.licholder_id*/

    public List<String> testGet(){
        List res= entityManager.createNativeQuery("SElECT lic.name, pp "+
                " FROM pdx_et.pip_prod pp "+
                "INNER JOIN  pdx_et.product p ON pp.productName=p.prod_name "+
                "INNER JOIN pdx_et.licholder_prod lp ON lp.prod_id=p.id "+
                "INNER JOIN pdx_et.lic_holder lic ON lic.id=lp.licholder_id")
                .getResultList();
        return res;
    }
    private void initializePIPOrder(List<PIPOrder> pipOrders) {
        for (PIPOrder pipOrder : pipOrders) {
            Hibernate.initialize(pipOrder.getPipProds());
        }
    }

}
