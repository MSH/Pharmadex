package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;

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
        ProdApplications prodApp = entityManager.find(ProdApplications.class, id);
        Hibernate.initialize(prodApp);
        return prodApp;
    }


    @Transactional
    public ProdApplications findProdApplicationByProduct(Long prodId) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProdApplications> cq = cb.createQuery(ProdApplications.class);
            Root<ProdApplications> paRoot = cq.from(ProdApplications.class);
            Join<ProdApplications, User> userJoin = paRoot.join("user", JoinType.LEFT);
            Join<ProdApplications, Product> prodJoin = paRoot.join("prod");

            paRoot.fetch("user", JoinType.LEFT);


            Predicate p = cb.equal(prodJoin.get("id"), prodId);

            cq.select(paRoot).where(p);
            ProdApplications prodApp = entityManager.createQuery(cq).getSingleResult();
            return prodApp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Transactional
    public ArrayList<ProdApplications> findProdExpiring(HashMap<String, Object> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProdApplications> query = builder.createQuery(ProdApplications.class);
        Root<ProdApplications> root = query.from(ProdApplications.class);
//        Join<ProdApplications, Invoice> invoiceJoin = root.join("invoices");
        Join<ProdApplications, User> userJoin = root.join("user", JoinType.LEFT);
//        root.fetch("invoices", JoinType.RIGHT);

        Predicate p = null;
        if (params.get("startDt") != null && params.get("endDt") != null) {
            p = builder.between(root.<Date>get("regExpiryDate"), (Date) params.get("startDt"), (Date) params.get("endDt"));
        }
//        if (params.get("paymentStatus") != null) {
//            p = builder.equal(invoiceJoin.<PaymentStatus>get("paymentStatus"), params.get("paymentStatus"));
//        }
        if (params.get("users") != null) {
            List<Long> userIdList = new ArrayList<Long>();
            for (User u : (List<User>) params.get("users")) {
                userIdList.add(u.getUserId());
            }
            Expression<Integer> userIdExp = userJoin.<Integer>get("userId");
            p = userIdExp.in(userIdList);
        }

        query.select(root).where(p);
        ArrayList<ProdApplications> prodApps = (ArrayList<ProdApplications>) entityManager.createQuery(query).getResultList();
        return prodApps;
    }

    public ArrayList<ProdApplications> findPendingRenew(HashMap<String, Object> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProdApplications> query = builder.createQuery(ProdApplications.class);
        Root<ProdApplications> root = query.from(ProdApplications.class);
        Join<ProdApplications, Invoice> invoiceJoin = root.join("invoices");
        Join<ProdApplications, User> userJoin = root.join("user", JoinType.LEFT);
//        root.fetch("invoices", JoinType.RIGHT);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = null;

        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("startDt") && params.get("endDt") != null) {
                p = builder.between(root.<Date>get("regExpiryDate"), (Date) params.get("startDt"), (Date) params.get("endDt"));
            }
            if (param.getKey().equals("paymentStatus") && param.getValue() != null) {
                p = builder.equal(invoiceJoin.<PaymentStatus>get("paymentStatus"), param.getValue());
            }
            if (param.getKey().equals("users") && param.getValue() != null) {
                List<Long> userIdList = new ArrayList<Long>();
                for (User u : (List<User>) params.get("users")) {
                    userIdList.add(u.getUserId());
                }
                Expression<Integer> userIdExp = userJoin.<Integer>get("userId");
                p = userIdExp.in(userIdList);
            }
            predicateList.add(p);
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicateList.toArray(predicates);
        if (predicateList.size() > 0)
            query.where(predicates);

        ArrayList<ProdApplications> prodApps = (ArrayList<ProdApplications>) entityManager.createQuery(query).getResultList();
        return prodApps;
    }


    @Transactional
    public String saveApplication(ProdApplications prodApplications) {
        entityManager.persist(prodApplications);
        return "persisted";
    }

    @Transactional
    public ProdApplications updateApplication(ProdApplications prodApplications) {
        Product prod = entityManager.merge(prodApplications.getProd());
        return prod.getProdApplications();
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
        return entityManager.createQuery("select p from ProdApplications p left join p.reviews r where r.user.userId = :userId and p.regState = :regState")
                .setParameter("userId", userId)
                .setParameter("regState", RegState.REVIEW_BOARD)
                .getResultList();
    }

    public List<ProdApplications> findProdApplicationsByHead(Integer userId) {
        return entityManager.createQuery("select p from ProdApplications p left join p.reviews r where r.user.userId = :userId and p.regState = :regState")
                .setParameter("userId", userId)
                .setParameter("regState", RegState.REVIEW_BOARD)
                .getResultList();
    }

    public List<ProdApplications> findProdApplicationsByModerator(Integer userId) {
        return entityManager.createQuery("select p from ProdApplications p left join p.moderator r where r.userId = :userId ")
                .setParameter("userId", userId)
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
//        Join statusUser = prodApp.join("statusUser", JoinType.LEFT);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = null;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("regState") && param.getValue() != null) {
                Expression<String> exp = prodApp.get("regState");
                p = exp.in(params.get("regState"));
            } else if (param.getKey().equals("userId") && param.getValue() != null) {
                Join user = prodApp.join("user", JoinType.LEFT);
                Expression userid = user.get("userId");
                p = cb.equal(userid, param.getValue());
            } else if (param.getKey().equals("moderatorId") && param.getValue() != null) {
                Join user = prodApp.join("moderator", JoinType.LEFT);
                Expression moderatorId = user.get("userId");
                p = cb.equal(moderatorId, param.getValue());
            } else if (param.getKey().equals("reviewerId") && param.getValue() != null) {
                Join<Review, ProdApplications> user = prodApp.join("reviews", JoinType.LEFT).join("user");
                Expression reviewId = user.get("userId");
                p = cb.equal(reviewId, param.getValue());
            } else if (param.getKey().equals("regExpDate") && param.getValue() != null) {
                p = cb.lessThan(prodApp.<Date>get("regExpiryDate"), (Date) param.getValue());
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
