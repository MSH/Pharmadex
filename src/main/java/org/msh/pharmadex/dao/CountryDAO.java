package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.Country;
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
public class CountryDAO implements Serializable{

    private static final long serialVersionUID = -3564196794362591424L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public List<Country> allCountry(){
        return entityManager.createQuery(" from Country c ").getResultList();
    }

    public Country findCountryByName(String name){
        return (Country) entityManager.createQuery("from Country c where c.countryName = :cntryName ")
                .setParameter("cntryName", name).getSingleResult();
    }

    public Country find(long id){
        return entityManager.find(Country.class, id);
    }

    @Transactional
    public Country findByCntryCD(String s) {
        return (Country) entityManager.createQuery("select c from Country c where c.countryCD = :cntryCD")
                .setParameter("cntryCD", s).getSingleResult();
    }
}

