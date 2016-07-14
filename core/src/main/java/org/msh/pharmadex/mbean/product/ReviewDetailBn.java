/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.DisplayReviewInfo;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.UserService;
import org.primefaces.event.FileUploadEvent;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ReviewDetailBn implements Serializable {

	private static final long serialVersionUID = -4955791411479803842L;

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
    
    private boolean fileImg = false;

    /**
     * Saves review without state changing
     */
    public void saveReview(boolean changeStatus) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        User user = userService.findUser(userSession.getLoggedINUserID());
        reviewDetail.setAnswered(true);
        reviewDetail.setUpdatedBy(user);
        if (changeStatus)
            reviewService.updateReviewStatus(reviewDetail);
        reviewDetail = reviewService.saveReviewDetail(reviewDetail);
        msg = new FacesMessage(bundle.getString("app_save_success"));
        facesContext.addMessage(null, msg);
    }

    public boolean visibleBtnDownload(){
    	boolean res = false;
    	if(reviewDetail != null){
    		if(reviewDetail.getFile() != null)
    			return true;
    	}
    	return res;
    }
    
    public void handleFileUpload(FileUploadEvent event) {
    	String fname = event.getFile().getFileName();
        FacesMessage message = new FacesMessage("Successful", fname + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        reviewDetail.setFile(event.getFile().getContents());
        reviewDetail.setFilename(fname);
        
        varificationFileName(fname);
    }

    private void varificationFileName(String text){
    	fileImg = (text.toLowerCase().endsWith(".png") || text.toLowerCase().endsWith(".gif")
    			|| text.toLowerCase().endsWith(".jpeg") || text.toLowerCase().endsWith(".jpg"));
    }
    
    public void satisAction() {
        if (reviewDetail.isSatifactory())
            satisfactory = true;
        else
            satisfactory = false;
    }

    /**
     * Reviewer press "Submit"
     * @return result
     */
    public String submitReview() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        saveReview(true);
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
                } else if (reviewDetail.getReviewInfo().getSecReviewer() != null && reviewDetail.getReviewInfo().getSecReviewer().getUserId().equals(userSession.getLoggedINUserID())) {
                    setSecReviewer(true);
                    setPriReviewer(false);
                }
            }
            if(reviewDetail.getFile() != null)
            	varificationFileName(reviewDetail.getFilename());
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
            if (reviewInfo.getReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId())) {
                revType = bundle.getString("pri_processor");
            } else if (reviewInfo.getSecReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getSecReviewer().getUserId()))
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

	public boolean isFileImg() {
		return fileImg;
	}

	public void setFileImg(boolean fileImg) {
		this.fileImg = fileImg;
	}
}
