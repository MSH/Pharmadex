package org.msh.pharmadex.dao;

import org.msh.pharmadex.dao.iface.CustomAtcDao;
import org.msh.pharmadex.domain.Atc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Author: usrivastava
 */
public class CustomAtcDaoImpl implements CustomAtcDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Atc> findAtcByProduct(Long prodId) {
        return entityManager.createQuery("select p.atcs from Product p join Atc a where p = :prodId")
                .setParameter("prodId", prodId)
                .getResultList();
    }

}
