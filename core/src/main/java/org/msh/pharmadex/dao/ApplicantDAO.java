package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("applicantDAO")
@Transactional
public class ApplicantDAO implements Serializable {

    private static final long serialVersionUID = -4410852928737926281L;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CountryDAO countryDAO;

    @Transactional
    public Applicant findApplicant(long id) {
    	if(id <= 0)
    		return null;
    	
        Applicant applicant = (Applicant) entityManager.createQuery("select a from Applicant a fetch all properties where a.applcntId = :id ")
                .setParameter("id", id)
                .getSingleResult();

        return applicant;
    }

    @Transactional(readOnly = true)
    public List<Applicant> findAllApplicants() {
    	return entityManager.createQuery(" select a from Applicant a left join fetch a.address.country c left join fetch a.applicantType apptype order by a.appName ").getResultList();
    }

    @Transactional(readOnly = true)
    public List<Applicant> findRegApplicants() {
        return (List<Applicant>) entityManager.createQuery("from Applicant a left join fetch a.address.country c left join fetch a.applicantType apptype where a.state = :state order by a.appName ")
                .setParameter("state", ApplicantState.REGISTERED)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Applicant findApplicantByProduct(Long id) {
        return (Applicant) entityManager.createQuery("select a from Applicant a left join fetch a.address.country c left join fetch a.applicantType apptype left join a.products p " +
                "where p.id = :prodId")
                .setParameter("prodId", id).getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<Applicant> findPendingApplicant() {
        return (List<Applicant>) entityManager.createQuery("select a from Applicant a left join fetch a.address.country c left join fetch a.applicantType apptype where a.state = :state ")
                .setParameter("state", ApplicantState.NEW_APPLICATION).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Applicant> findApplicantsNotRegistered() {
        return (List<Applicant>) entityManager.createQuery("select a from Applicant a left join fetch a.address.country c left join fetch a.applicantType apptype where a.state != :state ")
                .setParameter("state", ApplicantState.REGISTERED).getResultList();
    }
    
    @Transactional
    public Applicant saveApplicant(Applicant applicant) {
//        for (User u : applicant.getUsers()) {
//            u.setApplicant(applicant);
//            entityManager.merge(u);
//        }

        Applicant a = entityManager.merge(applicant);
        entityManager.flush();
        return a;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Applicant updateApplicant(Applicant applicant) {
        applicant.getAddress().setCountry(countryDAO.find(applicant.getAddress().getCountry().getId()));
        Applicant a = entityManager.merge(applicant);
        return a;
    }


    public User findApplicantDefaultUser(Long applcntId) {
        return (User) entityManager.createQuery("select u from User u where u.applicant.applcntId = :appID")
                .setParameter("appID", applcntId)
                .getSingleResult();
    }

    public boolean isUsernameDuplicated(String applicantName) {
        applicantName = applicantName.trim();
        Long i = (Long) entityManager.createQuery("select count(applcntId) from Applicant a where upper(a.appName) = upper(:applicantName)")
                .setParameter("applicantName", applicantName)
                .getSingleResult();
        if (i > 0)
            return true;
        else
            return false;
    }
}

