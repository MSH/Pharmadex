package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@Repository
public class ProdApplicationsDAO implements Serializable {

    private static final long serialVersionUID = 8496860054039645100L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public ProdApplications findProdApplications(long id) {
        return entityManager.find(ProdApplications.class, id);
    }

    @Transactional
    public ProdApplications findProdApplicationByProduct(Long prodId) {
        try {
            ProdApplications prodApp = (ProdApplications) entityManager.createQuery("select a from ProdApplications a left join a.prod.atcs atc where a.prod.id = :prodId ")
                    .setParameter("prodId", prodId).getSingleResult();
            return prodApp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Transactional
    public String saveApplication(ProdApplications prodApplications) {
        entityManager.persist(prodApplications);
        return "persisted";
    }

    @Transactional
    public String updateApplication(ProdApplications prodApplications) {
        entityManager.merge(prodApplications);
        return "persisted";
    }

    public List<Company> findCompanies(Long prodId) {
        return entityManager.createQuery("select c from Company c where c.product.id = :prodId ")
                .setParameter("prodId", prodId)
                .getResultList();
    }


    @Transactional
    public List<ProdApplications> allProdApplications() {
        return (List<ProdApplications>) entityManager.createQuery("select a from ProdApplications a").getResultList();
    }

    @Transactional
    public List<ProdApplications> findSubmittedApp() {
        return (List<ProdApplications>) entityManager.createQuery("select a from ProdApplications a where a.regState not in (:regstate1,:regstate2) ")
                .setParameter("regstate1", RegState.SAVED)
                .setParameter("regstate2", RegState.REGISTERED)
                .getResultList();
    }

    public List<ProdApplications> findProdApplicationsByReviewer(Integer userId) {
        return entityManager.createQuery("select p from ProdApplications p left join p.statusUser s where (s.module1.userId = :userId or s.module2.userId = :userId" +
                " or s.module3.userId = :userId or s.module4.userId = :userId) and p.regState = :regState")
                .setParameter("userId", userId)
                .setParameter("regState", RegState.REVIEW_BOARD)
                .getResultList();
    }

    /**
     * Fetches prodapplications from data base based on passed parameters
     * Construct the parameters with the field name as string and the  vlaue as an object.
     * You will have to add a condition for setting the value for joins or non direct mapping.
     * For everything else the default operator is equal and default root is prodApp entity.
     *
     * @param params
     * @return
     */
    public List<ProdApplications> getProdAppByParams(HashMap<String, Object> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProdApplications> query = cb.createQuery(ProdApplications.class);
        Root<ProdApplications> prodApp = query.from(ProdApplications.class);
        Join user = prodApp.join("user", JoinType.LEFT);
        Join statusUser = prodApp.join("statusUser", JoinType.LEFT);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = null;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("regState") && param.getValue() != null) {
                Expression<String> exp = prodApp.get("regState");
                p = exp.in(params.get("regState"));
            } else if (param.getKey().equals("userId") && param.getValue() != null) {
                Expression userid = user.get("userId");
                p = cb.equal(userid, param.getValue());
            } else {

            }

            predicateList.add(p);
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicateList.toArray(predicates);
        if (predicateList.size() > 0)
            query.where(predicates);
        return entityManager.createQuery(query).getResultList();
    }
}
