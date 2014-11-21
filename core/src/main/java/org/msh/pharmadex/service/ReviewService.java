/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewChecklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
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

    public Review findReview(Long id, ProdApplications prodApplications) {
        Review review = reviewDAO.findOne(id);
        List<ReviewChecklist> reviewChecklists = review.getReviewChecklists();
        if (reviewChecklists.size() < 1) {
            reviewChecklists = new ArrayList<ReviewChecklist>();
            review.setReviewChecklists(reviewChecklists);
            List<Checklist> allChecklist = checklistService.getChecklists(prodApplications.getProdAppType(), true);
            ReviewChecklist eachReviewChecklist;
            for (int i = 0; allChecklist.size() > i; i++) {
                eachReviewChecklist = new ReviewChecklist();
                eachReviewChecklist.setChecklist(allChecklist.get(i));
                eachReviewChecklist.setReview(review);
                reviewChecklists.add(eachReviewChecklist);
            }
            review.setReviewChecklists(reviewChecklists);
        }
        return review;
    }

    public Review saveReview(Review review) {
        return reviewDAO.saveAndFlush(review);
    }
}
