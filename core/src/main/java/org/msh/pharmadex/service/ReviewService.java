/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ReviewQDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.dao.iface.ReviewDetailDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class ReviewService implements Serializable {


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

    public Review findReview(Review review) {
        review = reviewDAO.findOne(review.getId());
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
        return reviewInfoDAO.findByReviewer_UserIdAndProdApplications_Id(userId, prodAppID);
    }

    public void delete(Review review) {
        reviewDAO.delete(review);
    }

    public ReviewInfo findReviewInfo(Long reviewInfoID) {
        ReviewInfo reviewInfo = reviewInfoDAO.findOne(reviewInfoID);
        if (reviewInfo.getReviewDetails().size() < 1)
            initReviewDetail(reviewInfo);
        return reviewInfo;
    }

    @Transactional
    public List<ReviewDetail> findReviewDetails(Long userID, Long reviewInfoID) {
        return reviewQDAO.findReviewSummary(userID, reviewInfoID);

    }

    public List<ReviewDetail> initReviewDetail(ReviewInfo reviewInfo) {
        List<ReviewDetail> reviewDetails = new ArrayList<ReviewDetail>();
        List<ReviewQuestion> reviewQuestions = reviewQDAO.findAll();

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


    public RetObject saveReviewInfo(ReviewInfo reviewInfo) {
        RetObject retObject = new RetObject();
        retObject.setObj(reviewInfoDAO.saveAndFlush(reviewInfo));
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
        if(displayReview.getReviewDetailID() !=null){
            return reviewDetailDAO.findOne(displayReview.getReviewDetailID());
        }else{
            ReviewDetail reviewDetail = new ReviewDetail();
            reviewDetail.setReviewQuestions(reviewQDAO.findOne(displayReview.getId()));
            reviewDetail.setReviewInfo(reviewInfoDAO.findOne(displayReview.getReviewInfoID()));
            return  reviewDetail;
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

        if(reviewInfo.getRecomendType()==null)
            return "NO_RECOMMEND_TYPE";

        reviewInfoDAO.saveAndFlush(reviewInfo);
        return "SAVE";
    }

    public List<ReviewInfo> findReviewInfos(Long id) {
        return reviewInfoDAO.findByProdApplications_IdOrderByAssignDateAsc(id);
    }

    public List<Review> findReviews(Long prodAppId) {
        return reviewDAO.findByProdApplications_Id(prodAppId);
    }

    public void deleteReviewInfo(ReviewInfo reviewInfo) {
        reviewInfoDAO.delete(reviewInfo);
    }
}
