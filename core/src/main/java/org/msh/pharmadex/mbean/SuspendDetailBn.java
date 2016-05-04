/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.service.*;
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
import javax.management.relation.RoleStatus;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
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

    @ManagedProperty(value = "#{suspendServiceET}")
    private SuspendService suspendService;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{prodAppLetterDAO}")
    private ProdAppLetterDAO prodAppLetterDAO;

    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;

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
    private boolean closed=false;
    private String moderSummary;
    private String moderDecision;
    private ReviewInfo review;
    private ReviewComment curReviewComment;
    private JasperPrint jasperPrint;

    @PostConstruct
    private void init() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            String suspID = facesContext.getExternalContext().getRequestParameterMap().get("suspDetailID");
            if (suspID==null){// this is new suspension record
                Long prodAppID = Long.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("prodAppID"));
                if (prodAppID != null) {
                    prodApplications = prodApplicationsService.findActiveProdAppByProd(prodAppID);
                    product = prodApplications.getProduct();
                    suspComments = new ArrayList<SuspComment>();
                    suspDetail = new SuspDetail(prodApplications, suspComments);
                    suspDetail.setSuspensionStatus(SuspensionStatus.REQUESTED);
                    suspDetail.setCreatedBy(getLoggedInUser());
                }
            }else{
                if (suspID != null && !suspID.equals("")) {
                    getLoggedInUser();
                    Long suspDetailID = Long.valueOf(suspID);
                    suspDetail = suspendService.findSuspendDetail(suspDetailID);
                    suspComments = suspDetail.getSuspComments();
                    prodAppLetters = prodAppLetterDAO.findByProdApplications_Id(suspDetailID);
                    prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
                    product = prodApplications.getProduct();
                    if (product.getId()==null) return;
                    if (suspDetail.getReviewer()!=null){
                        checkReviewStatus(suspDetail.getReviewer().getUserId(), prodApplications.getId());
                        review = reviewService.findReviewInfoByUserAndProdApp(suspDetail.getReviewer().getUserId(), prodApplications.getId());
                    }
                    isClosed();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void checkReviewStatus(long reviewerId, long appId){
        try {
            review = reviewService.findReviewInfoByUserAndProdApp(reviewerId, appId);
            List<ReviewComment> lst = review.getReviewComments();
            if (review.getReviewStatus().equals(SuspensionStatus.FEEDBACK)) return;
            if (null != review) {
                if (review.getReviewStatus().equals(ReviewStatus.ASSIGNED))
                    suspDetail.setSuspensionStatus(SuspensionStatus.IN_PROGRESS);
                else if (review.getReviewStatus().equals(ReviewStatus.SUBMITTED)
                        ||review.getReviewStatus().equals(ReviewStatus.ACCEPTED)){
                    if (!suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT)){
                        suspDetail.setSuspensionStatus(SuspensionStatus.SUBMIT);
                    }
                }
            }
        }catch (Exception e){
            //nothing to do
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
            if (suspComment.getComment()==null || suspComment.getComment().isEmpty()){
                String mes = bundle.getString("comment_required");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mes, ""));
            }
            suspComments.add(suspComment);
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));
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
//            RetObject retObject = suspendService.saveSuspend(suspDetail);
//            if (retObject.getMsg().equals("persist")) {
//                suspDetail = (SuspDetail) retObject.getObj();
//                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("moderator_add_success")));
//            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "suspendetailbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("processor_add_error")));
        }
    }

    public String reviewFeedback() {
        suspDetail.setSuspensionStatus(SuspensionStatus.FEEDBACK);
        suspendService.saveSuspend(suspDetail);
        return "";
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
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
        return download;
    }

    /**
     * Returns all suspension letter
     * @return
     */
    public List<ProdAppLetter> getLetters(){
        List<ProdAppLetter> prodAppLetters = prodAppLetterDAO.findByProdApplications_Id(prodApplications.getId());
        List<ProdAppLetter> res = null;
        if (prodAppLetters!=null){
            if (prodAppLetters.size()>0){
                for(int i=0;i<prodAppLetters.size();i++){
                    ProdAppLetter letter = prodAppLetters.get(i);
                    if ((letter.getLetterType()==LetterType.SUSP_NOTIF_LETTER)  || (letter.getLetterType()==LetterType.CANCELLATION_LETTER) || (letter.getLetterType()==LetterType.CANCELLATION_SENDER_LETTER)) {
                        if (res==null) {res = new ArrayList<ProdAppLetter>();}
                        res.add(letter);
                    }
                }
            }
        }
        return res;
    }

    public List<ReviewInfo> getReviewInfo(){
        User user = suspDetail.getReviewer();
        List<ReviewInfo> res = null;
        if (user!=null){
            res = suspendService.findReviewList(user.getUserId(),suspDetail.getProdApplications().getId());
        }
        return res;
    }

    public void initComment() {
        suspComment = new SuspComment();
    }


