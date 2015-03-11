/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
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
public class ReviewInfoBn implements Serializable {


    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    private UploadedFile file;
    private ReviewInfo reviewInfo;
    private Product product;
    private List<DisplayReviewQ> displayReviewQs;
    private String selID;
    private boolean readOnly = false;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public String getSelID() {
        return selID;
    }

    public void setSelID(String selID) {
        this.selID = selID;
    }

    public void handleFileUpload() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();

        if (file != null) {
            msg = new FacesMessage(bundle.getString("global.success"), file.getFileName() + bundle.getString("upload_success"));
            facesContext.addMessage(null, msg);
            try {
                reviewInfo.setFile(IOUtils.toByteArray(file.getInputstream()));
//                saveReview();
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
        byte[] file1 = reviewInfo.getFile();
        InputStream ist = new ByteArrayInputStream(file1);
        StreamedContent download = new DefaultStreamedContent(ist);
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void reviewNA(DisplayReviewInfo displayReviewInfo) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        msg = new FacesMessage("Selected question is marked not applicable.");
        reviewService.makeReviewNA(displayReviewInfo.getReviewDetailID());
        displayReviewInfo.setSave(true);
        displayReviewQs = null;
        facesContext.addMessage(null, msg);

    }

    public String saveReview() {
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        return "";
    }

    public String reviewerFeedback() {
        reviewInfo.setReviewStatus(ReviewStatus.FEEDBACK);
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        return "/internal/processreg";
    }

    public String approveReview() {
        if (reviewInfo.getRecomendType() == null) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }

        if (!reviewInfo.getReviewStatus().equals(ReviewStatus.SUBMITTED)) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }

        reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
        saveReview();
        userSession.setProdApplications(reviewInfo.getProdApplications());
        userSession.setProduct(reviewInfo.getProdApplications().getProd());
        return "/internal/processreg";
    }

    public String updateReview(DisplayReviewInfo displayReviewInfo) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        msg = new FacesMessage(bundle.getString("global.success") + " Selected ID == " + displayReviewInfo.getId(), "Selected ID == " + displayReviewInfo.getId());
        facesContext.addMessage(null, msg);
        userSession.setDisplayReviewInfo(displayReviewInfo);
        return "reviewdetail";

    }

    public String submitReview() {
        FacesMessage msg = null;
        facesContext = FacesContext.getCurrentInstance();
        if (reviewInfo.getRecomendType() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please provide recommendation type.");
            facesContext.addMessage(null, msg);
            return "";
        }
        String retValue = reviewService.submitReview(reviewInfo);
        if (retValue.equals("NOT_ANSWERED")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please answer all the questions");
            facesContext.addMessage(null, msg);
            return "";
        } else if (retValue.equals("SAVE")) {
            msg = new FacesMessage(bundle.getString("global.success"));
            facesContext.addMessage(null, msg);
            return "processreg";
        }
        return "";
    }

    public String cancelReview() {
        userSession.setReview(null);
        userSession.setProdApplications(reviewInfo.getProdApplications());
        userSession.setProduct(reviewInfo.getProdApplications().getProd());
        return "/internal/processreg";

    }


    public ReviewInfo getReviewInfo() {
        if (reviewInfo == null) {
            if (userSession.getReviewInfoID() != null) {
                reviewInfo = reviewService.findReviewInfo(userSession.getReviewInfoID());
                ReviewStatus reviewStatus = reviewInfo.getReviewStatus();
                if (reviewStatus.equals(ReviewStatus.SUBMITTED) || reviewStatus.equals(ReviewStatus.ACCEPTED)) {
                    readOnly = true;
                }
                userSession.setReviewInfoID(null);
            }
        }
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
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
        if (product == null) {
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

    public List<DisplayReviewQ> getDisplayReviewQs() {
        if (displayReviewQs == null) {
            displayReviewQs = reviewService.getDisplayReviewSum(getReviewInfo());
        }
        return displayReviewQs;
    }

    public void setDisplayReviewQs(List<DisplayReviewQ> displayReviewQs) {
        this.displayReviewQs = displayReviewQs;
    }

    public boolean isReadOnly() {
        if(reviewInfo==null)
            getReviewInfo();
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
