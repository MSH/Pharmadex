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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.dao.iface.DosageFormDAO;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: usrivastava
 */
@Repository
public class ProdApplicationsDAO implements Serializable {

    private static final long serialVersionUID = 8496860054039645100L;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private DosageFormDAO dosageFormDAO;
    @Autowired
    private DosUomDAO dosUomDAO;

    @Transactional
    public ProdApplications findProdApplications(long id) {
        ProdApplications prodApp = (ProdApplications) entityManager.createQuery("select pa from ProdApplications pa where pa.id = :prodId ")
                .setParameter("prodId", id)
                .getSingleResult();
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


            return prodApp;
    }

    public ProdApplications findActiveProdAppByProd(Long prodID){
    	List<ProdApplications> list = entityManager.createQuery("select pa from ProdApplications pa where pa.product.id = :prodID and pa.active = true")
                .setParameter("prodID", prodID).getResultList();
    	
        ProdApplications pa = null;
        if(list != null && list.size() > 0)
        	pa = list.get(0);
        return pa;
    }

    @Transactional
    public List<ProdApplications> findProdApplicationByProduct(Long prodId) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProdApplications> cq = cb.createQuery(ProdApplications.class);
            Root<ProdApplications> paRoot = cq.from(ProdApplications.class);
//            Join<ProdApplications, User> userJoin = paRoot.join("user", JoinType.LEFT);
            Join<ProdApplications, Product> prodJoin = paRoot.join("product");

//            paRoot.fetch("user", JoinType.LEFT);
            paRoot.fetch("product", JoinType.LEFT);
//            paRoot.fetch("applicant", JoinType.LEFT);


            Predicate p = cb.equal(prodJoin.get("id"), prodId);

