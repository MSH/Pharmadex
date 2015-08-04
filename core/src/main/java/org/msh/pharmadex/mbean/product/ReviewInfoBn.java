/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.UserAccessMBean;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private UploadedFile file;
    private ReviewInfo reviewInfo;
    private Product product;
    private ProdApplications prodApplications;
    private List<DisplayReviewQ> displayReviewQs;
    private boolean readOnly = false;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private ReviewComment reviewComment;
    private List<ReviewComment> reviewComments;
    private RevDeficiency revDeficiency;
    private List<RevDeficiency> revDeficiencies;
    @ManagedProperty(value = "#{userAccessMBean}")
    private UserAccessMBean userAccessMBean;
    private String revType;
    private boolean priReview;
    private User loggedInUser;

    @PostConstruct
    private void init() {
        if (reviewInfo == null) {
            Long reviewInfoID = (Long) JsfUtils.flashScope().get("reviewInfoID");
            if (reviewInfoID != null) {
                reviewInfo = reviewService.findReviewInfo(reviewInfoID);
                ReviewStatus reviewStatus = reviewInfo.getReviewStatus();
                JsfUtils.flashScope().keep("reviewInfoID");
                if (reviewStatus.equals(ReviewStatus.SUBMITTED) || reviewStatus.equals(ReviewStatus.ACCEPTED)) {
                    readOnly = true;
                }
                reviewComments = reviewInfo.getReviewComments();
            }
            loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        }
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

    public StreamedContent fileDownload(ProdAppLetter doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void initComment() {
        reviewComment = new ReviewComment();
        if (reviewInfo.getReviewComments() == null) {
            reviewInfo.setReviewComments(new ArrayList<ReviewComment>());
        }

        reviewComment.setUser(loggedInUser);
        reviewComment.setDate(new Date());
        reviewComment.setReviewInfo(reviewInfo);
    }

    public void initRevDef() {
        reviewComment = new ReviewComment();
        revDeficiency = new RevDeficiency();
    }

    public void findRevDef(RevDeficiency revDeficiency) {
        this.revDeficiency = revDeficiency;
        reviewComment = new ReviewComment();
        revDeficiency.setAckComment(reviewComment);
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
        reviewInfo.setUpdatedBy(loggedInUser);
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        return "";
    }

    public String reviewerFeedback() {
        reviewInfo.getReviewComments().add(reviewComment);
        reviewInfo.setReviewStatus(ReviewStatus.FEEDBACK);
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("prodAppID", reviewInfo.getProdApplications().getId());
        return "/internal/processreg";
    }

    public String approveReview() {
//        if (reviewInfo.getRecomendType() == null) {
//            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
//        }

        if (!reviewInfo.getReviewStatus().equals(ReviewStatus.SUBMITTED)) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
        }

        reviewComment = reviewInfo.getReviewComments().get(reviewInfo.getReviewComments().size() - 1);
        reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
        reviewInfo.setComment(reviewComment.getComment());
        saveReview();
        userSession.setProdAppID(reviewInfo.getProdApplications().getId());
        userSession.setProdID(reviewInfo.getProdApplications().getProduct().getId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("prodAppID", reviewInfo.getProdApplications().getId());
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
//        if (reviewInfo.getRecomendType() == null) {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please provide recommendation type.");
//            facesContext.addMessage(null, msg);
//            return "";
//        }
        String retValue = reviewService.submitReview(reviewInfo);
        if (retValue.equals("NOT_ANSWERED")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please answer all the questions");
            facesContext.addMessage(null, msg);
            return "";
        } else if (retValue.equals("SAVE")) {
            msg = new FacesMessage(bundle.getString("global.success"));
            facesContext.addMessage(null, msg);
            facesContext.getExternalContext().getFlash().put("prodAppID", reviewInfo.getProdApplications().getId());
            return "processreg";
        }
        return "";
    }

    public String cancelReview() {
//        userSession.setReview(null);
        JsfUtils.flashScope().put("prodAppID", reviewInfo.getProdApplications().getId());
        userSession.setProdID(reviewInfo.getProdApplications().getProduct().getId());
        return "/internal/processreg";

    }

    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (reviewComment.getRecomendType() == null) {
                reviewInfo.setReviewStatus(ReviewStatus.IN_PROGRESS);
                reviewComment.setFinalSummary(false);
            } else {
                if (reviewComment.getRecomendType().equals(RecomendType.RECOMENDED) || reviewComment.getRecomendType().equals(RecomendType.NOT_RECOMENDED)) {
                    if (userAccessMBean.getWorkspace().isSecReview()) {
                        if (userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId()))
                            reviewInfo.setReviewStatus(ReviewStatus.SEC_REVIEW);
                        else if (userSession.getLoggedINUserID().equals(reviewInfo.getSecReviewer().getUserId()))
                            reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
                    } else {
                        reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
                    }
                    reviewComment.setFinalSummary(true);
                }
//                } else if (reviewComment.getRecomendType().equals(RecomendType.FEEDBACK)) {
//                    reviewInfo.setReviewStatus(ReviewStatus.FEEDBACK);
//                    reviewComment.setFinalSummary(false);
//                }
            }
            reviewInfo.setSubmitDate(new Date());

            reviewInfo.getReviewComments().add(reviewComment);
            reviewInfo.setRevDeficiencies(revDeficiencies);
            RetObject retObject = reviewService.submitReviewInfo(reviewInfo);
            if (retObject.getMsg().equals("success")) {
                reviewInfo = (ReviewInfo) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));

            } else if (retObject.getMsg().equals("close_def")) {
                facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
    }

    public String revDefAck() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            reviewComment.setFinalSummary(false);
            reviewInfo.setReviewStatus(ReviewStatus.RFI_RECIEVED);

            reviewInfo.setSubmitDate(new Date());
            revDeficiency.setAckComment(reviewComment);
            revDeficiency.setResolved(true);
            reviewInfo.getReviewComments().add(reviewComment);
            RetObject retObject = reviewService.saveRevDeficiency(revDeficiency);

            if (retObject.getMsg().equals("success")) {
                revDeficiency = (RevDeficiency) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return "";
    }


    public String generateLetter() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
