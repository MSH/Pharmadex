/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.dao.iface.SuspendDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.enums.SuspensionStatus;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Author: usrivastava
 */
@Service
public class SuspendService implements Serializable {


	@Autowired
	private SuspendDAO suspendDAO;

	@Autowired
	private ReviewInfoDAO reviewInfoDAO;

	@Autowired
	private ProdApplicationsService prodApplicationsService;

	@Autowired
	private GlobalEntityLists globalEntityLists;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TimelineServiceET timelineServiceET;

	@Autowired
	private UserService userService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private LicenseHolderService licenseHolderService;

	@Autowired
	private UtilsByReports utilsByReports;

	private ProdApplications prodApplications;
	private SuspDetail suspDetail;
	private User loggedInUser;

	@Transactional
	public SuspDetail findSuspendDetail(Long suspDetailID) {
		suspDetail = suspendDAO.findOne(suspDetailID);
		Hibernate.initialize(suspDetail.getProdApplications().getProduct());
		if (suspDetail.getSuspComments() != null)
			Hibernate.initialize(suspDetail.getSuspComments());
		if (suspDetail.getProdAppLetters() != null)
			Hibernate.initialize(suspDetail.getProdAppLetters());
		if (suspDetail.getProdApplications().getReviewInfos()!=null){
			Hibernate.initialize(suspDetail.getProdApplications().getReviewInfos());
		}
		return suspDetail;
	}

	public List<SuspDetail> findSuspendByProd(Long prodApplicationsId) {
		List<SuspDetail> suspDetails = suspendDAO.findByProdApplications_Id(prodApplicationsId);
		return suspDetails;

	}

	public List<ReviewInfo> findReviewList(long userId,long appId){
		ArrayList<ReviewInfo> res = new ArrayList<ReviewInfo>();
		//ReviewInfo reviewResult = reviewInfoDAO.findByReviewer_UserIdAndProdApplications_Id(userId, appId);
		List<ReviewInfo> li =reviewInfoDAO.findByProdApplications_IdAndReviewer_UserId(appId,userId);
		if (li!=null)
			res=(ArrayList<ReviewInfo>) li;
		return res;
	}
	public List<ReviewInfo> findReviewListNew(long userId,long appId){
		ArrayList<ReviewInfo> res = new ArrayList<ReviewInfo>();
		//ReviewInfo reviewResult = reviewInfoDAO.findByReviewer_UserIdAndProdApplications_Id(userId, appId);
		List<ReviewInfo> li =reviewInfoDAO.findByProdApplications_IdAndR_UserId(appId,userId);
		if (li!=null)
			res=(ArrayList<ReviewInfo>) li;
		return res;
	}
	public void createSuspLetter() throws SQLException, IOException, JRException {
		File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_suspension_agent", ".pdf");
		String fileName = invoicePDF.getName();
		LetterType letterType = LetterType.SUSP_NOTIF_LETTER;
		String letterTitle = "Suspension Notification Letter";
		URL resource = getClass().getResource("/reports/suspension.jasper");
		JasperPrint jasperPrint = initRegCert(resource,letterType);
		addLetter(jasperPrint, invoicePDF, letterType, letterTitle, resource);
		JasperPrint jasperPrintLic = initRegCertLic(resource);
		if (jasperPrintLic!=null) {
			File repoPDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_suspension_licHolder", ".pdf");
			addLetter(jasperPrintLic, repoPDF, letterType, letterTitle, resource);
		}
	}


	public void createCancelLetter() throws SQLException, IOException, JRException {
		File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_cancellation_agent", ".pdf");
		LetterType letterType = LetterType.CANCELLATION_LETTER;
		String letterTitle = "Cancellation Notification Letter";
		String reportName = "/reports/cancellation.jasper";
		URL resource = getClass().getResource(reportName);
		JasperPrint jasperPrint = initRegCert(resource,letterType);
		addLetter(jasperPrint, invoicePDF, letterType, letterTitle, resource);
		jasperPrint = initRegCertLic(resource);
		if (jasperPrint!=null) {
			File repoPDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_cancellation_licHolder", ".pdf");
			addLetter(jasperPrint, repoPDF, letterType, letterTitle, resource);
		}

	}

