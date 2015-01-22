package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.FeeSchedule;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Repository
public class CustomFeeSchDAO implements Serializable {

    @PersistenceContext
    EntityManager entityManager;

    public List<FeeSchedule> findByStartAndEndDate(Date currDate) {
        return entityManager.createQuery("from FeeSchedule f where f.startDate < :currDate and f.endDate > :currDate")
                .setParameter("currDate", currDate)
                .getResultList();
    }

}
