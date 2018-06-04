package org.msh.pharmadex.dao;
import org.msh.pharmadex.domain.*;
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
            return null;
        }
    }
    public DosageForm findDosFormByName(String name){
        try {
            String query = "select d from DosageForm d where d.dosForm = :dName";
            return (DosageForm) entityManager.createQuery(query)
                    .setParameter("dName", name).getSingleResult();
        }  catch (Exception ex) {
            return null;
        }
    }
    public Atc findAtsbyCode(String name){
    try{
        return (Atc) entityManager.createQuery("select a from Atc a where a.atcCode = :aName ")
                .setParameter("aName", name).getSingleResult();
    } catch (Exception ex) {
        return null;
    }
    }
    public PharmClassif findPharmSlassifByName(String name){
        try{
            return (PharmClassif) entityManager.createQuery("select f from PharmClassif f where f.name = :aName ")
                    .setParameter("aName", name).getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }


public DosUom findDosUomByName(String res) {
    try{
        return (DosUom) entityManager.createQuery("select u from DosUom u where u.uom = :name ")
                .setParameter("name", res).getSingleResult();
    } catch (Exception ex) {
        return null;
    }

    }
    public Applicant findApplicantByName(String res ) {
        try{
         List<Applicant> ap= (List<Applicant>)entityManager.createQuery("select a from Applicant a where a.appName like :name ")
                .setParameter("name", res).getResultList();
            return  ap.get(0);
      } catch (Exception ex) {
                   return null;
      }
    }

    public Company findCompanyByName(String res ) {
        try{
            List<Company> ap= (List<Company>)entityManager.createQuery("select c from Company c where c.companyName like :name ")
                    .setParameter("name", res).getResultList();
            return  ap.get(0);
        } catch (Exception ex) {
            return null;
        }
    }

}