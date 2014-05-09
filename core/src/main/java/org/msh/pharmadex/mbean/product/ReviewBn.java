/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewChecklist;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@Component
@Scope("request")
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

    private boolean submitted;

    @Autowired
    private WebSession webSession;


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

        return "";
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
            review = reviewService.findReview(webSession.getReview().getId());
            reviewChecklists = review.getReviewChecklists();
        }
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public boolean isSubmitted() {
        if (review.getSubmitDate() != null)
            submitted = true;
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
