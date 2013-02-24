package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Repository
public class ProdApplicationsDAO implements Serializable{

    private static final long serialVersionUID = 8496860054039645100L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public ProdApplications findProdApplications(long id){
        return (ProdApplications) entityManager.createQuery("select pa from ProdApplications pa where pa.id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    @Transactional
    public List<ProdApplications> allProdApplications(){
        return (List<ProdApplications>)entityManager.createQuery("select a from ProdApplications a").getResultList();
    }

    @Transactional
    public List<ProdApplications> findProdApplicationsByState(RegState regState, int userId){
        return (List<ProdApplications>)entityManager.createQuery("select a from ProdApplications a where a.regState = :regstate and a.user.id = :userId ")
                    .setParameter("regstate", regState)
                    .setParameter("userId", userId).getResultList();
    }

    @Transactional
    public List<ProdApplications> findSubmittedApp(int userId){
        return (List<ProdApplications>)entityManager.createQuery("select a from ProdApplications a where a.regState != :regstate and a.user.id = :userId ")
                    .setParameter("regstate", RegState.SAVED)
                    .setParameter("userId", userId).getResultList();
    }

    @Transactional
    public List<ProdApplications> findSubmittedApp(){
        return (List<ProdApplications>)entityManager.createQuery("select a from ProdApplications a where a.regState not in (:regstate1,:regstate2) ")
                    .setParameter("regstate1", RegState.SAVED)
                    .setParameter("regstate2", RegState.REGISTERED)
                    .getResultList();
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
}