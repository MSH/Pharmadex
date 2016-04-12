/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewChecklist;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ReviewBn implements Serializable {

    private static final long serialVersionUID = -1555668282210872889L;

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    private Review review;

    private List<ReviewChecklist> reviewChecklists;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private UploadedFile file;

    private boolean attach;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public boolean isAttach() {
        if (review.getFile() != null && review.getFile().length > 0)
            return true;
        else
            return false;
    }

    public void setAttach(boolean attach) {
        this.attach = attach;
    }

    @PostConstruct
    public void init(){
        try {

            Long prodAppID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reviewID"));
            review = reviewService.findReview(prodAppID);
            reviewChecklists = review.getReviewChecklists();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void handleFileUpload() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();

        if (file != null) {
            msg = new FacesMessage(bundle.getString("global.success"), file.getFileName() + bundle.getString("upload_success"));
            facesContext.addMessage(null, msg);
            try {
                review.setFile(IOUtils.toByteArray(file.getInputstream()));
                saveReview();
            } catch (IOException e) {
                msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public StreamedContent fileDownload() {
        byte[] file1 = review.getFile();
        InputStream ist = new ByteArrayInputStream(file1);
        StreamedContent download = new DefaultStreamedContent(ist);
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }


    public List<ReviewChecklist> getReviewChecklists() {
        if (getReview() == null)
            return null;

        return reviewChecklists;
    }

    public void setReviewChecklists(List<ReviewChecklist> reviewChecklists) {
        this.reviewChecklists = reviewChecklists;
    }

    public String saveReview() {
        reviewChecklists = review.getReviewChecklists();
        RetObject retObject = reviewService.saveReview(review);
        review = (Review) retObject.getObj();
        return "";
    }

    public String reviewerFeedback() {
        review.setReviewStatus(ReviewStatus.FEEDBACK);
        saveReview();
        return "/internal/processreg";
    }

    public String approveReview() {
        if (review.getRecomendType() == null) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }

        if(!review.getReviewStatus().equals(ReviewStatus.SUBMITTED)){
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }

        review.setReviewStatus(ReviewStatus.ACCEPTED);
        saveReview();
        return "/internal/processreg";
    }

    public String submitReview() {
        if (review.getRecomendType() == null) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }
        review.setSubmitDate(new Date());
        review.setReviewStatus(ReviewStatus.SUBMITTED);
        saveReview();
        return "/internal/processreg";
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
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
}
