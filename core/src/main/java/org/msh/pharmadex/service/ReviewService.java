/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ReviewQDAO;
import org.msh.pharmadex.dao.iface.RevDeficiencyDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.dao.iface.ReviewDetailDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class ReviewService implements Serializable {


    @Autowired
    TimelineService timelineService;
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private GlobalEntityLists globalEntityLists;
    @Autowired
    private ChecklistService checklistService;
    @Autowired
    private ReviewInfoDAO reviewInfoDAO;
    @Autowired
    private ReviewQDAO reviewQDAO;
    @Autowired
    private ReviewDetailDAO reviewDetailDAO;
    @Autowired
    private ProdApplicationsService prodApplicationsService;
    @Autowired
    private RevDeficiencyDAO revDeficiencyDAO;
    @Autowired
    private CustomReviewDAO customReviewDAO;
    @Autowired
    private UserAccessService userAccessService;

    public List<ReviewInfoTable> findRevInfoTableByReviewer(Long reviewerID){
        if(reviewerID==null)
            return null;
        return customReviewDAO.findReviewInfoByReview(reviewerID);

    }

    public List<ReviewInfoTable> findReviewByReviewer(Long reviewerID){
        if(reviewerID==null)
            return null;
        return customReviewDAO.findReviewByReviewer(reviewerID);

    }

    public List<ReviewInfoTable> findAllPriSecReview() {
        return customReviewDAO.findAllPriSecReview();
    }


    public Review findReview(Long reviewID) {
        Review review = reviewDAO.findOne(reviewID);
//        List<ReviewChecklist> reviewChecklists = review.getReviewChecklists();
//        if (reviewChecklists.size() < 1) {
//            reviewChecklists = new ArrayList<ReviewChecklist>();
//            review.setReviewChecklists(reviewChecklists);
//            List<Checklist> allChecklist = checklistService.getChecklists(review.getProdApplications().getProdAppType(), true);
//            ReviewChecklist eachReviewChecklist;
//            for (int i = 0; allChecklist.size() > i; i++) {
//                eachReviewChecklist = new ReviewChecklist();
//                eachReviewChecklist.setChecklist(allChecklist.get(i));
//                eachReviewChecklist.setReview(review);
//                reviewChecklists.add(eachReviewChecklist);
//            }
//            review.setReviewChecklists(reviewChecklists);
//        }
        return review;
    }

    public RetObject saveReviewers(Review review) {
        RetObject retObject = new RetObject();
        Review returnvalue = reviewDAO.findByUser_UserIdAndProdApplications_Id(review.getUser().getUserId(), review.getProdApplications().getId());
        if (returnvalue == null) {
            retObject.setObj(reviewDAO.saveAndFlush(review));
            retObject.setMsg("success");
        } else {
            retObject.setMsg("exist");
        }
        return retObject;
    }

    public Review findReviewByUserAndProdApp(Long userId, Long prodAppID) {
        return reviewDAO.findByUser_UserIdAndProdApplications_Id(userId, prodAppID);
    }

    public ReviewInfo findReviewInfoByUserAndProdApp(Long userId, Long prodAppID) {
        List<ReviewInfo> reviewInfos = reviewInfoDAO.findByProdApplications_IdAndReviewer_UserIdOrSecReviewer_UserId(prodAppID, userId, userId);
        System.out.println("reviewinfos == " + reviewInfos.size());
        return reviewInfos.get(0);
    }

    public void delete(Review review) {
        reviewDAO.delete(review);
    }

    public ReviewInfo findReviewInfo(Long reviewInfoID) {
        if (reviewInfoID == null)
            return null;
        ReviewInfo reviewInfo = reviewInfoDAO.findOne(reviewInfoID);
        Hibernate.initialize(reviewInfo.getReviewDetails());
        Hibernate.initialize(reviewInfo.getReviewComments());

//        if (reviewInfo.getReviewDetails().size() < 1)
//            initReviewDetail(reviewInfo);
        return reviewInfo;
    }


//    public List<DisplayReviewQ> getDisplayReviewQ() {
//        List<ReviewQuestion> reviewQuestions = reviewQDAO.findAll();
//        List<DisplayReviewQ> header1 = new ArrayList<DisplayReviewQ>();
//        List<DisplayReviewQ.Header2> header2 = new ArrayList<DisplayReviewQ.Header2>();
//
//        String header1Name = "";
//        String header2Name = "";
//
//        List<DisplayReviewInfo> questions = null;
//        DisplayReviewQ dispHeader1 = null;
//        DisplayReviewQ.Header2 dispHeader2;
//        int size = reviewQuestions.size();
//        ReviewQuestion rq;
//        for (int i = 0; i < reviewQuestions.size(); i++) {
//            rq = reviewQuestions.get(i);
//            if (header1Name.equals("")) {
//                header1Name = rq.getHeader1();
//                header2Name = rq.getHeader2();
//                questions = new ArrayList<DisplayReviewInfo>();
//                questions.add(new DisplayReviewInfo(rq.getId(), null, rq.getQuestion(), false, null));
//                dispHeader1 = new DisplayReviewQ(header1Name, header2);
//                dispHeader2 = dispHeader1.new Header2(header2Name, questions);
//                header2.add(dispHeader2);
//                header1.add(dispHeader1);
//
////                    header2 = header1.new Header2(header2Name, questions);
//
////                    header2.put(header2Name, questions);
////                    header1.put(header1Name, header2);
//            } else {
//                if (header1Name.equals(rq.getHeader1())) {
//                    if (header2Name.equals(rq.getHeader2())) {
//                        questions.add(new DisplayReviewInfo(rq.getId(), null, rq.getQuestion(), false));
//                    } else {
//                        questions = new ArrayList<DisplayReviewInfo>();
//                        questions.add(new DisplayReviewInfo(rq.getId(), null, rq.getQuestion(), false));
//                        header2Name = rq.getHeader2();
//                        dispHeader2 = dispHeader1.new Header2(header2Name, questions);
//                        header2.add(dispHeader2);
////                            header2.put(header2Name, questions);
//                    }
//                } else {
//                    header2 = new ArrayList<DisplayReviewQ.Header2>();
//                    header1Name = rq.getHeader1();
//                    header2Name = rq.getHeader2();
//                    questions = new ArrayList<DisplayReviewInfo>();
//                    questions.add(new DisplayReviewInfo(rq.getId(), null, rq.getQuestion(), false));
////                        header1 = new DisplayReviewQ(header1Name, header2);
//                    dispHeader1 = new DisplayReviewQ(header1Name, header2);
//                    dispHeader2 = dispHeader1.new Header2(header2Name, questions);
//                    header2.add(dispHeader2);
//                    header1.add(dispHeader1);
//
//
////                        header2.put(header2Name, questions);
////                        header1.put(header1Name, header2);
//
//                }
//            }
//
//
//        }
//
//        return header1;
//    }

    @Transactional
    public List<ReviewDetail> findReviewDetails(Long userID, Long reviewInfoID) {
        return reviewQDAO.findReviewSummary(userID, reviewInfoID);

    }

    public List<ReviewDetail> initReviewDetail(ReviewInfo reviewInfo) {
        ProdApplications prodApplications = reviewInfo.getProdApplications();
        List<ReviewDetail> reviewDetails = new ArrayList<ReviewDetail>();
        List<ReviewQuestion> reviewQuestions = null;

        if (prodApplications.getProdAppType().equals(ProdAppType.RENEW)) {
            reviewQuestions = reviewQDAO.findBySRA();
        } else if (prodApplications.isSra()) {
            reviewQuestions = reviewQDAO.findBySRA();
        } else if (prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
            reviewQuestions = reviewQDAO.findByNewMolecule();
        } else {
            reviewQuestions = reviewQDAO.findByGenMed();
        }


//        if (prodApplications.isSra()) {
//            reviewQuestions = reviewQDAO.findBySRA();
//        } else {
//            reviewQuestions = reviewQDAO.findByProdAppType(prodApplications.getProdAppType());
//        }
        ReviewDetail reviewDetail;
        for (ReviewQuestion reviewQuestion : reviewQuestions) {
            reviewDetail = new ReviewDetail(reviewQuestion, reviewInfo, false);
            reviewDetails.add(reviewDetail);
        }

        reviewDetails = reviewDetailDAO.save(reviewDetails);
        return reviewDetails;
    }

    public List<DisplayReviewQ> getDisplayReviewSum(ReviewInfo ri) {
        List<ReviewDetail> reviewDetails = reviewQDAO.findReviewSummary(ri.getReviewer().getUserId(), ri.getId());
        boolean init = false;
        if (reviewDetails == null || reviewDetails.size() < 1) {
            init = true;
            reviewDetails = initReviewDetail(ri);
        }

//        List<ReviewQuestion> reviewQuestions = reviewQDAO.findAll();
        List<DisplayReviewQ> header1 = new ArrayList<DisplayReviewQ>();
        List<DisplayReviewQ.Header2> header2 = new ArrayList<DisplayReviewQ.Header2>();

        String header1Name = "";
        String header2Name = "";

        List<DisplayReviewInfo> questions = null;
        DisplayReviewQ dispHeader1 = null;
        DisplayReviewQ.Header2 dispHeader2;
        int size = reviewDetails.size();
        ReviewQuestion rq;
        ReviewDetail rd;
        for (int i = 0; i < reviewDetails.size(); i++) {
            rd = reviewDetails.get(i);
            rq = rd.getReviewQuestions();

            if (header1Name.equals("")) {
                header1Name = rq.getHeader1();
                header2Name = rq.getHeader2();
                questions = new ArrayList<DisplayReviewInfo>();
                dispHeader1 = new DisplayReviewQ(header1Name, header2);
                dispHeader2 = dispHeader1.new Header2(header2Name, questions);
                header2.add(dispHeader2);
                header1.add(dispHeader1);

//                    header2 = header1.new Header2(header2Name, questions);

//                    header2.put(header2Name, questions);
//                    header1.put(header1Name, header2);
            } else {
                if (header1Name.equals(rq.getHeader1())) {
                    if (!header2Name.equals(rq.getHeader2())) {
                        questions = new ArrayList<DisplayReviewInfo>();
//                        questions.add(new DisplayReviewInfo(rq.getId(), rd.getId(), rq.getQuestion(), rd.isAnswered()));
                        header2Name = rq.getHeader2();
                        dispHeader2 = dispHeader1.new Header2(header2Name, questions);
                        header2.add(dispHeader2);
//                            header2.put(header2Name, questions);
                    }
                } else {
                    header2 = new ArrayList<DisplayReviewQ.Header2>();
                    header1Name = rq.getHeader1();
                    header2Name = rq.getHeader2();
                    questions = new ArrayList<DisplayReviewInfo>();
//                    questions.add(new DisplayReviewInfo(rq.getId(), rd.getId(), rq.getQuestion(), rd.isAnswered()));
//                        header1 = new DisplayReviewQ(header1Name, header2);
                    dispHeader1 = new DisplayReviewQ(header1Name, header2);
                    dispHeader2 = dispHeader1.new Header2(header2Name, questions);
                    header2.add(dispHeader2);
                    header1.add(dispHeader1);


//                        header2.put(header2Name, questions);
//                        header1.put(header1Name, header2);

                }
            }
            questions.add(new DisplayReviewInfo(rq.getId(), rd.getId(), rq.getQuestion(), rd.isAnswered(), ri.getId()));
        }

        return header1;
    }

    public JasperPrint initRegCert(ProdApplications prodApplications, RevDeficiency reviewComment) throws JRException {
        String emailBody = reviewComment.getSentComment().getComment();
        Product product = prodApplications.getProduct();
        URL resource = getClass().getResource("/reports/rev_def_letter.jasper");
        HashMap param = new HashMap();
        param.put("appName", prodApplications.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength() + product.getDosUnit());
        param.put("dosForm", product.getDosForm().getDosForm());
        param.put("manufName", product.getManufName());
        param.put("appType", "New Medicine Registration");
        param.put("subject", "Product application deficiency letter for  " + product.getProdName());
        param.put("body", emailBody);
        param.put("address1", prodApplications.getApplicant().getAddress().getAddress1());
        param.put("address2", prodApplications.getApplicant().getAddress().getAddress2());
        param.put("country", prodApplications.getApplicant().getAddress().getCountry().getCountryName());
//        param.put("cso",user.getName());
        param.put("date", new Date());
//        param.put("summary", comment);
        param.put("appNumber", prodApplications.getProdAppNo());
//        param.put("registrar", "Major General Md Jahangir Hossain Mollik");

        return JasperFillManager.fillReport(resource.getFile(), param);
    }

    public RetObject createDefLetter(RevDeficiency reviewComment) {
        ProdApplications prodApp = prodApplicationsService.findProdApplications(reviewComment.getReviewInfo().getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_fir", ".pdf");
            JasperPrint jasperPrint = initRegCert(prodApp, reviewComment);
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
            attachment.setComment(reviewComment.getSentComment().getComment());
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Further Information Request");
            attachment.setUploadedBy(reviewComment.getUser());
            attachment.setComment("Automatically generated Letter");
            attachment.setContentType("application/pdf");
            attachment.setReviewInfo(reviewComment.getReviewInfo());
            reviewComment.setProdAppLetter(attachment);
            revDeficiencyDAO.saveAndFlush(reviewComment);

            TimeLine timeLine = new TimeLine();
            timeLine.setComment("Status changes due to further information request");
            timeLine.setRegState(RegState.FOLLOW_UP);
            timeLine.setProdApplications(prodApp);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(reviewComment.getUser());
            RetObject retObject = timelineService.saveTimeLine(timeLine);
            if (retObject.getMsg().equals("persist")) {
                timeLine = (TimeLine) retObject.getObj();
                prodApp = timeLine.getProdApplications();
                reviewComment.getReviewInfo().setProdApplications(prodApp);
            }
            return saveReviewInfo(reviewComment.getReviewInfo());

//                prodApplicationsDAO.updateApplication(prodApp);


        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    @Transactional
    public RetObject saveReviewInfo(ReviewInfo reviewInfo) {
        RetObject retObject = new RetObject();
        ReviewInfo ri = reviewInfoDAO.saveAndFlush(reviewInfo);
        Hibernate.initialize(ri.getReviewComments());
        Hibernate.initialize(ri.getReviewDetails());
        retObject.setObj(ri);
        retObject.setMsg("success");
        return retObject;
    }

    public RetObject saveReview(Review review) {
        RetObject retObject = new RetObject();
        retObject.setObj(reviewDAO.saveAndFlush(review));
        retObject.setMsg("success");
        return retObject;
    }

    public RetObject addReviewInfo(ReviewInfo reviewInfo) {
        RetObject retObject = new RetObject();
        ReviewInfo returnvalue = reviewInfoDAO.findByReviewer_UserIdAndProdApplications_Id(reviewInfo.getReviewer().getUserId(), reviewInfo.getProdApplications().getId());
        if (returnvalue == null) {
            retObject.setObj(reviewInfoDAO.saveAndFlush(reviewInfo));
            retObject.setMsg("success");
        } else {
            retObject.setMsg("exist");
        }
        return retObject;
    }

    public ReviewDetail findReviewDetails(DisplayReviewInfo displayReview) {
        if (displayReview.getReviewDetailID() != null) {
            return reviewDetailDAO.findOne(displayReview.getReviewDetailID());
        } else {
            ReviewDetail reviewDetail = new ReviewDetail();
            reviewDetail.setReviewQuestions(reviewQDAO.findOne(displayReview.getId()));
            reviewDetail.setReviewInfo(reviewInfoDAO.findOne(displayReview.getReviewInfoID()));
            return reviewDetail;
        }
    }

    public ReviewDetail saveReviewDetail(ReviewDetail reviewDetail) {
        return reviewDetailDAO.saveAndFlush(reviewDetail);
    }

    public ReviewDetail makeReviewNA(Long reviewDetailID) {
        ReviewDetail rd = reviewDetailDAO.findOne(reviewDetailID);
        rd.setAnswered(true);
        rd = reviewDetailDAO.saveAndFlush(rd);
        return rd;
    }

    public String submitReview(ReviewInfo reviewInfo) {
        reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
        reviewInfo.setSubmitDate(new Date());

//        List<ReviewDetail> reviewDetails = findReviewDetails(reviewInfo.getReviewer().getUserId(), reviewInfo.getId());
//        for(ReviewDetail reviewDetail : reviewDetails){
//            if(!reviewDetail.isAnswered()){
//                return "NOT_ANSWERED";
//            }
//        }

//        if(reviewInfo.getRecomendType()==null)
//            return "NO_RECOMMEND_TYPE";

        reviewInfoDAO.saveAndFlush(reviewInfo);
        return "SAVE";
    }

    public List<ReviewInfo> findReviewInfos(Long id) {
        return reviewInfoDAO.findByProdApplications_IdOrderByAssignDateAsc(id);
    }

    public byte[] findReviewDetailImage(Long reviewDetailID) {
        if (reviewDetailID == null)
            return null;
        ReviewDetail reviewDetail = reviewDetailDAO.findOne(reviewDetailID);
        if (reviewDetail != null)
            return reviewDetail.getFile();
        else
            return null;
    }

    public List<Review> findReviews(Long prodAppId) {
        return reviewDAO.findByProdApplications_Id(prodAppId);
    }

    public void deleteReviewInfo(ReviewInfo reviewInfo) {
        reviewInfoDAO.delete(reviewInfo);
    }

    public RetObject saveRevDeficiency(RevDeficiency revDeficiency) {
        revDeficiency = revDeficiencyDAO.saveAndFlush(revDeficiency);

        TimeLine timeLine = new TimeLine();
        timeLine.setComment("FIR recieved and status updated back to Under Review");
        timeLine.setProdApplications(revDeficiency.getReviewInfo().getProdApplications());
        timeLine.setUser(revDeficiency.getUser());
        timeLine.setStatusDate(new Date());
        timeLine.setRegState(RegState.REVIEW_BOARD);
        RetObject retObject = timelineService.saveTimeLine(timeLine);
        if (retObject.getMsg().equals("persist")) {
            timeLine = (TimeLine) retObject.getObj();
            revDeficiency.getReviewInfo().setProdApplications(timeLine.getProdApplications());
        }

        RetObject retObject2 = saveReviewInfo(revDeficiency.getReviewInfo());
        if (retObject2.getMsg().equals("success")) {
            ReviewInfo ri = (ReviewInfo) retObject2.getObj();
            revDeficiency.setReviewInfo(ri);
        }
        return new RetObject("success", revDeficiency);
    }

    public RetObject submitReviewInfo(ReviewInfo reviewInfo, ReviewComment reviewComment, Long userID) {
        if (reviewComment.getRecomendType() == null) {
//            reviewInfo.setReviewStatus(ReviewStatus.IN_PROGRESS);
            reviewComment.setFinalSummary(false);
        } else {
            if (reviewComment.getRecomendType().equals(RecomendType.RECOMENDED) || reviewComment.getRecomendType().equals(RecomendType.NOT_RECOMENDED)
                    || reviewComment.getRecomendType().equals(RecomendType.FIR)) {
                if (userAccessService.getWorkspace().isSecReview()) {
                    if (userID.equals(reviewInfo.getReviewer().getUserId()))
                        if (reviewInfo.isSecreview()) {
                            reviewInfo.setReviewStatus(ReviewStatus.SEC_REVIEW);
                        } else {
                            reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
                        }
                    else if (userID.equals(reviewInfo.getSecReviewer().getUserId()))
                        reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
                } else {
                    reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
                }
                reviewComment.setFinalSummary(true);
            }
//                } else if (reviewComment.getRecomendType().equals(RecomendType.FEEDBACK)) {
//                    reviewInfo.setReviewStatus(ReviewStatus.FEEDBACK);
//                    reviewComment.setFinalSummary(false);
//                }
        }
        reviewInfo.setSubmitDate(new Date());
        reviewInfo.getReviewComments().add(reviewComment);
//        reviewInfo.setRevDeficiencies(revDeficiencies);


        List<RevDeficiency> revDeficiencies = revDeficiencyDAO.findByReviewInfo_Id(reviewInfo.getId());
        for (RevDeficiency revDeficiency : revDeficiencies) {
            if (!revDeficiency.isResolved()) {
                return new RetObject("close_def");
            }
        }
        List<ReviewComment> reviewComments = reviewInfo.getReviewComments();
        for (ReviewComment rc : reviewComments) {
            if (rc.getRecomendType() != null) {
                reviewInfo.setRecomendType(rc.getRecomendType());
                reviewInfo.setExecSummary(rc.getComment());
            }
        }
        return saveReviewInfo(reviewInfo);
    }

    public JasperPrint getReviewReport(Long id) throws Exception {
        JasperPrint jasperPrint;

        jasperPrint = customReviewDAO.getReviewReport(id);
        return jasperPrint;


    }
}
