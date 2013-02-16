package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.Company;
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
public class ProductDAO implements Serializable{

    private static final long serialVersionUID = 6366730721078424594L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Product findProduct(long id){
        return entityManager.find(Product.class, id);
    }

    @Transactional
    public List<Product> allApplicants(){
        return (List<Product>)entityManager.createQuery("select a from Product a").getResultList();
    }

    @Transactional
    public String saveProduct(Product product){
        entityManager.persist(product);
        return "persisted";
    }

    @Transactional
    public String updateProduct(Product product){
        entityManager.merge(product);
        entityManager.flush();
        return "updated";
    }

    public List<Product> findRegProducts(){
        return entityManager.createQuery("select p from Product p where p.regState = :regstate")
                .setParameter("regstate", RegState.REGISTERED).getResultList();
    }

    public List<Company> findCompanies(Long prodId){
        return entityManager.createQuery("select c from Company c where c.product.id = :prodId")
                .setParameter("prodId", prodId).getResultList();
    }

    public List<Product> findProductByFilter(HashMap<String, Object> params){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate p = cb.conjunction();
        for (Map.Entry<String, Object> param: params.entrySet()){
            p = cb.and(p, cb.equal(root.get(param.getKey()), param.getValue()));
        }

        query.select(root).where(p);
        return entityManager.createQuery(query).getResultList();
    }
}

