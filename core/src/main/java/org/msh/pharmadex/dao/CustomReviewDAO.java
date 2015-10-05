package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

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

}

