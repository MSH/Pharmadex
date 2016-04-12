/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.dao.iface.SuspendDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.ResourceBundle;

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
    private TimelineService timelineService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    private ProdApplications prodApplications;
    private SuspDetail suspDetail;
    private User loggedInUser;

    @Transactional
    public SuspDetail findSuspendDetail(Long suspDetailID) {
        SuspDetail suspDetail = suspendDAO.findOne(suspDetailID);
        if (suspDetail.getSuspComments() != null)
            Hibernate.initialize(suspDetail.getSuspComments());
        if (suspDetail.getProdAppLetters() != null)
            Hibernate.initialize(suspDetail.getProdAppLetters());
        return suspDetail;
    }

    public List<SuspDetail> findSuspendByProd(Long prodApplicationsId) {
        List<SuspDetail> suspDetails = suspendDAO.findByProdApplications_Id(prodApplicationsId);
        return suspDetails;

    }

    public List<ReviewInfo> findReviewList(long userId,long appId){
        ArrayList<ReviewInfo> res = new ArrayList<ReviewInfo>();
        ReviewInfo reviewResult = reviewInfoDAO.findByReviewer_UserIdAndProdApplications_Id(userId, appId);
        if (reviewResult!=null)
            res.add(reviewResult);
        return res;
    }

    public void createSuspLetter() throws SQLException, IOException, JRException {
        File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_suspension", ".pdf");
        String fileName = invoicePDF.getName();
        LetterType letterType = LetterType.SUSP_NOTIF_LETTER;
        String letterTitle = "Suspension Notification Letter";
        URL resource = getClass().getResource("/reports/suspension.jasper");
        addLetter(invoicePDF, letterType, letterTitle, resource);
    }

    public void createCancelLetter() throws SQLException, IOException, JRException {
        File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_cancellation", ".pdf");
        String fileName = invoicePDF.getName();
        LetterType letterType = LetterType.CANCELLATION_LETTER;
        String letterTitle = "Cancellation Notification Letter";
        URL resource = getClass().getResource("/reports/suspension.jasper");
        addLetter(invoicePDF, letterType, letterTitle, resource);
    }

    public void createCancelSenderLetter() throws SQLException, IOException, JRException {
        File invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_cancelsender", ".pdf");
        String fileName = invoicePDF.getName();
        LetterType letterType = LetterType.CANCELLATION_SENDER_LETTER;
        String letterTitle = "Cancellation Notification to Sender Letter";
        URL resource = getClass().getResource("/reports/cancel_susp_sender.jasper");
        addLetter(invoicePDF, letterType, letterTitle, resource);
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
    private void addLetter(File invoicePDF, LetterType letterType, String letterTitle, URL resource) throws SQLException, JRException, IOException {
        JasperPrint jasperPrint = initRegCert(suspDetail.getSuspComments().get(0), resource);
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
        byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));

        ProdAppLetter attachment = new ProdAppLetter();
        attachment.setRegState(prodApplications.getRegState());
//            attachment.setComment(sampleTest.get);
        attachment.setFile(file);
        attachment.setProdApplications(prodApplications);
        attachment.setFileName(invoicePDF.getName());
        attachment.setTitle(letterTitle);
        attachment.setUploadedBy(suspDetail.getCreatedBy());
        attachment.setComment("System generated Letter");
        attachment.setLetterType(letterType);
        attachment.setContentType("application/pdf");
//        attachment.setSuspDetail(suspDetail);
        if (suspDetail.getProdAppLetters() == null)
            suspDetail.setProdAppLetters(new ArrayList<ProdAppLetter>());
        suspDetail.getProdAppLetters().add(attachment);
    }

    private void updateProdApp(RegState regState) {
        //If registration status changes then update timeline as well
        if (!prodApplications.getRegState().equals(regState)) {
            TimeLine timeLine = new TimeLine();
            timeLine.setRegState(regState);
            timeLine.setComment("The application is suspended with Suspension number " + suspDetail.getSuspNo());
            timeLine.setUser(loggedInUser);
            timeLine.setStatusDate(new Date());
            timeLine.setProdApplications(prodApplications);
            timelineService.saveTimeLine(timeLine);
        }

        prodApplications.setRegState(regState);
        User updatedBy = suspDetail.getUpdatedBy();
        if (updatedBy==null) {updatedBy =  loggedInUser;}
        prodApplicationsService.saveApplication(prodApplications, updatedBy.getUserId());
    }

    public JasperPrint initRegCert(SuspComment suspComment, URL resource) throws JRException, SQLException {
        String emailBody = suspComment.getComment();
        Product product = suspDetail.getProdApplications().getProduct();
//        Session hibernateSession = entityManager.unwrap(Session.class);
        Connection conn = entityManager.unwrap(Session.class).connection();
        HashMap param = new HashMap();
        List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
        ProdApplications prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
        String manufName = product.getManufName();
        if (manufName==null){
            List<ProdCompany> companyList = prodApplications.getProduct().getProdCompanies();
            if (companyList!=null){
                for(ProdCompany company:companyList){
                    if (company.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
                        manufName = company.getCompany().getCompanyName();
                        suspDetail.getProdApplications().getProduct().setManufName(manufName);
                        break;
                    }

                }
            }
        }
        param.put("id", prodApplications.getId());
        param.put("manufName", manufName);
        param.put("reason", emailBody);
        param.put("batchNo", suspDetail.getBatchNo());
//        param.put("cso",user.getName());

        JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
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
            suspDetails = suspendDAO.findAll();
        }
        if (userSession.isModerator()) {
            suspDetails = suspendDAO.findByModerator_UserId(userSession.getLoggedINUserID());
        }
        if (userSession.isReviewer()) {
            suspDetails = suspendDAO.findByReviewer_UserId(userSession.getLoggedINUserID());
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
        prodApplications.setRegState(suspDetail.getDecision());
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

    /**
     * DEPECATED
     */
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
                createSuspLetter();
                //update product applications
                updateProdApp(RegState.SUSPEND);
            } else if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT)) {
                if (suspDetail.getDecision().equals(RegState.SUSPEND)) {
                    createSuspLetter();
                } else if (suspDetail.getDecision().equals(RegState.CANCEL)) {
                    //process cancellation
                    //create cancellation letters
                    createCancelLetter();
                    createCancelSenderLetter();
                    suspDetail.setComplete(true);
                } else if (suspDetail.getDecision().equals(RegState.REGISTERED)) {
                    //update product
                    //update suspension detail
                    suspDetail.setComplete(true);
                }
                //update product applications
                updateProdApp(suspDetail.getDecision());
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
}
