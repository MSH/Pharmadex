package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.UserAccessMBean;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.context.RequestContext;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.*;
import java.util.ResourceBundle;

import static javax.faces.context.FacesContext.getCurrentInstance;

/**
 * Created by utkarsh on 3/6/15.
 */

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdReviewBn implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    @ManagedProperty(value = "#{userAccessMBean}")
    private UserAccessMBean userAccessMBean;

    private List<Review> reviews;
    private List<ReviewInfo> reviewInfos;
    private boolean checkReviewStatus;
    private User reviewer;
    private User secReviewer;
    private boolean displaySecReview = false;
    private List<ReviewComment> reviewComments;
    private ReviewComment reviewComment;

    private Review review;
    private ReviewInfo reviewInfo;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void secreviewListener() {
        if (reviewInfo.isSecreview()) {
            setDisplaySecReview(true);
        } else {
            setDisplaySecReview(false);
        }
    }

    public void assignReviewer() {
        facesContext = FacesContext.getCurrentInstance();
        ProdApplications prodApplications = processProdBn.getProdApplications();

        if (!userAccessMBean.isDetailReview()) {
            if (review == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
            }
            review.setProdApplications(prodApplications);
            review.setReviewStatus(ReviewStatus.ASSIGNED);
            review.setAssignDate(new Date());
            review.setUser(reviewer);

            RetObject retObject = reviewService.saveReviewers(review);
            if (!retObject.getMsg().equalsIgnoreCase("success")) {
                if (retObject.getMsg().equals("exist")) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "Reviewer has already been assigned"));

                } else
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
            } else {
                reviews.add(review);
                updateRegState();
            }
            review = new Review();
        } else {
            if (reviewInfo == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
            }
            reviewInfo.setReviewer(reviewer);
            reviewInfo.setAssignDate(new Date());
            reviewInfo.setProdApplications(prodApplications);
            reviewInfo.setReviewStatus(ReviewStatus.ASSIGNED);
            if (userAccessMBean.getWorkspace().isSecReview())
                reviewInfo.setSecReviewer(secReviewer);

            if (reviewInfo.getDueDate().after(new Date())) {
                RetObject riRetObj = reviewService.addReviewInfo(reviewInfo);
                if (!riRetObj.getMsg().equalsIgnoreCase("success")) {
                    if (riRetObj.getMsg().equals("exist")) {
                        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "Reviewer has already been assigned"));

                    } else
                        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
                } else {
                    reviewInfos.add(reviewInfo);
                    updateRegState();
                    initProcessorAdd();
                }
                reviewInfo = new ReviewInfo();
            } else {
                facesContext.addMessage(null, new FacesMessage("Due date must be in the future."));
            }
        }
        secReviewer = null;
        reviewer = null;
        displaySecReview = false;
    }

    public void changeReviewer() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            ProdApplications prodApplications = processProdBn.getProdApplications();
            if (reviewInfo == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
            }
            reviewInfo.setReviewer(reviewer);
            reviewInfo.setAssignDate(new Date());
            if (reviewInfo.getReviewComments() == null)
                reviewInfo.setReviewComments(new ArrayList<ReviewComment>());
            reviewInfo.getReviewComments().add(reviewComment);
            if (userAccessMBean.getWorkspace().isSecReview())
                reviewInfo.setSecReviewer(secReviewer);

            if (secReviewer != null)
                reviewInfo.setSecreview(true);
            else
                reviewInfo.setSecreview(false);

            if (reviewInfo.getDueDate().after(new Date())) {
                RetObject riRetObj = reviewService.updateReviewInfo(reviewInfo);
                if (!riRetObj.getMsg().equalsIgnoreCase("success")) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
                    reviewInfo = new ReviewInfo();
                } else {
                    facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
                    reviewInfo = new ReviewInfo();

                }
            } else {
                facesContext.addMessage(null, new FacesMessage("Due date must be in the future."));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Due date must be in the future.", ex.getMessage()));
        }
    }
    
    private void updateRegState() {
        if (!processProdBn.getProdApplications().getRegState().equals(RegState.REVIEW_BOARD)) {
            TimeLine timeLine = new TimeLine();
            timeLine.setRegState(RegState.REVIEW_BOARD);
            timeLine.setComment("Sent to the Review team for further review");
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
    }

    public void initProcessorAdd() {
    	//RequestContext.getCurrentInstance().reset(":reghome:processtab:prireviewac");
        review = new Review();
        review.setUser(new User());
        review.setProdApplications(processProdBn.getProdApplications());
        review.setAssignDate(new Date());
        review.setReviewStatus(ReviewStatus.ASSIGNED);

        reviewInfo = new ReviewInfo();
        reviewInfo.setReviewer(new User());
        reviewInfo.setProdApplications(processProdBn.getProdApplications());
        reviewInfo.setAssignDate(new Date());
        reviewInfo.setReviewStatus(ReviewStatus.ASSIGNED);
        
    }

    public void initProcessorChange(ReviewInfo ri) {
        try {
            reviewInfo = reviewService.findReviewInfo(ri.getId());
            reviewInfo.setUpdatedDate(new Date());
            reviewInfo.setUpdatedBy(processProdBn.getUser());
            reviewComment = new ReviewComment(reviewInfo, processProdBn.getUser(), RecomendType.COMMENT);
            reviewComment.setDate(new Date());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean getCheckReviewStatus() {
//        if (prodApplications.getId() == null)
        ProdApplications prodApplications = processProdBn.getProdApplications();
        reviews = reviewService.findReviews(prodApplications.getId());
        if (prodApplications != null) {
            if (userAccessMBean.isDetailReview()) {
                for (ReviewInfo ri : getReviewInfos()) {
                    if (ri.getReviewStatus().equals(ReviewStatus.ACCEPTED))
                        checkReviewStatus = true;
                    else
                        checkReviewStatus = false;

                }
            } else {
                for (Review each : reviews) {
                    if (!each.getReviewStatus().equals(ReviewStatus.ACCEPTED)) {
                        checkReviewStatus = false;
                        break;
                    } else {
                        checkReviewStatus = true;
                    }
                }
            }
        }
        return checkReviewStatus;

    }

    public List<Review> getReviews() {
        if (reviews == null && processProdBn.getProdApplications() != null) {
            reviews = reviewService.findReviews(processProdBn.getProdApplications().getId());
        }
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String deleteReview(Review review) {
        reviews.remove(review);
        facesContext = FacesContext.getCurrentInstance();
        try {
            reviewService.delete(review);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    resourceBundle.getString("global.success"), resourceBundle.getString("comment_del_success")));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("comment_del_fail")));
            e.printStackTrace();
        }
        return null;
    }

    public String deleteReviewInfo(ReviewInfo reviewInfo) {
        reviewInfos.remove(reviewInfo);
        facesContext = FacesContext.getCurrentInstance();
        try {
            reviewService.deleteReviewInfo(reviewInfo);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    resourceBundle.getString("global.success"), resourceBundle.getString("comment_del_success")));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("comment_del_fail")));
            e.printStackTrace();
        }
        return null;
    }

    public List<ReviewInfo> getReviewInfos() {
        if (reviewInfos == null) {
            reviewInfos = processProdBn.getReviewInfos();
        }
        return reviewInfos;
    }

    public void setReviewInfos(List<ReviewInfo> reviewInfos) {
        this.reviewInfos = reviewInfos;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
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

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public UserAccessMBean getUserAccessMBean() {
        return userAccessMBean;
    }

    public void setUserAccessMBean(UserAccessMBean userAccessMBean) {
        this.userAccessMBean = userAccessMBean;
    }

    public boolean isCheckReviewStatus() {
        return checkReviewStatus;
    }

    public void setCheckReviewStatus(boolean checkReviewStatus) {
        this.checkReviewStatus = checkReviewStatus;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public ReviewInfo getReviewInfo() {
        if (reviewInfo == null)
            reviewInfo = new ReviewInfo();
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public User getSecReviewer() {
        return secReviewer;
    }

    public void setSecReviewer(User secReviewer) {
        this.secReviewer = secReviewer;
    }

    public boolean isDisplaySecReview() {
        return displaySecReview;
    }

    public void setDisplaySecReview(boolean displaySecReview) {
        this.displaySecReview = displaySecReview;
    }

    public List<ReviewComment> getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(List<ReviewComment> reviewComments) {
        this.reviewComments = reviewComments;
    }

    public ReviewComment getReviewComment() {
    	if(reviewComment == null){
    		reviewComment=new ReviewComment(reviewInfo, processProdBn.getUser(), RecomendType.COMMENT);
    	}
        return reviewComment;
    }

    public void setReviewComment(ReviewComment reviewComment) {
        this.reviewComment = reviewComment;
    }

}
