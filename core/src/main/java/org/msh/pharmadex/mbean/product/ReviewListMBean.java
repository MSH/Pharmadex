package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.JsfUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ReviewListMBean implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    private List<ReviewInfoTable> reviewInfoTables;
    private List<ReviewInfoTable> filteredReviewInfos;

    private List<ReviewInfoTable> allReviews;


    public String sentToDetail(Long id){
        JsfUtils.flashScope().put("reviewInfoID", id);
        return "/internal/reviewInfo.faces";
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<ReviewInfoTable> getReviewInfoTables() {
        if(reviewInfoTables == null){
            reviewInfoTables = reviewService.findRevInfoTableByReviewer(userSession.getLoggedINUserID());
        }

        return reviewInfoTables;
    }

    public void setReviewInfoTables(List<ReviewInfoTable> reviewInfoTables) {
        this.reviewInfoTables = reviewInfoTables;
    }

    public List<ReviewInfoTable> getAllReviews() {
        if (allReviews == null) {
            allReviews = reviewService.findAllPriSecReview();
        }
        return allReviews;
    }

    public void setAllReviews(List<ReviewInfoTable> allReviews) {
        this.allReviews = allReviews;
    }

    public List<ReviewInfoTable> getFilteredReviewInfos() {
        return filteredReviewInfos;
    }

    public void setFilteredReviewInfos(List<ReviewInfoTable> filteredReviewInfos) {
        this.filteredReviewInfos = filteredReviewInfos;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


}
