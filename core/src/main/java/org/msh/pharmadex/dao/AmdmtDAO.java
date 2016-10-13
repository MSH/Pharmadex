package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("amdmtDAO")
@Transactional
public class AmdmtDAO implements Serializable {


    private static final long serialVersionUID = 8792595197570706014L;
    @PersistenceContext
    EntityManager entityManager;

    public List<ProdApplications> findByAmdmtState(AmdmtState amdmtState) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProdApplications> cq = cb.createQuery(ProdApplications.class);
            Root<ProdApplications> paRoot = cq.from(ProdApplications.class);
            Join<ProdApplications, ProdAppAmdmt> prodAppJoin = paRoot.join("prodAppAmdmts", JoinType.LEFT);

//            paRoot.fetch("prodAppAmdmts", JoinType.LEFT);
            paRoot.fetch("product", JoinType.LEFT);


            Predicate p = cb.equal(prodAppJoin.get("amdmtState"), amdmtState);

            cq.select(paRoot).where(p).distinct(true);
            List<ProdApplications> prodApps = entityManager.createQuery(cq).getResultList();
            return prodApps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}

