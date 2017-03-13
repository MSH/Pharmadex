package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.msh.pharmadex.mbean.product.ReviewItemReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class CustomReviewDAO implements Serializable {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private ReviewQDAO reviewQDAO;
	/**
	 * Method to fetch review info when the workspace configuration is set for detail review
	 * @param reviewID
	 * @return
	 */
	public List<ReviewInfoTable> findReviewInfoByReview(Long reviewID) {
		List<Object[]> ris = entityManager.createNativeQuery("select ri.id, " +
				"CASE " +
				"        WHEN ri.reviewer_id = :reviewID THEN 'PRIMARY' " +
				"        ELSE 'SECONDARY' " +
				"    END AS rev_type, " +
				"    ri.reviewStatus, ri.assignDate, ri.submitDate, ri.ctdModule, ri.dueDate, ri.recomendType, "
				+ "p.prod_name, pa.sra, pa.fastrack, pa.id, pa.regState, ri.sec_reviewer_id, ri.secreview " +
				"from review_info ri, prodapplications pa, product p " +
				"where ri.prod_app_id = pa.id " +
				"and pa.PROD_ID = p.id " +
				"and (ri.reviewer_id = :reviewID or ri.sec_reviewer_id = :reviewID )")
				.setParameter("reviewID", reviewID)
				.getResultList();

		List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();
		ReviewInfoTable reviewInfoTable = null;
		for (Object[] objArr : ris) {
			reviewInfoTable = new ReviewInfoTable();
			reviewInfoTable.setId(Long.valueOf("" + objArr[0]));
			reviewInfoTable.setRevType((String) objArr[1]);
			reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[2])));
			reviewInfoTable.setAssignDate((Date) objArr[3]);
			reviewInfoTable.setSubmittedDate((Date) objArr[4]);
			reviewInfoTable.setCtdModule((String) objArr[5]);
			reviewInfoTable.setDueDate((Date) objArr[6]);
			if (objArr[7] != null)
				reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[7]));
			reviewInfoTable.setProdName((String) objArr[8]);
			reviewInfoTable.setSra((Boolean) objArr[9]);
			reviewInfoTable.setFastrack((Boolean) objArr[10]);
			reviewInfoTable.setProdAppID(((BigInteger) objArr[11]).longValue());
			String rst = (String)objArr[12];
			reviewInfoTable.setRegState(RegState.valueOf(rst));
			if(objArr[13] != null){
				reviewInfoTable.setSecReviewerId(Long.valueOf("" + objArr[13]));
			}else{
				reviewInfoTable.setSecReviewerId(new Long(0));
			}
			reviewInfoTable.setSecondary(Boolean.valueOf("" + objArr[14]));
			prodTables.add(reviewInfoTable);
		}
		return prodTables;
	}

	/**
	 * Method to fetch review info when the workspace configuration is set for detail review
	 * ProdApplication regState=REVIEW_BOARD or regState=FOLLOW_UP
	 * @param reviewID
	 * @return
	 */
	public List<ReviewInfoTable> findReviewInfoByReviewer(Long reviewerID) {
		List<Object[]> ris = entityManager.createNativeQuery("select ri.id, " +
				"CASE " +
				"        WHEN ri.reviewer_id = :reviewID THEN 'PRIMARY' " +
				"        ELSE 'SECONDARY' " +
				"    END AS rev_type, " 
				+ " ri.reviewStatus, ri.assignDate, ri.submitDate, ri.ctdModule, ri.dueDate, ri.recomendType, "
				+ " p.prod_name, pa.sra, pa.fastrack, pa.id, pa.regState, ri.sec_reviewer_id, ri.secreview "
				+ " from review_info ri, prodapplications pa, product p "
				+ " where ri.prod_app_id = pa.id "
				+ " and pa.PROD_ID = p.id "
				+ " and (ri.reviewer_id = :reviewID or ri.sec_reviewer_id = :reviewID )"
				+ " and (pa.regState='" + RegState.REVIEW_BOARD.name() + "' or pa.regState='" + RegState.FOLLOW_UP.name() + "')")
				.setParameter("reviewID", reviewerID)
				.getResultList();

		List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();

		for (Object[] objArr : ris) {
			ReviewInfoTable reviewInfoTable = null;
			//very special case! Secondary can see review only in secondary stage!
			Long secID = new Long(0);
			if(objArr[13] != null)
				secID = Long.valueOf("" + objArr[13]);

			if(secID.intValue() == reviewerID.intValue()){
				boolean isSec = Boolean.valueOf("" + objArr[14]);
				ReviewStatus revSt = ReviewStatus.valueOf((String) objArr[2]);
				if(isSec && (revSt.equals(ReviewStatus.SEC_REVIEW) || revSt.equals(ReviewStatus.FEEDBACK)))
					reviewInfoTable = createItemReviewInfoTable(objArr);
			}else
				reviewInfoTable = createItemReviewInfoTable(objArr);

			if(reviewInfoTable != null)
				prodTables.add(reviewInfoTable);
		}

		return prodTables;
	}

	private ReviewInfoTable createItemReviewInfoTable(Object[] objArr){
		ReviewInfoTable reviewInfoTable = new ReviewInfoTable();
		reviewInfoTable.setId(Long.valueOf("" + objArr[0]));
		reviewInfoTable.setRevType((String) objArr[1]);
		reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[2])));
		reviewInfoTable.setAssignDate((Date) objArr[3]);
		reviewInfoTable.setSubmittedDate((Date) objArr[4]);
		reviewInfoTable.setCtdModule((String) objArr[5]);
		reviewInfoTable.setDueDate((Date) objArr[6]);
		if (objArr[7] != null)
			reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[7]));
		reviewInfoTable.setProdName((String) objArr[8]);
		reviewInfoTable.setSra((Boolean) objArr[9]);
		reviewInfoTable.setFastrack((Boolean) objArr[10]);
		reviewInfoTable.setProdAppID(((BigInteger) objArr[11]).longValue());
		String rst = (String)objArr[12];
		reviewInfoTable.setRegState(RegState.valueOf(rst));
		if(objArr[13] != null){
			reviewInfoTable.setSecReviewerId(Long.valueOf("" + objArr[13]));
		}else{
			reviewInfoTable.setSecReviewerId(new Long(0));
		}
		reviewInfoTable.setSecondary(Boolean.valueOf("" + objArr[14]));

		return reviewInfoTable;
	}


	/**
	 * Method to fetch the review details from the Review table when the workspace configuration is for review detail false.
	 * @param reviewID
	 * @return
	 */
	public List<ReviewInfoTable> findReviewByReviewer(Long reviewID) {
		List<Object[]> ris = entityManager.createNativeQuery("select ri.id, ri.reviewStatus, ri.assignDate, ri.submitDate, ri.dueDate, " +
				"ri.recomendType,p.prod_name, pa.sra, pa.fastrack, pa.id, pa.regState " +
				"                from review ri, prodapplications pa, product p " +
				"                where ri.prod_app_id = pa.id " +
				"                and pa.PROD_ID = p.id " +
				"and ri.user_id = :reviewID ")
				.setParameter("reviewID", reviewID)
				.getResultList();

		List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();
		ReviewInfoTable reviewInfoTable = null;
		for (Object[] objArr : ris) {
			reviewInfoTable = new ReviewInfoTable();
			reviewInfoTable.setId(Long.valueOf("" + objArr[0]));
			reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[1])));
			reviewInfoTable.setAssignDate((Date) objArr[2]);
			reviewInfoTable.setSubmittedDate((Date) objArr[3]);
			reviewInfoTable.setDueDate((Date) objArr[4]);
			if (objArr[5] != null)
				reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[5]));
			reviewInfoTable.setProdName((String) objArr[6]);
			reviewInfoTable.setSra((Boolean) objArr[7]);
			reviewInfoTable.setFastrack((Boolean) objArr[8]);
			reviewInfoTable.setProdAppID(((BigInteger) objArr[9]).longValue());

			String rst = (String)objArr[10];
			reviewInfoTable.setRegState(RegState.valueOf(rst));
			prodTables.add(reviewInfoTable);
		}
		return prodTables;
	}

	public JasperPrint getReviewReport(Long id) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			//        Session hibernateSession = entityManager.unwrap(Session.class);
			Connection conn = entityManager.unwrap(Session.class).connection();

			HashMap param = new HashMap();
			param.put("reviewInfoID", id);

			URL resource = getClass().getResource("/reports/review_detail_report.jasper");
			jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
			conn.close();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return jasperPrint;

	}

	/**
	 * Fetches review info by primary and seconday reviewer
	 *
	 * @return
	 */
	public List<ReviewInfoTable> findAllPriSecReview() {
		List<Object[]> ris = entityManager.createNativeQuery("select * \n" +
				"from ((select u.name, p.prod_name, ri.reviewStatus, 'PRIMARY' as revType\n" +
				",ri.assignDate, ri.dueDate, ri.submitDate, pa.prodAppType, pa.prodAppNo, ri.id, ri.recomendType, ri.ctdModule, pa.id as prodAppID, pa.fastrack, pa.sra " +
				"from review_info ri, prodapplications pa, product p, user u\n" +
				"where ri.prod_app_id = pa.id\n" +
				"and pa.PROD_ID = p.id\n" +
				"and ri.reviewer_id is not null\n" +
				"and u.userid = ri.reviewer_id\n" +
				")\n" +
				"union \n" +
				"(select u.name, p.prod_name, ri.reviewStatus, 'SECONDARY' as revType\n" +
				",ri.assignDate, ri.dueDate, ri.submitDate, pa.prodAppType, pa.prodAppNo, ri.id, ri.recomendType, ri.ctdModule, pa.id  as prodAppID, pa.fastrack, pa.sra " +
				"from review_info ri, prodapplications pa, product p, user u\n" +
				"where ri.prod_app_id = pa.id\n" +
				"and pa.PROD_ID = p.id\n" +
				"and ri.sec_reviewer_id is not null\n" +
				"and u.userid = ri.sec_reviewer_id)) review\n" +
				"where reviewStatus <> 'ACCEPTED'\n" +
				"order by name, prod_name;")
				.getResultList();

		List<ReviewInfoTable> prodTables = new ArrayList<ReviewInfoTable>();
		ReviewInfoTable reviewInfoTable = null;
		for (Object[] objArr : ris) {
			reviewInfoTable = new ReviewInfoTable();
			reviewInfoTable.setRevName((String) objArr[0]);
			reviewInfoTable.setProdName((String) objArr[1]);
			reviewInfoTable.setReviewStatus((ReviewStatus.valueOf((String) objArr[2])));
			reviewInfoTable.setRevType((String) objArr[3]);
			reviewInfoTable.setAssignDate((Date) objArr[4]);
			reviewInfoTable.setDueDate((Date) objArr[5]);
			reviewInfoTable.setSubmittedDate((Date) objArr[6]);
			reviewInfoTable.setProdAppType((ProdAppType.valueOf((String) objArr[7])));
			reviewInfoTable.setProdAppNo((String) objArr[8]);
			reviewInfoTable.setId(Long.valueOf("" + objArr[9]));
			if (objArr[10] != null)
				reviewInfoTable.setRecomendType(RecomendType.valueOf((String) objArr[10]));
			reviewInfoTable.setCtdModule((String) objArr[11]);
			reviewInfoTable.setProdAppID(((BigInteger) objArr[12]).longValue());
			prodTables.add(reviewInfoTable);
		}
		return prodTables;
	}

	public Map<String, List<ReviewItemReport>> getReviewListByReportNew(Long prodAppId) {
		List<ReviewItemReport> list = null;
		final Map<String, Long> mapHeaderIds = new HashMap<String, Long>();
		List<ReviewInfo> listInfo = entityManager.createQuery("select ri from ReviewInfo ri "
				+ " where ri.prodApplications.id=:prodAppId")
				.setParameter("prodAppId", prodAppId)
				.getResultList();
		if(listInfo != null && listInfo.size() > 0){
			for(ReviewInfo info:listInfo){
				List<ReviewDetail> listDetail = reviewQDAO.findReviewSummary(info.getReviewer().getUserId(), info.getId());
				/*List<ReviewDetail> listDetail = entityManager
						.createQuery("select rd from ReviewDetail rd where rd.reviewInfo.id=:revinfoId "
								+ 
								"order by rd.reviewQuestions.id")
						.setParameter("revinfoId", info.getId())
						.getResultList();*/
				if(listDetail != null && listDetail.size() > 0){
					
					String firstReviewer = info.getReviewer().getName();
					String secondReviewer = info.getSecReviewer() != null ? info.getSecReviewer().getName() : null;
					for(ReviewDetail det:listDetail){
						String answ = det.getAnswer()!=null?det.getAnswer()+"":"";
						if(!answ.equals("NA")){
							ReviewItemReport item = new ReviewItemReport();
							if(det.getOtherComment() != null && !det.getOtherComment().trim().equals("")){
								item.setFirstRevName(firstReviewer);
								item.setFirstRevComment(det.getOtherComment());
							}
							if(secondReviewer != null && det.getSecComment() != null && !det.getSecComment().trim().equals("")){
								item.setSecondRevName(secondReviewer);
								item.setSecondRevComment(det.getSecComment());
							}
	
							item.setHeader1(det.getReviewQuestions().getHeader1());
							item.setHeader2(det.getReviewQuestions().getHeader2());
							item.setDetailId(det.getId());
							item.setQuestionId(det.getReviewQuestions().getId());
							item.setReviewQuestion(det.getReviewQuestions().getQuestion());
							item.setPages(det.getVolume());
							item.setNo_reason(det.getNoReason());
							
							if(answ.equals("NO")){
								item.setAnswer("NO");
							}else if(answ.equals("YES")){
								item.setAnswer("YES");
							}							
							if(det.getFile() != null){
								// only pictures
								if(det.getFilename() != null && !det.getFilename().equals("")){
									String fname = det.getFilename();
									fname = fname.toLowerCase(); //AK because of clipping tool :) 20161220
									if(fname.endsWith(".png") || fname.endsWith(".bmp")
											|| fname.endsWith(".jpeg") || fname.endsWith(".jpg"))
										item.setFile(det.getFile());
								}
							}	
							if(list == null)
								list = new ArrayList<ReviewItemReport>();
							list.add(item);
	
							mapHeaderIds.put(det.getReviewQuestions().getHeader1(), det.getReviewQuestions().getId());					 
					  }//
					
					}
				 
				}
			}
		}

		Map<String, List<ReviewItemReport>> resMap = new TreeMap<String, List<ReviewItemReport>>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Long id1 = mapHeaderIds.get(o1);
				Long id2 = mapHeaderIds.get(o2);
				return id1.compareTo(id2);
			}
		});
		if(list != null && list.size() > 0){
			for(ReviewItemReport it:list){
				String h = it.getHeader1();
				List<ReviewItemReport> vals = resMap.get(h);
				if(vals == null)
					vals = new ArrayList<ReviewItemReport>();
				vals.add(it);
				resMap.put(h, vals);
			}
		}

		return resMap;
	}
}