/*
      public void submitSuspendStatus(int status){
        if (status==1){
            suspDetail.setDecision(RegState.CANCEL);
        }if (status==2){
            suspDetail.setDecision(RegState.SUSPEND);
        }else if (status==3){
            suspDetail.setDecision(RegState.REGISTERED);
        }
        submitSuspend();
    }
*/

    /**
     * Searches for review comment for current user, if it missing, creates it
     */
    public void initApproval(){
        curReviewComment = reviewService.findSuspendReviewComment(review,loggedInUser);
        if (curReviewComment==null){
            curReviewComment = reviewService.createSuspendReviewComment(review,loggedInUser);
        }
    }

    /**
     * Main suspend procedure, action depends of document status and user position
     * @return
     */
    public String submitSuspend() {
        facesContext = FacesContext.getCurrentInstance();
        String errorMsg="";
        if (userSession.isHead()) {//head user (director)
            if (suspDetail.getModerator() == null) {
                FacesMessage fm = new FacesMessage("Please specify a moderator to process the request.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            if (suspComments.size() == 0) {// head comment are mandatory
                FacesMessage fm = new FacesMessage("Please specify comment for Team Leader.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            RetObject retObject = suspendService.submitHead(suspDetail, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("error")) {
                FacesMessage fm = new FacesMessage("Error");
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
                return "";
            }
        }

        if (userSession.isModerator()) {//moderator (team leader) role...
            boolean errorFound = false;
            if (suspDetail.getReviewer() == null) {//assesor assigment required
                errorMsg=bundle.getString("susp_assesorReqired");
                FacesMessage fm = new FacesMessage(errorMsg);
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
                errorFound=true;
            }
            if (!suspendService.isCommentsExists(suspDetail,getLoggedInUser())){
                //moderators comment are mandatory
                errorMsg = userSession.getLoggedInUser()+" "+ bundle.getString("comment_required");
                FacesMessage fm = new FacesMessage(errorMsg);
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
                errorFound=true;
            }
            if (errorFound) return "";
            try {
                RetObject retObject = suspendService.submitModeratorComment(suspDetail, userSession.getLoggedINUserID());
            } catch (SQLException e) {
                FacesMessage fm = new FacesMessage(e.getMessage());
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                e.printStackTrace();
            } catch (JRException e) {
                FacesMessage fm = new FacesMessage(e.getMessage());
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                e.printStackTrace();
            }
        }

        if (userSession.isReviewer()) {//reviewer role...
            if (suspDetail.getFinalSumm() == null || suspDetail.equals("")) {
                FacesMessage fm = new FacesMessage("Please provide final comment.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
            }
            RetObject retObject = suspendService.submitReview(suspDetail, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("error")) {
                FacesMessage fm = new FacesMessage("Error");
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
            } else {
                suspDetail = (SuspDetail) retObject.getObj();
            }
        }
        return "internal/processcancellist";
    }

    public List<RegState> getDecisionType() {
        List<RegState> decisionType = new ArrayList<RegState>();
        decisionType.add(RegState.SUSPEND);
        decisionType.add(RegState.CANCEL);
        decisionType.add(RegState.REGISTERED);
        return decisionType;
    }

    public String suspendProduct() {
        facesContext = FacesContext.getCurrentInstance();
        if (suspDetail.getSuspStDate() == null && suspDetail.getDecisionDate() == null && suspDetail.getDecision() == null)
            return "";

        RetObject retObject = null;
        try {
            retObject = suspendService.suspendProduct(suspDetail, getLoggedInUser());

            if (retObject.getMsg().equals("persist")) {
                return "/internal/processreg";
            } else {
                FacesMessage fm = new FacesMessage(bundle.getString("global_fail"));
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
        } catch (JRException e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
        }
        return null;

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

        prodAppLetters.add(prodAppLetter);
        suspDetail.setProdAppLetters(prodAppLetters);
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

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

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
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
        if (suspDetail != null && suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT))
            showSuspend = true;
        else
            showSuspend = false;

        return showSuspend;
    }


    public boolean isClosed() {
        closed=false;
        String state = prodApplications.getRegState().getKey();
        if (state.equalsIgnoreCase("RegState.CANCEL"))
            return true;
        return closed;
    }

    /**
     * Determine visibility of Submit button for moderator, depends of status
     * @param no - buttin number
     * @return
     */
    public boolean showSubmitButtonNo(int no){
        if (suspDetail==null) return false;
        if (no==1) {//moderator, before reviewing by assesor
            if (!(userSession.isModerator()||(userSession.isHead()))) return false;
            if (!(suspDetail.getSuspensionStatus().equals(SuspensionStatus.SUBMIT)
                    ||suspDetail.getSuspensionStatus().equals(SuspensionStatus.REQUESTED)
                    ||suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT)))
                return true;
            else
                return false;
        }else if (no==2){//Review finished, team leader or head should approve assessment and send it to head
            if (!(userSession.isModerator()||userSession.isHead())) return false;
            if (suspDetail.getSuspensionStatus().equals(SuspensionStatus.SUBMIT)||suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT))
                return true;
            else
                return false;
        }
        return false;
    }

    public List<RecomendType> getRevRecomendTypes() {
        List<RecomendType> recomendTypes = new ArrayList<RecomendType>();
        recomendTypes.add(RecomendType.REGISTER);
        recomendTypes.add(RecomendType.SUSPEND);
        recomendTypes.add(RecomendType.CANCEL);
        return recomendTypes;
    }

    public String submitAproveDecision(){
        SuspensionStatus oldStatus = suspDetail.getSuspensionStatus();
        SuspensionStatus suspStatus = suspendService.submitApprove(suspDetail,review,curReviewComment.getRecomendType(),getLoggedInUser().getUserId());
        if (suspStatus.equals(oldStatus)){
//            String statusMsg=bundle.getString("SuspensionStatusChanged") + " to " + suspStatus.getKey();
//            FacesMessage fm = new FacesMessage(statusMsg);
//            fm.setSeverity(FacesMessage.SEVERITY_INFO);
//            facesContext.addMessage(null, fm);
            suspDetail.setSuspensionStatus(suspStatus);
        }
        if (userSession.isHead())
            suspDetail.setFinalSumm(curReviewComment.getComment());
        else if (userSession.isHead())
            suspDetail.setModeratorSumm(curReviewComment.getComment());
        suspDetail.setDecision(curReviewComment.getRecomendType());
        suspendService.saveSuspend(suspDetail);
        return submitSuspend();
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setShowSuspend(boolean showSuspend) {
        this.showSuspend = showSuspend;
    }

    public ProdAppLetterDAO getProdAppLetterDAO() {
        return prodAppLetterDAO;
    }

    public void setProdAppLetterDAO(ProdAppLetterDAO prodAppLetterDAO) {
        this.prodAppLetterDAO = prodAppLetterDAO;
    }

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public ReviewComment getCurReviewComment() {
        return curReviewComment;
    }

    public void setCurReviewComment(ReviewComment curReviewComment) {
        this.curReviewComment = curReviewComment;
    }

    public String getModerSummary() {
        return moderSummary;
    }

    public void setModerSummary(String moderSummary) {
        this.moderSummary = moderSummary;
    }

    public String getModerDecision() {
        return moderDecision;
    }

    public void setModerDecision(String moderDecision) {
        this.moderDecision = moderDecision;
    }

}
