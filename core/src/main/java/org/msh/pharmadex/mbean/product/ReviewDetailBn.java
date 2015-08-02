/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.DisplayReviewInfo;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ReviewDetailBn implements Serializable {


    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private ReviewDetail reviewDetail;
    private boolean satisfactory;
    private ProdApplications prodApplications;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private String revType;
    private boolean priReviewer;
    private boolean secReviewer;


    public void saveReview() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        reviewDetail.setAnswered(true);
        ReviewInfo ri = reviewDetail.getReviewInfo();
        if (ri.getReviewStatus().equals(ReviewStatus.ASSIGNED))
            ri.setReviewStatus(ReviewStatus.IN_PROGRESS);
        User user = userService.findUser(userSession.getLoggedINUserID());
        reviewDetail.setUpdatedBy(user);
        reviewDetail = reviewService.saveReviewDetail(reviewDetail);
        msg = new FacesMessage(bundle.getString("app_save_success"));
        facesContext.addMessage(null, msg);
    }

    public String back() {
        JsfUtils.flashScope().put("reviewInfoID", reviewDetail.getReviewInfo().getId());
        return "reviewInfo";
    }

    public void satisAction() {
        if (reviewDetail.isSatifactory())
            satisfactory = true;
        else
            satisfactory = false;
    }

    public String submitReview() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        saveReview();
        JsfUtils.flashScope().put("reviewInfoID", reviewDetail.getReviewInfo().getId());
        msg = new FacesMessage(bundle.getString("app_submit_success"));
        facesContext.addMessage(null, msg);
        return "reviewInfo";

    }


    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ReviewDetail getReviewDetail() {
        if (reviewDetail == null) {
            DisplayReviewInfo displayReviewInfo = userSession.getDisplayReviewInfo();
            if (displayReviewInfo != null) {
                reviewDetail = reviewService.findReviewDetails(displayReviewInfo);
                setSatisfactory(reviewDetail.isSatifactory());
                prodApplications = prodApplicationsService.findProdApplications(reviewDetail.getReviewInfo().getProdApplications().getId());
                if (reviewDetail.getReviewInfo().getReviewer().getUserId().equals(userSession.getLoggedINUserID())) {
                    setPriReviewer(true);
                    setSecReviewer(false);
                } else if (reviewDetail.getReviewInfo().getSecReviewer().getUserId().equals(userSession.getLoggedINUserID())) {
                    setSecReviewer(true);
                    setPriReviewer(false);

                }
            }
        }
        return reviewDetail;
    }

    public void setReviewDetail(ReviewDetail reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

    public boolean isSatisfactory() {
        return satisfactory;
    }

    public void setSatisfactory(boolean satisfactory) {
        this.satisfactory = satisfactory;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null)
            getReviewDetail();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public String getRevType() {
        ReviewInfo reviewInfo = getReviewDetail().getReviewInfo();
        if (reviewInfo != null) {
            if (userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId())) {
                revType = bundle.getString("pri_processor");
            } else if (userSession.getLoggedINUserID().equals(reviewInfo.getSecReviewer().getUserId()))
                revType = bundle.getString("sec_processor");
        }
        return revType;
    }

    public void setRevType(String revType) {
        this.revType = revType;
    }

    public boolean isPriReviewer() {
        return priReviewer;
    }

    public void setPriReviewer(boolean priReviewer) {
        this.priReviewer = priReviewer;
    }

    public boolean isSecReviewer() {
        return secReviewer;
    }

    public void setSecReviewer(boolean secReviewer) {
        this.secReviewer = secReviewer;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
