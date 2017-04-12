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
import javax.persistence.Query;

import java.io.Serializable;
import java.math.BigInteger;
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
		//        if (applicant.getAddress().getCountry()!=null)
		//            applicant.getAddress().setCountry(countryDAO.find(applicant.getAddress().getCountry().getId()));
		List<User> list = applicant.getUsers();
		if(list != null && list.size() > 0){
			for(User u:list){
				u.setApplicant(applicant);
			}
		}

		Applicant a = entityManager.merge(applicant);
		entityManager.flush();
		return a;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Applicant updateApplicant(Applicant applicant) {
		if(applicant != null){
			if(applicant.getAddress()!= null 
					&& applicant.getAddress().getCountry()!=null 
					&& applicant.getAddress().getCountry().getId()!=null 
					&& applicant.getAddress().getCountry().getId()>0){
				applicant.getAddress().setCountry(countryDAO.find(applicant.getAddress().getCountry().getId()));
			}else{
				applicant.getAddress().setCountry(null);
			}
			List<User> list = applicant.getUsers();
			if(list != null && list.size() > 0){
				for(User u:list){
					u.setApplicant(applicant);
					//entityManager.merge(u);
				}
			}
			Applicant a = entityManager.merge(applicant);
			return a;
		}else{
			return null;
		}
	}

	@Transactional
	public Applicant updateApplicantResp(Applicant applicant) {
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

	/**
	 * Get applicants for display in lists
	 * @param registered only registered or only non registered
	 * @return
	 */
	public List<ApplicantListDTO> getApplicantsForLists(boolean registered){
		List<ApplicantListDTO> ret = new ArrayList<ApplicantListDTO>();
		String querySQL="SELECT app.applcntId, app.appName,atype.name as 'appType', agentApp.appName as 'agentName'," 
				+"user1.name as 'nativeResp',"
				+"user1.email as 'nativeEmail',"
				+"user1.phoneNo as 'nativePhone',"
				+"user2.name as 'agentResp',"
				+"user2.email as 'agentEmail',"
				+"user2.phoneNo as 'agentPhone' "
				+"FROM pdx_na.applicant app "
				+"left join pdx_na.applicant_type atype on app.applicantType_id=atype.id "
				+"left join pdx_na.user user1 on app.contactName= user1.username "
				+"left join pdx_na.agent_applicant agent on agent.applicant_id=app.applcntId and agent.active "
				+"left join pdx_na.applicant agentApp on agent.agent_id=agentApp.applcntId "
				+"left join pdx_na.user user2 on agentApp.contactName=user2.username "
				+"where app.state=?";
		Query query = entityManager.createNativeQuery(querySQL);
		if(registered){
			query.setParameter(1, "REGISTERED");
		}else{
			query.setParameter(1, "NEW_APPLICATION");
		}
		List<Object[]> result = query.getResultList();
		if(result != null){
			for(Object[] line : result){
				ApplicantListDTO dto = new ApplicantListDTO();
				dto.setApplcntId(((BigInteger)line[0]).longValue());
				dto.setName((String) line[1]);
				dto.setAppType((String) line[2]);
				if(line[3] != null){
					//show agent
					dto.setAgentName((String) line[3]);
					dto.setContactName((String) line[7]);
					dto.setContactEmail((String) line[8]);
					dto.setContactPhone((String) line[9]);
				}else{
					//show native
					dto.setAgentName("");
					dto.setContactName((String) line[4]);
					dto.setContactEmail((String) line[5]);
					dto.setContactPhone((String) line[6]);
				}
				ret.add(dto);
			}
		}
		return ret;
	}
}