            cq.select(paRoot).where(p);
            List<ProdApplications> prodApp = entityManager.createQuery(cq).getResultList();
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
        Join<ProdApplications, User> userJoin = root.join("createdBy", JoinType.LEFT);
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
        Join<ProdApplications, User> userJoin = root.join("createdBy", JoinType.LEFT);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = null;

        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("startDt") && params.get("endDt") != null) {
                p = builder.between(root.<Date>get("regExpiryDate"), (Date) params.get("startDt"), (Date) params.get("endDt"));
            }
            if (param.getKey().equals("users") && param.getValue() != null) {
                List<Long> userIdList = new ArrayList<Long>();
                for (User u : (List<User>) params.get("users")) {
                    userIdList.add(u.getUserId());
                }
                Expression<Integer> userIdExp = userJoin.<Integer>get("userId");
                p = userIdExp.in(userIdList);
            }
            if(p != null)
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
    public List<ProdApplications> findSavedProdApp(Long loggedInUserID) {
        List<ProdApplications> prodApplicationses = entityManager.createQuery("select pa from ProdApplications pa join fetch pa.product where pa.regState=:regState " +
                "and (pa.createdBy.userId = :userId or pa.applicantUser.userId =:userId)")
                .setParameter("userId", loggedInUserID)
                .setParameter("regState", RegState.SAVED)
                .getResultList();
        return prodApplicationses;


    }

    @Transactional
    public String saveApplication(ProdApplications prodApplications) {
        entityManager.persist(prodApplications);
        return "persisted";
    }

    @Transactional
    public ProdApplications updateApplication(ProdApplications prodApplications) {
        if(prodApplications != null && prodApplications.getProduct() != null){
    		if(prodApplications.getProduct().getDosUnit() != null){
    			DosUom du = prodApplications.getProduct().getDosUnit();
        		if(du.getId() == 0){
        			// verification
        			DosUom du2 = findDosUom(du.getUom());
        			if(du2 == null)
        				entityManager.persist(du);
        			else
        				du = du2;
        			prodApplications.getProduct().setDosUnit(du);
        		}
        	}
    	}
        prodApplications = entityManager.merge(prodApplications);
        return prodApplications;
    }

    public boolean moveToArchive(ProdApplications prodApplications, Date archivingDate){
        prodApplications.setRegState(RegState.ARCHIVE);

    	prodApplications.setArchivingDate(archivingDate);
    	entityManager.merge(prodApplications);
    	return true;
    }
    public DosUom findDosUom(String uom) {
    	List<DosUom> list = entityManager.createQuery("select du from DosUom du")
                .getResultList();
    	if(list != null){
    		for(DosUom d:list){
    			if(d.getUom().equals(uom))
    				return d;
    		}
    	}
        return null;
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
        Fetch<ProdApplications, Product> prod =  prodApp.fetch("product", JoinType.LEFT);
        prodApp.fetch("applicant", JoinType.LEFT);
        prod.fetch("dosForm", JoinType.LEFT);
        prod.fetch("dosUnit", JoinType.LEFT);
        prod.fetch("pharmClassif", JoinType.LEFT);
        prodApp.fetch("appointment", JoinType.LEFT);
        prodApp.fetch("moderator", JoinType.LEFT);
        prod.fetch("adminRoute", JoinType.LEFT);
        prod.fetch("pricing", JoinType.LEFT);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Predicate p = null;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("regState") && param.getValue() != null) {
                Expression<String> exp = prodApp.get("regState");
                p = exp.in(params.get("regState"));
            } else if (param.getKey().equals("userId") && param.getValue() != null) {
                Join user = prodApp.join("createdBy", JoinType.LEFT);
                Expression userid = user.get("userId");
                Join user2 = prodApp.join("applicantUser", JoinType.LEFT);
                Expression userid2 = user2.get("userId");
                p = cb.or((cb.equal(userid, param.getValue())),(cb.equal(userid2, param.getValue())));
            } else if (param.getKey().equals("moderatorId") && param.getValue() != null) {
                Join user = prodApp.join("moderator", JoinType.LEFT);
                Expression moderatorId = user.get("userId");
                p = cb.equal(moderatorId, param.getValue());
            } else if (param.getKey().equals("appID") && param.getValue() != null) {
                Join user = prodApp.join("applicant", JoinType.LEFT);
                Expression appID = user.get("applcntId");
                p = cb.equal(appID, param.getValue());
            } else if (param.getKey().equals("reviewerId") && param.getValue() != null) {
                Join<Review, ProdApplications> user = prodApp.join("reviews", JoinType.LEFT).join("user");
                Expression reviewId = user.get("userId");
                p = cb.equal(reviewId, param.getValue());
            } else if (param.getKey().equals("reviewerInfoId") && param.getValue() != null) {
                Join<ReviewInfo, ProdApplications> user = prodApp.join("reviewInfos", JoinType.LEFT).join("reviewer");
                Expression reviewId = user.get("userId");
                Join user2 = prodApp.join("createdBy", JoinType.LEFT);
                Expression userid2 = user2.get("userId");
                p = cb.or((cb.equal(reviewId, param.getValue())),(cb.equal(userid2, param.getValue())));
            } else if (param.getKey().equals("regExpDate") && param.getValue() != null) {
                p = cb.lessThan(prodApp.<Date>get("regExpiryDate"), (Date) param.getValue());
            } else if (param.getKey().equals("prodAppType") && param.getValue() != null) {
                p = cb.equal(prodApp.<Date>get("prodAppType"), param.getValue());
            }


            predicateList.add(p);
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicateList.toArray(predicates);
        if (predicateList.size() > 0)
            query.where(predicates);
        return entityManager.createQuery(query).getResultList();
    }

    public List<ProdApplications> findProdAppByReviewer(HashMap params) {
        if (params == null)
            return null;

        Long reviewer = (Long) params.get("reviewer");
        List<RegState> regStates = (List<RegState>) params.get("regState");

        if (reviewer == null)
            return null;

        List<ProdApplications> prodApplicationses = entityManager.createQuery("select pa from ProdApplications pa left join pa.reviewInfos ri where pa.regState in (:regStates) " +
                "and  (pa.createdBy.userId = :reviewer )")
                .setParameter("reviewer", reviewer)
                .setParameter("regStates", regStates)
                .getResultList();
        return prodApplicationses;
    }

    public Long findApplicationCount() {
        return (Long) entityManager.createQuery("select count(pa.id) from ProdApplications pa ")
                .getSingleResult();
    }

    public List<ProdApplications> findProdAppByNo(String prodAppNo) {
        return entityManager.createQuery("from ProdApplications pa where pa.prodAppNo = :prodAppNo ")
                .getResultList();
    }

    public List<ProdApplications> findProdAppByReviewStatus(HashMap params) {
        if (params == null)
            return null;
        String addparam="";
        if(params.get("moderatorId")!=null) addparam=" and pa.moderator="+params.get("moderatorId").toString();
        if(params.get("reviewer")!=null) addparam=" and ri.reviewer=" +params.get("reviewer").toString();;;
        //params.get("reviewerId");

        if (params.get("userId")!=null)  addparam=" and pa.applicantUser="+params.get("userId").toString();

        List<ReviewStatus> rs = (List<ReviewStatus>) params.get("reviewState");
        List<RegState> regStates = (List<RegState>) params.get("regState");
   List<ProdApplications> prodApplicationses = entityManager.createQuery("select pa from ProdApplications pa left join pa.reviewInfos ri where pa.regState in (:regStates) " +
                "and  (ri.reviewStatus = :rs )"+addparam)
                .setParameter("rs", rs)
                .setParameter("regStates", regStates)
                .getResultList();
        return prodApplicationses;

    }
}
