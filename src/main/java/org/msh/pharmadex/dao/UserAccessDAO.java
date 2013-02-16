package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.UserAccess;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * User: usrivastava
 */
@Repository
@Transactional
public class UserAccessDAO implements Serializable{

    private static final long serialVersionUID = -3110111857022043784L;
    @PersistenceContext
    EntityManager entityManager;

    public UserAccess findById(long id){
        return entityManager.find(UserAccess.class, id);
    }

    public List<UserAccess> allUserAccess(){
        return (List<UserAccess>)entityManager.createQuery("select ua from UserAccess ua").getResultList();
    }

    public String saveUserAcess(UserAccess userAccess){
        entityManager.persist(userAccess);
        return "persisted";
    }

    public String updateUserAccess(UserAccess userAccess){
        entityManager.merge(userAccess);
        entityManager.flush();
        return "updated";
    }
}