//            reviewComment.setRecomendType(RecomendType.APPLICANT_FEEDBACK);
            revDeficiency.setSentComment(reviewComment);
            revDeficiency.setUser(reviewComment.getUser());
            reviewInfo.setSubmitDate(new Date());
            reviewInfo.getReviewComments().add(reviewComment);
            reviewInfo.setReviewStatus(ReviewStatus.IN_PROGRESS);
            revDeficiency.setReviewInfo(reviewInfo);
            revDeficiency.setCreatedDate(new Date());
            RetObject retObject = reviewService.createDefLetter(revDeficiency);

            if (retObject.getMsg().equals("success")) {
                reviewInfo = (ReviewInfo) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                reviewComments = reviewInfo.getReviewComments();
                revDeficiencies = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return "";
    }

    public ReviewInfo getReviewInfo() {
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
        if (product != null && product.getId() != null) {
            System.out.println("product id == " + product.getId());
        } else {
            reviewInfo = getReviewInfo();
            if (reviewInfo != null) {
                prodApplications = reviewInfo.getProdApplications();
                product = productService.findProduct(reviewInfo.getProdApplications().getProduct().getId());
            }
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
        if (reviewInfo == null)
            getReviewInfo();
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null)
            getProduct();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ReviewComment getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(ReviewComment reviewComment) {
        this.reviewComment = reviewComment;
    }

    public List<ReviewComment> getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(List<ReviewComment> reviewComments) {
        this.reviewComments = reviewComments;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public List<RecomendType> getRevRecomendTypes() {
        List<RecomendType> recomendTypes = new ArrayList<RecomendType>();
        recomendTypes.add(RecomendType.RECOMENDED);
        recomendTypes.add(RecomendType.NOT_RECOMENDED);
        return recomendTypes;
    }

    public List<RevDeficiency> getRevDeficiencies() {
        if (revDeficiencies == null) {
            revDeficiencies = prodApplicationsService.findRevDefByRI(getReviewInfo());
        }
        return revDeficiencies;
    }

    public void setRevDeficiencies(List<RevDeficiency> revDeficiencies) {
        this.revDeficiencies = revDeficiencies;
    }

    public RevDeficiency getRevDeficiency() {
        return revDeficiency;
    }

    public void setRevDeficiency(RevDeficiency revDeficiency) {
        this.revDeficiency = revDeficiency;
    }

    public UserAccessMBean getUserAccessMBean() {
        return userAccessMBean;
    }

    public void setUserAccessMBean(UserAccessMBean userAccessMBean) {
        this.userAccessMBean = userAccessMBean;
    }

    public String getRevType() {
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

    public boolean isPriReview() {
        if (reviewInfo != null && reviewInfo.getReviewer() != null) {
            if (userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId())) {
                if (reviewInfo.getReviewStatus().equals(ReviewStatus.SEC_REVIEW))
                    priReview = false;
                else
                    priReview = true;
            } else {
                priReview = true;
            }
        }

        return priReview;
    }

    public void setPriReview(boolean priReview) {
        this.priReview = priReview;
    }
}
