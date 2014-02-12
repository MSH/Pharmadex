package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
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
@Repository("applicantDAO")
@Transactional
public class ApplicantDAO implements Serializable {

    private static final long serialVersionUID = -4410852928737926281L;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public Applicant findApplicant(long id) {
        return entityManager.find(Applicant.class, id);
    }

    @Transactional(readOnly = true)
    public List<Applicant> findAllApplicants() {
        return entityManager.createQuery(" select a from Applicant a ").getResultList();
    }

    @Transactional(readOnly = true)
    public List<Applicant> findRegApplicants() {
        return (List<Applicant>) entityManager.createQuery("select a from Applicant a where a.state = :state")
                .setParameter("state", ApplicantState.REGISTERED).getResultList();
    }

    @Transactional(readOnly = true)
    public Applicant findApplicantByProduct(Long id) {
        return (Applicant) entityManager.createQuery("select a from Applicant a join a.products p " +
                "where p.id = :prodId")
                .setParameter("prodId", id).getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<Applicant> findPendingApplicant() {
        return (List<Applicant>) entityManager.createQuery("select a from Applicant a where a.state = :state ")
                .setParameter("state", ApplicantState.NEW_APPLICATION).getResultList();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Applicant saveApplicant(Applicant applicant) {
        for (User u : applicant.getUsers()) {
            u.setApplicant(applicant);
            entityManager.merge(u);
        }
        Applicant a = entityManager.merge(applicant);
        entityManager.flush();
        return a;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateApplicant(Applicant applicant) {
        entityManager.merge(applicant);
        entityManager.flush();
        return "updated";
    }


    public User findApplicantDefaultUser(Long applcntId) {
        return (User) entityManager.createQuery("select u from User u where u.applicant.applcntId = :appID")
                .setParameter("appID", applcntId)
                .getSingleResult();
    }
}

