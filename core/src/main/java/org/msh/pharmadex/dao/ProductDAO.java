package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public Product findProduct(long id) {
        return (Product) entityManager.createQuery("select p from Product p join fetch p.applicant a where p.id = :prodId")
                .setParameter("prodId", id)
                .getSingleResult();
    }

    @Transactional
    public List<Product> allApplicants() {
        return (List<Product>) entityManager.createQuery("select a from Product a").getResultList();
    }

    @Transactional
    public String saveProduct(Product product) {
        entityManager.persist(product);
        return "persisted";
    }

    @Transactional
    public Product updateProduct(Product product) {
        try {

            Product prod = entityManager.merge(product);
            Hibernate.initialize(prod.getInns());
            Hibernate.initialize(prod.getAtcs());
//        Hibernate.initialize(prod.getCompanies());
            Hibernate.initialize(prod.getProdApplications());
            Hibernate.initialize(prod.getApplicant());
            if (prod.getProdApplications() != null) {
                Hibernate.initialize(prod.getProdApplications().getInvoices());
                Hibernate.initialize(prod.getProdApplications().getComments());
                Hibernate.initialize(prod.getProdApplications().getMails());
                Hibernate.initialize(prod.getProdApplications().getProdAppAmdmts());
                Hibernate.initialize(prod.getProdApplications().getProdAppChecklists());
                Hibernate.initialize(prod.getProdApplications().getTimeLines());
                Hibernate.initialize(prod.getProdApplications().getPricing());
                Hibernate.initialize(prod.getProdApplications().getForeignAppStatus());

                if (prod.getProdApplications().getReviews() != null)
                    Hibernate.initialize(prod.getProdApplications().getReviews());
                if (prod.getProdApplications().getPricing() != null) {
                    Hibernate.initialize(prod.getProdApplications().getPricing().getDrugPrices());
                }
                if (prod.getProdCompanies() != null) {
                    Hibernate.initialize(prod.getProdCompanies());
                }
            }
            return prod;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public List<Product> findRegProducts() {
        return entityManager.createQuery("select p from Product p where p.regState = :regstate")
                .setParameter("regstate", RegState.REGISTERED).getResultList();
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
        return (List<Product>) entityManager.createQuery(" select p from Product p join p.applicant a where a.applcntId = :appID")
                .setParameter("appID", appID)
                .getResultList();
    }

    @Transactional
    public Product findProductEager(Long prodId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
//        Join<Product, ProdApplications> prodAppJoin = root.join("prodApplications");
//        Join<Product, Atc> atcJoin = root.join("atcs");
//        Join<Product, Company> companyJoin = root.join("companies");

//        root.fetch("prodApplications");

        Predicate p = cb.equal(root.get("id"), prodId);

        query.select(root).where(p);
        Product prod = entityManager.createQuery(query).getSingleResult();
        Hibernate.initialize(prod.getInns());
        Hibernate.initialize(prod.getAtcs());
        Hibernate.initialize(prod.getProdCompanies());
        Hibernate.initialize(prod.getProdApplications());
        Hibernate.initialize(prod.getApplicant());

        if (prod.getProdApplications() != null) {
            Hibernate.initialize(prod.getProdApplications().getInvoices());
            Hibernate.initialize(prod.getProdApplications().getComments());
            Hibernate.initialize(prod.getProdApplications().getMails());
            Hibernate.initialize(prod.getProdApplications().getProdAppAmdmts());
            Hibernate.initialize(prod.getProdApplications().getProdAppChecklists());
            Hibernate.initialize(prod.getProdApplications().getTimeLines());
            Hibernate.initialize(prod.getProdApplications().getPricing());
            Hibernate.initialize(prod.getProdApplications().getModerator());
            Hibernate.initialize(prod.getProdApplications().getForeignAppStatus());
            if (prod.getProdApplications().getReviews() != null)
                Hibernate.initialize(prod.getProdApplications().getReviews());
            if (prod.getProdApplications().getPricing() != null) {
                Hibernate.initialize(prod.getProdApplications().getPricing().getDrugPrices());
            }
        }
        return prod;


    }

}

