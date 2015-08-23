/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.SuspensionStatus;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.SuspendService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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
public class SuspendDetailBn implements Serializable {


    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{suspendService}")
    private SuspendService suspendService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(SuspendDetailBn.class);
    private UploadedFile file;
    private SuspDetail suspDetail;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private SuspComment suspComment;
    private List<SuspComment> suspComments;
    private List<ProdAppLetter> prodAppLetters;
    private ProdAppLetter prodAppLetter;
    private User moderator;
    private User reviewer;
    private User loggedInUser;
    private boolean showSuspend;

    @PostConstruct
    private void init() {
        if (suspDetail == null) {
            Long suspDetailID = (Long) JsfUtils.flashScope().get("suspDetailID");
            if (suspDetailID != null && suspDetailID > 0) {
                suspDetail = suspendService.findSuspendDetail(suspDetailID);
                suspComments = suspDetail.getSuspComments();
                prodAppLetters = suspDetail.getProdAppLetters();
                prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
                JsfUtils.flashScope().keep("suspDetailID");
//                if (reviewStatus.equals(ReviewStatus.SUBMITTED) || reviewStatus.equals(ReviewStatus.ACCEPTED)) {
//                    readOnly = true;
//                }
            } else {
                Long prodAppID = (Long) JsfUtils.flashScope().get("prodAppID");
                if (prodAppID != null) {
                    prodApplications = prodApplicationsService.findProdApplications(prodAppID);
                    suspComments = new ArrayList<SuspComment>();
                    suspDetail = new SuspDetail(prodApplications, suspComments);
                    suspDetail.setSuspensionStatus(SuspensionStatus.REQUESTED);
                    suspDetail.setCreatedBy(getLoggedInUser());
                }
            }
        }
    }

    @Transactional
    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (suspComments == null) {
                suspComments = new ArrayList<SuspComment>();
            }

            suspComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
            suspComment.setDate(new Date());
            suspComment.setSuspDetail(suspDetail);
            suspComments.add(suspComment);
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));

            RetObject retObject = suspendService.saveSuspend(suspDetail);
            if (retObject.getMsg().equals("success")) {
                suspDetail = (SuspDetail) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));

            } else if (retObject.getMsg().equals("close_def")) {
                facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
    }

    private User getLoggedInUser() {
        if (loggedInUser == null) {
            loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        }
        return loggedInUser;
    }

    public void assignModerator() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(getLoggedInUser());
            suspDetail.setModerator(moderator);
            RetObject retObject = suspendService.saveSuspend(suspDetail);
            if (retObject.getMsg().equals("persist")) {
                suspDetail = (SuspDetail) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("moderator_add_success")));
            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "suspendetailbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("processor_add_error")));
        }
    }

    public void assignReviewer() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(getLoggedInUser());
            suspDetail.setReviewer(reviewer);
            RetObject retObject = suspendService.saveSuspend(suspDetail);
            if (retObject.getMsg().equals("persist")) {
                suspDetail = (SuspDetail) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("moderator_add_success")));
            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "suspendetailbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("processor_add_error")));
        }
    }

    public StreamedContent fileDownload(ProdAppLetter doc) {
        ProdAppLetter prodAppLetter = suspDetail.getProdAppLetters().get(0);
        InputStream ist = new ByteArrayInputStream(prodAppLetter.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, prodAppLetter.getContentType(), prodAppLetter.getFileName());
        return download;
    }

    public void initComment() {
        suspComment = new SuspComment();
    }

    public String submitSuspend() {
        facesContext = FacesContext.getCurrentInstance();
        if (userSession.isHead()) {
            if (suspDetail.getModerator() == null) {
                FacesMessage fm = new FacesMessage("Please specify a moderator to process the request.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            if (suspComments.size() == 0) {
                FacesMessage fm = new FacesMessage("Please specify comment for Team Leader.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
        }

        if (userSession.isModerator()) {
            if (suspDetail.getReviewer() == null) {
                FacesMessage fm = new FacesMessage("Please specify a Assessor to process the request.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.REQUESTED)) {
                suspDetail.setSuspensionStatus(SuspensionStatus.ASSIGNED);
            } else if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.SUBMIT)) {
                suspDetail.setSuspensionStatus(SuspensionStatus.RESULT);
            }
        }

        if (userSession.isReviewer()) {
            if (suspDetail.getFinalSumm() == null || suspDetail.equals("")) {
                FacesMessage fm = new FacesMessage("Please provide final comment.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
            } else {
                suspDetail.setSuspensionStatus(SuspensionStatus.SUBMIT);
            }
        }
        saveSuspend();
        return "processprodlist";
    }

    public List<RegState> getDecisionType() {
        List<RegState> decisionType = new ArrayList<RegState>();
        decisionType.add(RegState.SUSPEND);
        decisionType.add(RegState.CANCEL);
        decisionType.add(RegState.REGISTERED);
        return decisionType;
    }


    public String saveSuspend() {
        RetObject retObject = suspendService.saveSuspend(suspDetail);
        suspDetail = (SuspDetail) retObject.getObj();
        return "";
    }

    public String suspendProduct() {
        if (suspDetail.getSuspStDate() == null && suspDetail.getDecisionDate() == null && suspDetail.getDecision() == null)
            return "";

        RetObject retObject = suspendService.suspendProduct(suspDetail, getLoggedInUser());
        globalEntityLists.setRegProducts(null);

        if (retObject.getMsg().equals("persist")) {
            return cancelSuspendDetail();

        } else {
            FacesMessage fm = new FacesMessage(bundle.getString("global_fail"));
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, fm);
            return null;
        }

    }

    public void prepareUpload() {
        prodAppLetter = new ProdAppLetter();
    }


    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();

        file = event.getFile();
        try {
            if (prodAppLetter == null)
                prodAppLetter = new ProdAppLetter();
            prodAppLetter.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        pOrderDoc.setPipOrder(get);
        prodAppLetter.setFileName(file.getFileName());
        prodAppLetter.setContentType(file.getContentType());
        prodAppLetter.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        prodAppLetter.setRegState(prodApplications.getRegState());
//        userSession.setFile(file);

    }

    public void addDocument() {
        if (prodAppLetters == null)
            prodAppLetters = new ArrayList<ProdAppLetter>();

//        file = userSession.getFile();
        prodAppLetter.setSuspDetail(suspDetail);
//        getpOrderDocDAO().save(getpOrderDoc());
        prodAppLetters.add(prodAppLetter);
        suspDetail.setProdAppLetters(prodAppLetters);
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public String cancelSuspendDetail() {
//        userSession.setReview(null);
        JsfUtils.flashScope().put("prodAppID", suspDetail.getProdApplications().getId());
        userSession.setProdID(suspDetail.getProdApplications().getProduct().getId());
        return "/internal/processreg";
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

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public List<ProdAppLetter> getProdAppLetters() {
        return prodAppLetters;
    }

    public void setProdAppLetters(List<ProdAppLetter> prodAppLetters) {
        this.prodAppLetters = prodAppLetters;
    }

    public ProdAppLetter getProdAppLetter() {
        return prodAppLetter;
    }

    public void setProdAppLetter(ProdAppLetter prodAppLetter) {
        this.prodAppLetter = prodAppLetter;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }

    public SuspDetail getSuspDetail() {
        return suspDetail;
    }

    public void setSuspDetail(SuspDetail suspDetail) {
        this.suspDetail = suspDetail;
    }

    public SuspComment getSuspComment() {
        return suspComment;
    }

    public void setSuspComment(SuspComment suspComment) {
        this.suspComment = suspComment;
    }

    public List<SuspComment> getSuspComments() {
        return suspComments;
    }

    public void setSuspComments(List<SuspComment> suspComments) {
        this.suspComments = suspComments;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isShowSuspend() {
        if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT))
            showSuspend = true;
        else
            showSuspend = false;
        return showSuspend;
    }

    public void setShowSuspend(boolean showSuspend) {
        this.showSuspend = showSuspend;
    }
}
