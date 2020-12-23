package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.msh.pharmadex.service.SubmittedAppDTO;
import org.msh.pharmadex.service.UsersPerformanceDTO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 * All data for dashboards
 * @author Alex Kurasoff
 *
 */
@Repository
@Transactional
public class DashboardDAO implements Serializable {
	private static final long serialVersionUID = 5528271667610075149L;
	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Fetch all submitted applications
	 * @return empty list if not found or full list of...
	 */
	public List<SubmittedAppDTO> fetchSubmittedQuart() {
		List<SubmittedAppDTO> items = new ArrayList<SubmittedAppDTO>();
		String sql = "SELECT year(tl.statusDate) as 'y', quarter(tl.statusDate) as 'q', ap.appName as 'applicant', ap.applcntId as appId, count(pa.id) as 'applications'"
				+submittedSQL()
				+" group by y desc, q desc, ap.appName;";
		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null){
			for(Object[] objArr: list){
				items.add(new SubmittedAppDTO((Integer)objArr[0], (Integer)objArr[1],-1, (String)objArr[2], ((BigInteger)objArr[3]).intValue(),((BigInteger)objArr[4]).intValue()));
			}
		}
		return items;
	}

	/**
	 * Common SQL part to fetch submitted apps
	 * @return
	 */
	private String submittedSQL(){
		return " FROM prodapplications pa"
				+" join applicant ap on ap.applcntId = pa.APP_ID"
				+" join timeline tl on tl.PROD_APP_ID=pa.id and tl.regState='FEE'";
	}
	/**
	 * Fetch submitted by year and month
	 * @param year
	 * @param applicantId 
	 * @param month
	 * @return
	 */
	public List<SubmittedAppDTO> fetchSubmittedMonth(Integer year, Integer quart, Integer applicantId) {
		List<SubmittedAppDTO> items = new ArrayList<SubmittedAppDTO>();
		String sql = "SELECT year(tl.statusDate) as 'y', quarter(tl.statusDate) as 'q', month(tl.statusDate) as 'm',"
				+" ap.appName as 'applicant', ap.applcntId as appId, count(pa.id) as 'applications'"
				+submittedSQL()
				+" where ap.applcntId=:applicantId"
				+" group by y desc, m desc, ap.appName"
				+" having y='" + year +"' and q='" + quart + "';";
		@SuppressWarnings("unchecked")
		List<Object[]> list = entityManager.createNativeQuery(sql).setParameter("applicantId", applicantId).getResultList();
		if(list != null){
			for(Object[] objArr: list){
				items.add(new SubmittedAppDTO((Integer)objArr[0], (Integer)objArr[1],(Integer)objArr[2], (String)objArr[3], ((BigInteger)objArr[4]).intValue(),((BigInteger)objArr[5]).intValue()));
			}
		}
		return items;
	}

	public List<SubmittedAppDTO> fetchSubmittedStates(Integer year, Integer quart, Integer appId) {
		List<SubmittedAppDTO> items = new ArrayList<SubmittedAppDTO>();
		String sql = "SELECT year(tl.statusDate) as 'y', quarter(tl.statusDate) as 'q', month(tl.statusDate) as 'm',"
				+" ap.appName as 'applicant', ap.applcntId as appId, count(pa.id) as 'applications', pa.regState"
				+submittedSQL()
				+" where ap.applcntId=:applicantId"
				+" group by y desc, m desc, pa.regState, ap.appName"
				+" having y='" + year +"' and q='" + quart + "';";
		@SuppressWarnings("unchecked")
		List<Object[]> list = entityManager.createNativeQuery(sql).setParameter("applicantId", appId).getResultList();
		if(list != null){
			for(Object[] objArr: list){
				SubmittedAppDTO dto = new SubmittedAppDTO((Integer)objArr[0], (Integer)objArr[1],(Integer)objArr[2], (String)objArr[3], ((BigInteger)objArr[4]).intValue(),((BigInteger)objArr[5]).intValue());
				dto.setRegState((String) objArr[6]);
				items.add(dto);
			}
		}
		return items;
	}
	/**
	 * FEtch first level reviewers performance report from the database
	 * @return
	 */
	public List<UsersPerformanceDTO> fetchReviewers() {
		List<UsersPerformanceDTO> items = new ArrayList<UsersPerformanceDTO>();
		String sql= "SELECT u.name, u.userId, sum(ri.reviewer_id=u.userId) as pri, sum(ri.sec_reviewer_id=u.userId) as sec FROM user u "
				+"join review_info ri on ri.reviewer_id=u.userId or ri.sec_reviewer_id=u.userId "
				+"join prodapplications pa on pa.id=ri.prod_app_id " 
				+"where pa.regState in ('FOLLOW_UP', 'REVIEW_BOARD', 'RECOMMENDED', 'NOT_RECOMMENDED') "
				+"group by u.userId;";
		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null){
			for(Object[] objArr: list){
				UsersPerformanceDTO dto = new UsersPerformanceDTO((String)objArr[0], ((BigInteger)objArr[1]), ((BigDecimal)objArr[2]).intValue(), ((BigDecimal)objArr[3]).intValue());
				items.add(dto);
			}
		}
		return items;
	}

	/**
	 * Load current review details for a reviewer given
	 * @param userId reviewer
	 * @param primaryNeed only primary or only secondary
	 * @return
	 */
	public List<UsersPerformanceDTO> loadReviewerDetails(BigInteger userId, boolean primaryNeed) {
		List<UsersPerformanceDTO> items = new ArrayList<UsersPerformanceDTO>();
		String where = "where ri.reviewer_id=:reviewerId and ";
		if(!primaryNeed){
			where = "where ri.sec_reviewer_id=:reviewerId and ";
		}
		String sql = "SELECT ri.id, ri.createdDate, ri.ctdModule, ri.reviewStatus, ap.appName, pr.prod_name, um.name as moder, DATEDIFF(now(),ri.createdDate) FROM review_info ri "
				+"join prodapplications pa on pa.id=ri.prod_app_id "
				+"join applicant ap on pa.APP_ID=ap.applcntId "
				+"join product pr on pa.PROD_ID=pr.id "
				+"join user um on um.userId=pa.MODERATOR_ID "
				+where
				+"pa.regState in ('FOLLOW_UP', 'REVIEW_BOARD', 'RECOMMENDED', 'NOT_RECOMMENDED') "
				+"order by ri.createdDate desc;";
		List<Object[]> list = entityManager.createNativeQuery(sql).setParameter("reviewerId", userId).getResultList();
		if(list != null){
			for(Object[] objArr: list){
				UsersPerformanceDTO dto = new UsersPerformanceDTO(
						(BigInteger)objArr[0],
						(Date)objArr[1],
						(String)objArr[2],
						(String)objArr[3],
						(String)objArr[4],
						(String)objArr[5],
						(String)objArr[6]
						);
				dto.setDays(((BigInteger)objArr[7]).intValue());
				items.add(dto);
			}
		}
		return items;
	}

}
