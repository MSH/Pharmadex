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

	public User findUser(Long id) {
		User user = null;
/*		List<User> list = entityManager.createQuery("select u from User u where u.userId = :userid")
				.setParameter("userid", id)
				.getResultList();
		if(list != null && list.size() > 0)
			user = list.get(0);*/
		user = entityManager.find(User.class, id);
		if(user != null){
			Hibernate.initialize(user.getRoles());
			Hibernate.initialize(user.getAddress().getCountry());
		}
		return user;
	}

	public List<User> allUsers() {
		//where not(u.applicant is null)
		List<User> users = entityManager.createQuery("select u from User u ")
				.getResultList();
		return users;
	}

	public List<User> findNotRegistered() {
		return entityManager.createQuery("select u from User u where u.applicant is null and u.type = :userType")
				.setParameter("userType", UserType.COMPANY)
				.getResultList();
	}

	public List<User> findByApplicant(Long id) {
		List<User> u = entityManager.createQuery("select u from User u where u.applicant.applcntId = :applicantId ")
				.setParameter("applicantId", id)
				.getResultList();
		Hibernate.initialize(u);
		return u;
	}

	public List<User> findByRxSite(Long id) {
		return entityManager.createQuery("select u from User u join u.pharmacySites ps where ps.id = :siteId ")
				.setParameter("siteId", id)
				.getResultList();
	}

	public User findByUsername(String username) throws NoResultException {
		try {
			List<User> u =  entityManager.createQuery("select u from User u fetch all properties where u.username = :username")
					.setParameter("username", username)
					.getResultList();
			if(u.size() > 1){
				throw new Exception("Multiple Users with same username");
			}
			if(u.size() == 0){
				//throw new Exception("User doesnt exist");
				return null;
			}
			return u.get(0);

		} catch (NoResultException noe) {
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Transactional
	public String saveUser(User user) {
		user.setRegistrationDate(new Date());
		user.getAddress().setCountry(countryDAO.find(user.getAddress().getCountry().getId()));	 
		if(user.getApplicant() != null && user.getApplicant().getApplcntId() != null && user.getApplicant().getApplcntId() > 0){
			user.setApplicant(applicantDAO.findApplicant(user.getApplicant().getApplcntId()));
			user.setCompanyName(user.getApplicant().getAppName());
		}else{
			user.setApplicant(null);
			user.setCompanyName("");
		}
		entityManager.persist(user);
		return "persisted";
	}

	@Transactional
	public User updateUser(User user) {
		user.getAddress().setCountry(countryDAO.find(user.getAddress().getCountry().getId()));	 
		if(user.getApplicant() != null && user.getApplicant().getApplcntId() != null && user.getApplicant().getApplcntId() > 0){
			user.setApplicant(applicantDAO.findApplicant(user.getApplicant().getApplcntId()));
			user.setCompanyName(user.getApplicant().getAppName());
		}else{
			user.setApplicant(null);
			user.setCompanyName("");
		}
		user = entityManager.merge(user);
		return user;
	}

	public User findByUsernameOrEmail(User u) throws NoResultException {
		List<User> list = entityManager.createQuery("select u from User u left join fetch u.roles r left join fetch u.applicant a where u.username = :username or u.email = :email ")
				.setParameter("username", u.getUsername())
				.setParameter("email", u.getEmail()).getResultList();
		if(list != null && list.size() > 0)
			return list.get(0);
		return null;
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
	
	public List<User> findScreeners() {
		return entityManager.createQuery("select u from User u left join u.roles r where r.roleId = :roleId")
				.setParameter("roleId", 3)
				.getResultList();  //To change body of created methods use File | Settings | File Templates.
	}

	public boolean isUsernameDuplicated(String username) {
		if(username == null)
			return true;
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
