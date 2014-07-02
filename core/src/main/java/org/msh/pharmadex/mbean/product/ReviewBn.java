/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewChecklist;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ReviewService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ReviewBn implements Serializable {

    private static final long serialVersionUID = -1555668282210872889L;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    @Autowired
    private ProcessProdBn processProdBn;

    @Autowired
    private ReviewService reviewService;

    private Review review;

    private List<ReviewChecklist> reviewChecklists;

    @Autowired
    private WebSession webSession;

    private UploadedFile file;

    private boolean attach;

    public boolean isAttach() {
        if (review.getFile() != null && review.getFile().length > 0)
            return true;
        else
            return false;
    }

    public void setAttach(boolean attach) {
        this.attach = attach;
    }

    public void handleFileUpload() {
        FacesMessage msg;
        if (file != null) {
            msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            try {
                review.setFile(IOUtils.toByteArray(file.getInputstream()));
                saveReview();
            } catch (IOException e) {
                msg = new FacesMessage("Error", file.getFileName() + " is not uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            msg = new FacesMessage("Error", file.getFileName() + " is not uploaded.");
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

    public String saveReview() {
        reviewChecklists = review.getReviewChecklists();
        review = reviewService.saveReview(review);
        return "";
    }

    public String submitReview() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (review.getRecomendType() == null) {
            facesContext.addMessage(null, new FacesMessage("Recommendation field cannot be empty.", "Please enter recommendation in order to submit the review."));
        }

        review.setSubmitDate(new Date());
        saveReview();
        return "/internal/processreg";
    }

    public String cancelReview() {
        webSession.setReview(null);
        return "/internal/processreg";

    }

    public void setReviewChecklists(List<ReviewChecklist> reviewChecklists) {
        this.reviewChecklists = reviewChecklists;
    }

    public Review getReview() {
        if (review == null) {
            review = reviewService.findReview(webSession.getReview().getId(), processProdBn.getProdApplications());
            reviewChecklists = review.getReviewChecklists();
        }
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
}
