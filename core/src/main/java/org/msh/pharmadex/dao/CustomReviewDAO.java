package org.msh.pharmadex.dao;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.msh.pharmadex.mbean.product.ReviewItemReport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
                "    ri.reviewStatus, ri.assignDate, ri.submitDate, ri.ctdModule, ri.dueDate, ri.recomendType, p.prod_name, pa.sra, pa.fastrack, pa.id, pa.regState " +
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
            prodTables.add(reviewInfoTable);
        }
        return prodTables;
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
    
    /**
     * Method to fetch review info when the workspace configuration is set for detail review
     * @param reviewID
     * @return
     */
    public List<ReviewItemReport> getReviewListByReport(Long prodAppId) {
    	List<ReviewItemReport> list = null;
    	/*"select quest.id, quest.header1, quest.header2, us.name,  "
    					+ "if((ri.secreview=1 and ri.sec_reviewer_id=us.userId), det.sec_comment, det.other_comment) as comm "
    					+ ", det.file "
    					+ " from review_info ri, review_detail det, review_question quest, user us "
    					+ " where ri.prod_app_id=" + prodAppId
    					+ " and ri.id=det.review_info_id "
    					+ " and (us.userId=ri.reviewer_id or us.userId=ri.sec_reviewer_id) "
    					+ " group by quest.header1, us.name, comm, det.file "
    					+ " order by quest.id"*/
    	String query = "SELECT det.id, us.name, "
    					+ "if((info.secreview=1 and info.sec_reviewer_id=us.userId), det.sec_comment,"
    					+ "det.other_comment) as comm, det.file, det.filename, quest.header1, quest.header2, quest.id "
    					+ "FROM review_info info, review_detail det, review_question quest, user us "
    					+ "where info.prod_app_id=" + prodAppId
    					+ " and info.id=det.review_info_id"
    					+ " and (us.userId=info.reviewer_id or us.userId=info.sec_reviewer_id)"
    					+ "and ("
    					+ " (not isnull(det.other_comment) and length(trim(det.other_comment)) > 0)"
    					+ " or "
    					+ " (not isnull(det.sec_comment) and length(trim(det.sec_comment)) > 0) "
    					+ " or "
    					+ " not isnull(det.file)) "
    					+ " and det.reviewquest_id=quest.id "
    					+ " group by det.id, us.`name`, det.file"
    					+ " order by det.id ";    	
    	
    	List<Object[]> values = entityManager.createNativeQuery(query).getResultList();
    	if(values != null && values.size() > 0){
    		list = new ArrayList<ReviewItemReport>();
    		for(Object[] obj:values){
    			BigInteger v = (BigInteger)obj[0];
    			Long idDet = new Long(v.longValue());
    			ReviewItemReport item = isContainsItem(list, idDet);
    			if(item == null){
    				item = new ReviewItemReport();
    				item.setDetailId(idDet);
        			item.setFirstRevName((String)obj[1]);
        			item.setFirstRevComment((String)obj[2]);
        			String fname = (String)obj[4];
        			if(fname != null && (fname.endsWith(".png") || fname.endsWith(".gif")
        					 || fname.endsWith(".jpeg") || fname.endsWith(".jpg")))
        				item.setFile((byte[])obj[3]);
        			else
        				item.setFile(null);
        			item.setHeader1((String)obj[5]);
        			item.setHeader2((String)obj[6]);
        			
        			Long idQ = new Long(((BigInteger)obj[7]).longValue());
        			item.setQuestionId(idQ);
        			list.add(item);
    			}else{
    				item.setSecondRevName((String)obj[1]);
    				item.setSecondRevComment((String)obj[2]);
    			}
    		}
    	}
    	return list;
    }
    
    private ReviewItemReport isContainsItem(List<ReviewItemReport> list, Long id){
    	if(list != null && list.size() > 0){
    		for(ReviewItemReport revItem:list){
    			if(revItem.getDetailId().intValue() == id.intValue())
    				return revItem;
    		}
    	}
    	return null;
    }
}

