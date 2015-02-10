package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.PurOrder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

}
