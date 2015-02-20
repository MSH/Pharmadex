/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.service.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
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

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    private ReviewDetail reviewDetail;
    private Product product;


    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");


    public void saveReview(){
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        reviewDetail.setAnswered(true);
        reviewDetail =  reviewService.saveReviewDetail(reviewDetail);
        msg = new FacesMessage(bundle.getString("app_save_success"));
        facesContext.addMessage(null, msg);
    }

    public String back(){
        userSession.setReviewInfoID(reviewDetail.getReviewInfo().getId());
        return "reviewInfo";
    }

    public String submitReview(){
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        saveReview();
        userSession.setProduct(reviewDetail.getReviewInfo().getProdApplications().getProd());
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

    public Product getProduct() {
        if(product==null){
            product = productService.findProduct(userSession.getProduct().getId());

        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ReviewDetail getReviewDetail() {
        if(reviewDetail==null){
            DisplayReviewInfo displayReviewInfo = userSession.getDisplayReviewInfo();
            if(displayReviewInfo !=null) {
                    reviewDetail = reviewService.findReviewDetails(displayReviewInfo);
            }
        }
        return reviewDetail;
    }

    public void setReviewDetail(ReviewDetail reviewDetail) {
        this.reviewDetail = reviewDetail;
    }
}
