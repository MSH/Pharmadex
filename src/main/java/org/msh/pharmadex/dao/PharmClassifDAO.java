package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.PharmClassif;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class PharmClassifDAO implements Serializable{

    private static final long serialVersionUID = 6673819035361956468L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public List<PharmClassif> allCountry(){
        return entityManager.createQuery(" from PharmClassif p ").getResultList();
    }

    @Transactional
    public PharmClassif find(long id){
        return entityManager.find(PharmClassif.class, id);
    }

}

