package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        List<PurOrder> purOrders = entityManager.createQuery("select porder from PurOrder porder where porder.createdBy.userId = :userId and porder.applicant.applcntId = :applcntId ")
                .setParameter("userId", userId)
                .setParameter("applcntId", applcntId)
                .getResultList();

        initializePurOrder(purOrders);


        return purOrders;
    }

    public List<PurOrder> findAllPIPOrder() {
        List<PurOrder> purOrders = entityManager.createQuery("select porder from PurOrder porder")
                .getResultList();
        initializePurOrder(purOrders);
        return purOrders;
    }

    private void initializePurOrder(List<PurOrder> purOrders) {
        for (PurOrder purOrder : purOrders) {
            Hibernate.initialize(purOrder.getPurProds());
        }
    }

    public List<ProdTable> findProdByLH(Long applcntId) {
        List<Object[]> products =  entityManager.createNativeQuery("select p.id as id, p.prod_name as prodName, p.gen_name as genName, p.prod_cat as prodCategory, a1.appName, pa.registrationDate, pa.regExpiryDate, c.companyName as manufName, pa.prodRegNo, p.prod_desc "+
                "from prodApplications pa, product p, applicant a1, prod_company pc, company c, lic_holder lh, licholder_prod lp "+
                "where pa.PROD_ID = p.id "+
                "and pa.regState = :regState " +
                "and pa.active = :active " +
                "and pc.companyType = :companyType " +
                "and a1.applcntId = pa.APP_ID "+
                "and c.id = pc.company_id "+
                "and lp.licholder_id = lh.id "+
                "and lp.prod_id = p.id "+
                "and pc.prod_id = p.id "+
                "and lh.id in (select l.id from lic_holder l, agent_info ai, applicant a where ai.licholder_id = l.id "+
                "        and a.applcntId = ai.applicant_applcntId "+
                "        and a.applcntId = :appID " +
                        "and sysdate() BETWEEN ai.startDate and ai.endDate  order by a.appname, ai.agent_type);")
                .setParameter("appID", applcntId)
                .setParameter("active", true)
                .setParameter("regState", ""+ RegState.REGISTERED)
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
}
