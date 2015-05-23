/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.omnifaces.util.Faces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ExecSummaryBn implements Serializable {


    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    private ProdApplications prodApplications;
    private Product product;
    private List<ReviewInfo> reviewInfos;
    private boolean readOnly;
    private List<RegState> nextSteps;

    @PostConstruct
    private void init(){
        Long prodAppID = (Long) JsfUtils.flashScope().get("prodAppID");
        if(prodAppID!=null){
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            product = prodApplications.getProduct();
            reviewInfos = reviewService.findReviewInfos(prodAppID);

        }
    }

    public String submit(){
        User user = userService.findUser(userSession.getLoggedINUserID());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");


        String result = prodApplicationsService.submitExecSummary(prodApplications, user, reviewInfos);
        if(result.equals("persist")) {
            JsfUtils.flashScope().put("prodAppID", prodApplications.getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
            return "processreg";
        }else if(result.equals("state_error")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please accept the reviews before submitting the executive summary",""));
            return null;
        }

        return "";
    }

    public String sendToReviewInfo(Long id){
        JsfUtils.flashScope().put("reviewInfoID", id);
        return "/internal/reviewInfo.faces";
    }


    public String cancelReview() {
//        userSession.setReview(null);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("prodAppID", prodApplications.getId());
        userSession.setProdID(prodApplications.getProduct().getId());
        return "/internal/processreg";

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

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public List<ReviewInfo> getReviewInfos() {
        return reviewInfos;
    }

    public void setReviewInfos(List<ReviewInfo> reviewInfos) {
        this.reviewInfos = reviewInfos;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isReadOnly() {
        if(prodApplications.getRegState().equals(RegState.RECOMMENDED)||prodApplications.getRegState().equals(RegState.NOT_RECOMMENDED))
            readOnly = true;
        else
            readOnly = false;
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public List<RegState> getNextSteps() {
        List<RegState> nextSteps = new ArrayList<RegState>();
        nextSteps.add(RegState.RECOMMENDED);
        nextSteps.add(RegState.NOT_RECOMMENDED);
        nextSteps.add(RegState.FOLLOW_UP);
        return nextSteps;
    }

    public void setNextSteps(List<RegState> nextSteps) {
        this.nextSteps = nextSteps;
    }
}
