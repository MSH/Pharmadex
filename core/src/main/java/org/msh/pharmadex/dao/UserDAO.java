package org.msh.pharmadex.dao;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;
import java.util.ArrayList;
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
public class UserDAO implements Serializable {
    private static final long serialVersionUID = -3030011694490082788L;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Autowired
    CountryDAO countryDAO;
    
    @Autowired
    ApplicantDAO applicantDAO;

    @Transactional
    public User findUser(Long id) {

        User user = (User) entityManager.createQuery("select u from User u where u.userId = :userid")
                .setParameter("userid", id)
                .getSingleResult();
        Hibernate.initialize(user.getRoles());
        Hibernate.initialize(user.getAddress().getCountry());
        return user;
    }

    @Transactional
    public List<User> allUsers() {
        List<Object> objs = entityManager.createNativeQuery("select u.userId, u.name, u.username, u.email, u.type, u.enabled, a.appName " +
                "from user u left join applicant a ON a.applcntId = u.applcntId;").getResultList();
        ArrayList<User> users = new ArrayList<User>();
        User user;
        for (Object obj : objs) {
            Object[] o = (Object[]) obj;
            user = new User();
            user.setUserId(Long.valueOf("" + o[0]));
            user.setName("" + o[1]);
            user.setUsername("" + o[2]);
            user.setEmail("" + o[3]);
            if(o[4] != null)
            	user.setType(UserType.valueOf((String) o[4]));
            user.setEnabled((Boolean) o[5]);
            String companyName = (String) o[6];
            user.setCompanyName(companyName);
            users.add(user);
        }
        return users;
    }

    @Transactional
    public List<User> findNotRegistered() {
        return entityManager.createQuery("select u from User u where u.applicant is null and u.type = :userType")
                .setParameter("userType", UserType.COMPANY)
                .getResultList();
    }

    @Transactional
    public List<User> findByApplicant(Long id) {
        List<User> u = entityManager.createQuery("select u from User u where u.applicant.applcntId = :applicantId ")
                .setParameter("applicantId", id)
                .getResultList();
        Hibernate.initialize(u);
        return u;
    }

    @Transactional
    public List<User> findByRxSite(Long id) {
        return entityManager.createQuery("select u from User u join u.pharmacySites ps where ps.id = :siteId ")
                .setParameter("siteId", id)
                .getResultList();
    }

    public User findByUsername(String username) throws NoResultException {
        try {
            List<User> u =  entityManager.createQuery("select u from User u fetch all properties  where u.username = :username")
                    .setParameter("username", username)
                    .getResultList();
            if(u.size()>1){
                throw new Exception("Multiple Users with same username");
            }
            if(u.size()==0){
                throw new Exception("User doesnt exist");
            }
            return u.get(0);

        } catch (NoResultException noe) {
            return null;

        } catch (Exception e) {
            e.printStackTrace();
//            throw new NoResultException("User doesnt exist");

        }
        return null;
    }

    @Transactional
    public String saveUser(User user) {
        user.setRegistrationDate(new Date());
//        user.setEnabled(true);

//        try {
//            User u = findByUsernameOrEmail(user);
//            if (u != null) {
//                if (u.getEmail().equalsIgnoreCase(user.getEmail()))
//                    return "There already exist a user with the same Email address. If you have forgotten your password or email address please click on the forgot password link";
//                else if (u.getUsername().equalsIgnoreCase(user.getUsername()))
//                    return "A user with the same username already exist in the database. If you have forgotten your password or email address please click on the forgot password link";
//            }
//        } catch (NoResultException no) {
        entityManager.persist(user);
        return "persisted";
//        }
//        return "";
    }

    @Transactional
    public User updateUser(User user) {
        user.getAddress().setCountry(countryDAO.find(user.getAddress().getCountry().getId()));	 
        if(user.getApplicant() != null && user.getApplicant().getApplcntId() != 0){
        	user.setApplicant(applicantDAO.findApplicant(user.getApplicant().getApplcntId()));
        	user.setCompanyName(user.getApplicant().getAppName());
        }
        user = entityManager.merge(user);
        return user;
    }

    public User findByUsernameOrEmail(User u) throws NoResultException {
        return (User) entityManager.createQuery("select u from User u left join fetch u.roles r left join fetch u.applicant a where u.username = :username or u.email = :email ")
                .setParameter("username", u.getUsername())
                .setParameter("email", u.getEmail()).getSingleResult();
    }

    public List<User> findProcessors() {
        return entityManager.createQuery("select u from User u left join u.roles r where r.roleId = :roleId")
                .setParameter("roleId", 7)
                .getResultList();  //To change body of created methods use File | Settings | File Templates.
    }

    public List<User> findModerators() {
        return entityManager.createQuery("select u from User u left join u.roles r where r.roleId = :roleId")
                .setParameter("roleId", 6)
                .getResultList();  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isUsernameDuplicated(String username) {
        username = username.trim();
        Long i = (Long) entityManager.createQuery("select count(userId) from User u where upper(u.username) = upper(:username)")
                .setParameter("username", username)
                .getSingleResult();
        if (i > 0)
            return true;
        else
            return false;
    }

    public boolean isEmailDuplicated(String email) {
        email = email.trim();
        Long i = (Long) entityManager.createQuery("select count(userId) from User u where upper(u.email) = upper(:email)")
                .setParameter("email", email)
                .getSingleResult();
        if (i > 0)
            return true;
        else
            return false;
    }
}
