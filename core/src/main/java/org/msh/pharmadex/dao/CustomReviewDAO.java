package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class CustomReviewDAO implements Serializable {

    @PersistenceContext
    EntityManager entityManager;

    public List<ReviewInfoTable> findReviewInfoByReview(Long reviewID) {
        List<Object[]> ris = entityManager.createNativeQuery("select ri.id, " +
                "CASE " +
                "        WHEN ri.reviewer_id = :reviewID THEN 'PRIMARY' " +
                "        ELSE 'SECONDARY' " +
                "    END AS rev_type, " +
                "    ri.reviewStatus, ri.assignDate, ri.submitDate, ri.ctdModule, ri.dueDate, ri.recomendType,p.prod_name " +
                "from review_info ri, prodapplications pa, product p " +
                "where ri.prod_app_id = pa.id " +
                "and pa.PROD_ID = p.id " +
                "and (ri.reviewer_id = :reviewID or ri.sec_reviewer_id = :reviewID )")
                .setParameter("reviewID", reviewID)
                .getResultList();

        List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();
        ReviewInfoTable reviewInfoTable = null;
        for (Object[] objArr : ris) {
            reviewInfoTable = new ReviewInfoTable();
            reviewInfoTable.setId(Long.valueOf("" + objArr[0]));
            reviewInfoTable.setRevType((String) objArr[1]);
            reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[2])));
            reviewInfoTable.setAssignDate((Date) objArr[3]);
            reviewInfoTable.setSubmittedDate((Date) objArr[4]);
            reviewInfoTable.setCtdModule((String) objArr[5]);
            reviewInfoTable.setDueDate((Date) objArr[6]);
            if (objArr[7] != null)
                reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[7]));
            reviewInfoTable.setProdName((String) objArr[8]);
            prodTables.add(reviewInfoTable);
        }
        return prodTables;
    }

    /**
     * Fetches review info by primary and seconday reviewer
     *
     * @return
     */
    public List<ReviewInfoTable> findAllPriSecReview() {
        List<Object[]> ris = entityManager.createNativeQuery("select * \n" +
                "from ((select u.name, p.prod_name, ri.reviewStatus, 'PRIMARY' as revType\n" +
                ",ri.assignDate, ri.dueDate, ri.submitDate, pa.prodAppType, pa.prodAppNo, ri.id, ri.recomendType, ri.ctdModule\n" +
                "from review_info ri, prodapplications pa, product p, user u\n" +
                "where ri.prod_app_id = pa.id\n" +
                "and pa.PROD_ID = p.id\n" +
                "and ri.reviewer_id is not null\n" +
                "and u.userid = ri.reviewer_id\n" +
                ")\n" +
                "union \n" +
                "(select u.name, p.prod_name, ri.reviewStatus, 'SECONDARY' as revType\n" +
                ",ri.assignDate, ri.dueDate, ri.submitDate, pa.prodAppType, pa.prodAppNo, ri.id, ri.recomendType, ri.ctdModule\n" +
                "from review_info ri, prodapplications pa, product p, user u\n" +
                "where ri.prod_app_id = pa.id\n" +
                "and pa.PROD_ID = p.id\n" +
                "and ri.sec_reviewer_id is not null\n" +
                "and u.userid = ri.sec_reviewer_id)) review\n" +
                "where reviewStatus <> 'ACCEPTED'\n" +
                "order by name, prod_name;")
                .getResultList();

        List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();
        ReviewInfoTable reviewInfoTable = null;
        for (Object[] objArr : ris) {
            reviewInfoTable = new ReviewInfoTable();
            reviewInfoTable.setRevName((String) objArr[0]);
            reviewInfoTable.setProdName((String) objArr[1]);
            reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[2])));
            reviewInfoTable.setRevType((String) objArr[3]);
            reviewInfoTable.setAssignDate((Date) objArr[4]);
            reviewInfoTable.setDueDate((Date) objArr[5]);
            reviewInfoTable.setSubmittedDate((Date) objArr[6]);
            reviewInfoTable.setProdAppType((ProdAppType.valueOf((String) objArr[7])));
            reviewInfoTable.setProdAppNo((String) objArr[8]);
            reviewInfoTable.setId(Long.valueOf("" + objArr[9]));
            if (objArr[10] != null)
                reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[10]));
            reviewInfoTable.setCtdModule((String) objArr[11]);
            prodTables.add(reviewInfoTable);
        }
        return prodTables;
    }
}

