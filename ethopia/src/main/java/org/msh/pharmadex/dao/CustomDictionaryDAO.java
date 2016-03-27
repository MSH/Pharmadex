package org.msh.pharmadex.dao;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.AdminRoute;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.PharmClassif;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 26.03.2016.
 */
@Repository

public class CustomDictionaryDAO {
    @PersistenceContext
    EntityManager entityManager;

    public AdminRoute findAdminRouteByName(String name) {
        try {
            return (AdminRoute) entityManager.createQuery("select a from AdminRoute a where a.name = :aName ")
                    .setParameter("aName", name).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public DosageForm findDosFormByName(String name){
        try {
            return (DosageForm) entityManager.createQuery("select d from DosageForm d where d.dosForm = :dName ")
                    .setParameter("dName", name).getSingleResult();
        }  catch (Exception ex) {
        ex.printStackTrace();
        return null;
    }
    }
    public Atc findAtsbyCode(String name){
    try{
        return (Atc) entityManager.createQuery("select a from Atc a where a.atcCode = :aName ")
                .setParameter("aName", name).getSingleResult();
    } catch (Exception ex) {
        ex.printStackTrace();
        return null;
    }
    }
    public PharmClassif findPharmSlassifByName(String name){
        try{
            return (PharmClassif) entityManager.createQuery("select f from PharmClassif f where f.name = :aName ")
                    .setParameter("aName", name).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
