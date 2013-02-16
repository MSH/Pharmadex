package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository("userDAO")
public class UserDAO implements Serializable{
    private static final long serialVersionUID = -3030011694490082788L;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Transactional
    public User findUser(int id){
        return entityManager.find(User.class, id);
    }

    @Transactional
    public List<User> allUsers(){
        return entityManager.createQuery("select u from User u").getResultList();
    }

    @Transactional
    public List<User> findNotRegistered(){
        return entityManager.createQuery("select u from User u where u.applicant is null ")
                .getResultList();
    }

    @Transactional
    public List<User> findByApplicant(Long id){
        return entityManager.createQuery("select u from User u where u.applicant.applcntId = :applicantId ")
                .setParameter("applicantId", id)
                .getResultList();
    }

    public User findByUsername(String username) throws NoResultException{
        System.out.println("inside findByUsername ");
        try{
            User u = (User) entityManager.createQuery("select u from User u where u.username = :username")
                            .setParameter("username", username)
                            .getSingleResult();
            return u;

        }catch (Exception e){
            e.printStackTrace();
//            throw new NoResultException("User doesnt exist");

        }
        return null;
    }

    @Transactional
    public String saveUser(User user) {
        user.setRegistrationDate(new Date());
//        user.setEnabled(true);

        try{
            User u = findByUsernameOrEmail(user);
            if(u!=null){
                if(u.getEmail().equalsIgnoreCase(user.getEmail()))
                    return "There already exist a user with the same Email address. If you have forgotten your password or email address please click on the forgot password link";
                else if(u.getUsername().equalsIgnoreCase(user.getUsername()))
                    return "A user with the same username already exist in the database. If you have forgotten your password or email address please click on the forgot password link";
            }
        }catch (NoResultException no){
            entityManager.persist(user);
            return "persisted";
        }
        return "";
    }

    @Transactional
    public String updateUser(User user) {
        entityManager.merge(user);
        entityManager.flush();
        return "persisted";
    }

    public User findByUsernameOrEmail(User u) throws NoResultException{
        return (User) entityManager.createQuery("select u from User u where u.username = :username or u.email = :email ")
                .setParameter("username", u.getUsername())
                .setParameter("email", u.getEmail()).getSingleResult();
    }
}
