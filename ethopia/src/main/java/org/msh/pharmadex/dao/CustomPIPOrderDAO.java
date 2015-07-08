package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by utkarsh on 1/8/15.
 */
@Repository
public class CustomPIPOrderDAO {

    @PersistenceContext
    EntityManager entityManager;

    public List<PIPOrder> findPIPOrderByUser(Long userId, Long applcntId) {
        List<PIPOrder> pipOrders = entityManager.createQuery("select porder from PIPOrder porder where porder.createdBy.userId = :userId and porder.applicant.applcntId = :applcntId ")
                .setParameter("userId", userId)
                .setParameter("applcntId", applcntId)
                .getResultList();

        initializePIPOrder(pipOrders);


        return pipOrders;
    }

    public List<PIPOrderLookUp> findAllPIPOrderLookUp(Long applicantType, boolean pip) {
        return entityManager.createQuery("select lookup from PIPOrderLookUp lookup where lookup.pip = :pip and lookup.applicantType.id = :applicantType")
                .setParameter("pip", pip)
                .setParameter("applicantType", applicantType)
                .getResultList();
    }


    public List<PIPOrder> findAllPIPOrder() {
        List<PIPOrder> pipOrders = entityManager.createQuery("select porder from PIPOrder porder")
                .getResultList();
        initializePIPOrder(pipOrders);
        return pipOrders;
    }

    private void initializePIPOrder(List<PIPOrder> pipOrders) {
        for (PIPOrder pipOrder : pipOrders) {
            Hibernate.initialize(pipOrder.getPipProds());
        }
    }

}
