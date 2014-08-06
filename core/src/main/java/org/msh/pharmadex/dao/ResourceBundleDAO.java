package org.msh.pharmadex.dao;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ResourceBundle;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("resourceBundleDAO")
@Transactional
public class ResourceBundleDAO implements Serializable {

    @PersistenceContext
    EntityManager entityManager;

    public ResourceBundle findResourceBundleByLocale(Locale locale){
        return (ResourceBundle) entityManager.createNamedQuery(ResourceBundle.QUERYNAME_FIND_BY_LOCALE).setParameter("locale", ""+locale).getSingleResult();
    }

    public void save(ResourceBundle resourceBundle){
        entityManager.persist(resourceBundle);
    }
}