	public void createCancelSenderLetter() throws SQLException, IOException, JRException {
		String decType = " "+ suspDetail.getDecision().name()+ "_sender";
		File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + decType, ".pdf");
		String fileName = invoicePDF.getName();
		LetterType letterType = LetterType.CANCELLATION_SENDER_LETTER;
		String letterTitle = "Cancellation or Supension Notification to Sender Letter";
		URL resource = getClass().getResource("/reports/cancel_susp_sender.jasper");
		JasperPrint jasperPrint = initRegCert(resource, letterType);
		addLetter(jasperPrint,invoicePDF, letterType, letterTitle, resource);
	}

	/**
	 * Creates a ProdAppLetter object and sets to Suspension Detail
	 *
	 * @param invoicePDF  sets the filename
	 * @param letterType  the letter type that is being generated
	 * @param letterTitle the title of the letter generated
	 * @param resource    URL of the jasper report file
	 * @throws SQLException
	 * @throws JRException
	 * @throws IOException
	 */
	private void addLetter(JasperPrint jasperPrint, File invoicePDF, LetterType letterType, String letterTitle, URL resource) throws SQLException, JRException, IOException {
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
		byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));

		ProdAppLetter attachment = new ProdAppLetter();
		attachment.setRegState(prodApplications.getRegState());
		attachment.setFile(file);
		attachment.setProdApplications(prodApplications);
		attachment.setFileName(invoicePDF.getName());
		attachment.setTitle(letterTitle);
		attachment.setUploadedBy(suspDetail.getCreatedBy());
		attachment.setComment("System generated Letter");
		attachment.setLetterType(letterType);
		attachment.setContentType("application/pdf");
		if (suspDetail.getProdAppLetters() == null)
			suspDetail.setProdAppLetters(new ArrayList<ProdAppLetter>());
		suspDetail.getProdAppLetters().add(attachment);
	}

	private void updateProdApp(RegState regState) {
		//If registration status changes then update timeline as well
		if (!prodApplications.getRegState().equals(regState)) {
			TimeLineSC timeLine = new TimeLineSC();
			timeLine.setRegState(regState);
			timeLine.setProdApplications(suspDetail);
			String comment = "The application is suspended with Suspension number " + suspDetail.getSuspNo();
			timeLine.setUser(loggedInUser);
			timeLine.setStatusDate(new Date());
			timelineServiceET.createTimeLineEvent(suspDetail,RegState.NEW_APPL,loggedInUser,comment);
		}

		prodApplications.setRegState(regState);
		User updatedBy = suspDetail.getUpdatedBy();
		if (updatedBy==null) {updatedBy =  loggedInUser;}
		prodApplicationsService.saveApplication(prodApplications, updatedBy.getUserId());
	}

	/**
	 * takes manufacturer name from company database, if this data missing in application
	 * @param prodApplications
	 * @return
	 */
	private String takeManufacturerName(ProdApplications prodApplications){
		String manufName = "";
		List<ProdCompany> companyList = prodApplications.getProduct().getProdCompanies();
		if (companyList!=null){
			for(ProdCompany company:companyList){
				if (company.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
					manufName = company.getCompany().getCompanyName();
					suspDetail.getProdApplications().getProduct().setManufName(manufName);
					return manufName;
				}

			}
		}
		return manufName;
	}

	/***
	 * Creates letter to applicant (local agent) that registration was suspended after complains
	 * @param resource - resource to Jasper template
	 * @return Jasper binary to print or save
	 * @throws JRException
	 * @throws SQLException
	 */
	public JasperPrint initRegCert(URL resource,LetterType letterType) throws JRException, SQLException {
		String emailBody = suspDetail.getReason();
		Product product = suspDetail.getProdApplications().getProduct();
		Connection conn = entityManager.unwrap(Session.class).connection();
		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		ProdApplications prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;

		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);
		if (!(letterType.equals(LetterType.CANCELLATION_SENDER_LETTER))){
			utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_NAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY_NAME, "", false);
		}else{
			utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_NAME, suspDetail.getOrgReported(), true);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", true);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", true);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY_NAME, "", true);
		}

		utilsByReports.putNotNull(UtilsByReports.KEY_ID, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_REASON, emailBody, true);

		String str = suspDetail.getBatchNo() != null ? suspDetail.getBatchNo() : "";
		utilsByReports.putNotNull(UtilsByReports.KEY_BATCHNO, str, true);

		str = suspDetail.getFinalSumm() != null ? suspDetail.getFinalSumm() : "";
		utilsByReports.putNotNull(UtilsByReports.KEY_REASON, str, true);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = "";
		if(suspDetail.getCreatedDate() != null){
			strDate = sdf.format(suspDetail.getCreatedDate());
			utilsByReports.putNotNull(UtilsByReports.KEY_REPORT_DATE, strDate, true);
		}

		if(suspDetail.getDecisionDate() != null){
			strDate = sdf.format(suspDetail.getDecisionDate());
			utilsByReports.putNotNull(UtilsByReports.KEY_DECISION_DATE, strDate, true);
		}
		
		str = suspDetail.getDecision() != null ? suspDetail.getDecision().toString():"";
		utilsByReports.putNotNull(UtilsByReports.KEY_DECISION, str, true);
		
		strDate = sdf.format(Calendar.getInstance().getTime());
		param.put("date", strDate);

		JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
		jasperPrint.removePage(1);
		conn.close();

		return jasperPrint;
	}

	/**
	 * Creates letter to applicant (local agent) that registration was suspended after complains
	 * @param resource resource to Jasper template
	 * @return
	 * @throws JRException
	 * @throws SQLException
	 */
	public JasperPrint initRegCertLic(URL resource) throws JRException, SQLException {
		String emailBody = suspDetail.getReason();
		Product product = suspDetail.getProdApplications().getProduct();

		Connection conn = entityManager.unwrap(Session.class).connection();
		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		ProdApplications prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
		
		Long  appID = prodApplications.getApplicant().getApplcntId();
		//all license holders of applicants
		List<LicenseHolder> res = licenseHolderService.findAllLicHoldersByApplicant(appID);
		if (res==null) return null;
		if (res.size()==0) return null;
		LicenseHolder li = null;

		List<Product> products;
		for(LicenseHolder lh:res){//scan each LH
			products = lh.getProducts();
			for(Product lcProd:products){// all products of the LH
				if (lcProd.getId()==product.getId()){
					//if product of LC is the product from application - we found lic holder
					li = lh;
					break;
				}
			}
		}

		if (li == null) return null;
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);
		
		String str = li.getName() != null ? li.getName():"";
		utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_NAME, str, true);
		
		Address addr = li.getAddress();
		if(addr != null){
			str = addr.getAddress1() != null ? addr.getAddress1():"";
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, str, true);
			
			str = addr.getAddress2() != null ? addr.getAddress2():"";
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, str, true);
			
			if(addr.getCountry() != null){
				str = addr.getCountry().getCountryName() != null ? addr.getCountry().getCountryName():"";
				utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, str, true);
			}
		}
		utilsByReports.putNotNull(UtilsByReports.KEY_ID, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_REASON, emailBody, true);

		str = suspDetail.getBatchNo() != null ? suspDetail.getBatchNo() : "";
		utilsByReports.putNotNull(UtilsByReports.KEY_BATCHNO, str, true);
		
		str = suspDetail.getDecision() != null ? suspDetail.getDecision().name().toLowerCase():"";
		utilsByReports.putNotNull(UtilsByReports.KEY_DECISION, str, true);
		
		str = suspDetail.getFinalSumm() != null ? suspDetail.getFinalSumm():"";
		utilsByReports.putNotNull(UtilsByReports.KEY_REASON, str, true);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = "";
		if(suspDetail.getCreatedDate() != null){
			strDate = sdf.format(suspDetail.getCreatedDate());
			utilsByReports.putNotNull(UtilsByReports.KEY_REPORT_DATE, strDate, true);
		}

		if(suspDetail.getDecisionDate() != null){
			strDate = sdf.format(suspDetail.getDecisionDate());
			utilsByReports.putNotNull(UtilsByReports.KEY_DECISION_DATE, strDate, true);
		}
		
		strDate = sdf.format(Calendar.getInstance().getTime());
		param.put("date", strDate);

		JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
		jasperPrint.removePage(1);
		conn.close();

		return jasperPrint;
	}

	public RetObject saveSuspend(SuspDetail suspDetail) {
		RetObject retObject = new RetObject();
		try {
			suspDetail.setUpdatedBy(loggedInUser);
			suspDetail.setUpdatedDate(new Date());
			suspDetail = suspendDAO.save(suspDetail);
			retObject.setObj(suspDetail);
			retObject.setMsg("persist");
		} catch (Exception ex) {
			ex.printStackTrace();
			retObject.setObj(ex.getMessage());
			retObject.setMsg("error");
		}
		return retObject;
	}

	private void generateSuspNo() {
		if (suspDetail.getSuspNo() == null || suspDetail.getSuspNo().equals("")) {
			RegistrationUtil registrationUtil = new RegistrationUtil();
			String suspNo = registrationUtil.generateAppNo(suspDetail.getProdApplications().getId(), "SUS");
			suspDetail.setSuspNo(suspNo);
		}
	}

	public RetObject addNewSusp(SuspDetail suspDetail) {
		if (null == suspDetail || null == suspDetail.getProdApplications())
			return new RetObject("error");
		return saveSuspend(suspDetail);

	}

	public List<SuspDetail> findAll(UserSession userSession) {
		List<SuspDetail> suspDetails = null;
		if (userSession.isHead()) {
			suspDetails = suspendDAO.findByComplete(false);
		}
		if (userSession.isModerator()) {
			suspDetails = suspendDAO.findByModerator_UserIdAndComplete(userSession.getLoggedINUserID(),false);
		}
		if (userSession.isReviewer()) {
			suspDetails = suspendDAO.findByReviewer_UserIdAndComplete(userSession.getLoggedINUserID(),false);
		}
		return suspDetails;

	}

	public SuspDetail findSuspDetail(Long id) {
		if (id == null)
			return null;
		return suspendDAO.findOne(id);
	}

	@Transactional
	public RetObject suspendProduct(SuspDetail suspDetail, User loggedInUser) throws SQLException, JRException {
		suspDetail.setComplete(true);
		if (suspDetail.getSuspEndDate() == null)
			suspDetail.setCanceled(true);

		ProdApplications prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
		if (suspDetail.getDecision().equals(RecomendType.REGISTER)) {
			prodApplications.setRegState(RegState.REGISTERED);
		}else if (suspDetail.getDecision().equals(RecomendType.CANCEL)) {
			prodApplications.setRegState(RegState.CANCEL);
		}else
			prodApplications.setRegState(RegState.SUSPEND);
		//        prodApplications.setRegExpiryDate(suspDetail.getSuspStDate());
		prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());


		saveSuspend(suspDetail);
		//        generateSuspLetter(suspDetail);
		return new RetObject("persist", suspDetail);


	}

	public RetObject submitReview(SuspDetail suspDetail, Long loggedINUserID) {
		RetObject retObject;
		if (null == suspDetail || null == suspDetail.getProdApplications())
			return new RetObject("error");

		setSuspDetail(suspDetail, loggedINUserID);

		if (suspDetail.getDecision().equals(RegState.SUSPEND)) {
			suspDetail.setSuspensionStatus(SuspensionStatus.FEEDBACK);
		} else {
			suspDetail.setSuspensionStatus(SuspensionStatus.SUBMIT);
		}

		retObject = saveSuspend(suspDetail);
		return retObject;
	}

	public ReviewInfo findLastReviewResult(SuspDetail suspDetail){
		List<ReviewInfo> rl = this.findReviewList(suspDetail.getReviewer().getUserId(), suspDetail.getProdApplications().getId());
		if (rl==null) return null;
		if (rl.size()==0) return null;
		if (rl.size()==1) return rl.get(0);
		//if exists a few review results, search for last
		Calendar maxCal = Calendar.getInstance();
		maxCal.add(Calendar.YEAR, -1);
		Date maxDate = maxCal.getTime();
		int indexOfMax=0; int i=0;
		for(ReviewInfo info:rl){
			if (info.getSubmitDate().after(maxDate)){
				maxDate = info.getSubmitDate();
				indexOfMax = i;
			}
			i++;
		}
		return rl.get(indexOfMax);
	}

	public RetObject submitModeratorDecision(SuspDetail sDetail, Long userID, String summary, String decision){
		RetObject retObject;
		suspDetail=sDetail;
		//TODO Use resource
		if (summary==null) summary="";
		if ("RECOMMENDED".equalsIgnoreCase(decision)){
			//moderator approves review and sends application to head
			if (!"".equals(summary))
				addComment(summary,userID);
			suspDetail.setSuspensionStatus(SuspensionStatus.RESULT);
		}else if ("NOT RECOMMENDED".equalsIgnoreCase(decision)){
			if (!"".equals(summary)){
				addComment(summary,userID);
			}
			//return suspension status to previous (IN_PROCESS) and review status to ASSIGNED for current reviewer
			suspDetail.setSuspensionStatus(SuspensionStatus.IN_PROGRESS);
			User reviewer = suspDetail.getReviewer();
			if (reviewer!=null){
				ReviewInfo review = reviewService.findReviewInfoByUserAndProdApp(reviewer.getUserId(), suspDetail.getProdApplications().getId());
				review.setReviewStatus(ReviewStatus.ASSIGNED);
				reviewService.saveReviewInfo(review);
			}
		}
		retObject = saveSuspend(suspDetail);
		return retObject;
	}

	private void addComment(String comment, long userID){
		List<SuspComment> suspComments = suspDetail.getSuspComments();
		if (suspComments == null) {
			suspComments = new ArrayList<SuspComment>();
		}

		SuspComment suspComment = new SuspComment();
		suspComment.setComment(comment);
		suspComment.setUser(userService.findUser(userID));
		suspComment.setDate(new Date());
		suspComment.setSuspDetail(suspDetail);
		suspComments.add(suspComment);
	}

	public RetObject submitModeratorComment(SuspDetail suspDetail, Long loggedINUserID) throws SQLException, JRException {
		RetObject retObject;
		if (null == suspDetail || null == suspDetail.getProdApplications())
			return new RetObject("error");

		setSuspDetail(suspDetail, loggedINUserID);

		if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.REQUESTED)) {
			suspDetail.setSuspensionStatus(SuspensionStatus.ASSIGNED);
		}

		if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.SUBMIT)) {
			suspDetail.setSuspensionStatus(SuspensionStatus.RESULT);
		}

		retObject = new RetObject("persist", saveSuspend(suspDetail));

		return retObject;
	}


	public RetObject submitHead(SuspDetail suspDetail, Long loggedINUserID) {
		RetObject retObject;
		try {
			if (null == suspDetail || null == suspDetail.getProdApplications())
				return new RetObject("error");

			setSuspDetail(suspDetail, loggedINUserID);

			//generate a suspension number and letter
			if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.REQUESTED)) {
				//new suspension request, generate a susp ID for processing
				generateSuspNo();
				//generate a suspension letter
				//createSuspLetter(); // no need - Legesse 28/04
				//update product applications
				updateProdApp(RegState.NEW_APPL);
			} else if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT)) {
				//process is finished, set final state
				if (suspDetail.getDecision().equals(RecomendType.SUSPEND)) {
					createSuspLetter();
					createCancelSenderLetter();
					suspDetail.setComplete(true);
					updateProdApp(RegState.SUSPEND);
				} else if (suspDetail.getDecision().equals(RecomendType.CANCEL)) {
					//process cancellation
					//create cancellation letters
					createCancelLetter();
					createCancelSenderLetter();
					suspDetail.setComplete(true);
					updateProdApp(RegState.CANCEL);
				} else{
					//update product
					//update suspension detail
					suspDetail.setComplete(true);
					updateProdApp(RegState.REGISTERED);
				}
				suspDetail.setComplete(true);
			}
			//update suspension detail
			retObject = saveSuspend(this.suspDetail);
			return retObject;
		} catch (SQLException e) {
			e.printStackTrace();
			return new RetObject("error", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new RetObject("error", e.getMessage());
		} catch (JRException e) {
			e.printStackTrace();
			return new RetObject("error", e.getMessage());
		}

	}

	public void setSuspDetail(SuspDetail suspDetail, Long loggedINUserID) {
		if (suspDetail != null && suspDetail.getProdApplications() != null && loggedINUserID != null) {
			this.suspDetail = suspDetail;
			this.prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
			this.loggedInUser = userService.findUser(loggedINUserID);
		}
	}

	private String getUserRole(ReviewInfo reviewInfo, User curUser){
		if (userService.userHasRole(curUser,"moderator")){
			return "moderator";
		}else if (userService.userHasRole(curUser,"Head")){
			return "head";
		}
		return "assesor";
	}
	/**
	 * Find out previous review decision (for TL of Assesor, for Head - TL)
	 * If some decisions exist, procedure will return latest decision
	 * @return
	 */
	private ReviewComment getSubordinateDecision(ReviewInfo reviewInfo, User curUser){
		String curUserRole = getUserRole(reviewInfo, curUser);
		String searchRole="";
		// determine, whom comment should be found (which role)
		if (curUserRole.equals("moderator")){
			searchRole="assesor";
		}else if (curUserRole.equals("head")){
			searchRole="moderator";
		}else
			return  null;

		if ("".equals(curUserRole)) return null;

		List<ReviewComment> reviewComments = reviewInfo.getReviewComments();
		ReviewComment newlyComment = null;
		Date lastDate = Calendar.getInstance().getTime();
		for (ReviewComment rc : reviewComments) {
			if (!rc.getDate().before(suspDetail.getCreatedDate())) {
				if (rc.getRecomendType() != null) {
					User assesor = rc.getUser();
					if (userService.userHasRole(assesor, searchRole)) {
						if (rc.getDate().after(lastDate) || newlyComment == null) {
							lastDate = rc.getDate();
							newlyComment = rc;
						}
					}
				}
			}
		}
		return newlyComment;
	}

	public SuspensionStatus submitApprove(SuspDetail sDetail, ReviewInfo reviewInfo, RecomendType decision, Long userId){
		suspDetail=sDetail;
		SuspensionStatus result=null;
		User curUser = userService.findUser(userId);
		ReviewComment lastAssesorsComment = getSubordinateDecision(reviewInfo,curUser);
		RecomendType previousRecommendation = lastAssesorsComment.getRecomendType();
		String curUserRole = getUserRole(reviewInfo, curUser);
		if (decision.equals(previousRecommendation)){
			reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
			//previous decisions are the same
			if (curUserRole.equals("head")){ //am i head
				suspDetail.setDecision(decision);
				result=SuspensionStatus.RESULT;
			}else{//moderator
				result=SuspensionStatus.RESULT;
			}
		}else{//review results are different, i.e. negative decision
			if (curUserRole.equals("head")){//am I head
				//return to moderator for new decision (review)
				suspDetail.setDecision(decision);
				reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
				result=SuspensionStatus.SUBMIT;
			}else{//I am moderator
				//return to assesor for new review
				//change assesors review status (must be reviewed)
				suspDetail.setDecision(decision);
				ReviewInfo lastAssesorsReview = lastAssesorsComment.getReviewInfo();
				lastAssesorsReview.setReviewStatus(ReviewStatus.ASSIGNED);
				reviewService.saveReviewInfo(lastAssesorsReview);
				//return suspension process to assesors review step
				result=SuspensionStatus.ASSIGNED;
			}
		}
		reviewService.saveReviewInfo(reviewInfo);
		return result;
	}
}
