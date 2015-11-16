package org.msh.pharmadex.mbean;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
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
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
public abstract class ProcessPOrderBn implements Serializable{

    protected POrderBase pOrderBase;
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;
    @ManagedProperty(value = "#{userService}")
    protected UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    protected GlobalEntityLists globalEntityLists;
    protected boolean displayReview;
    protected boolean displayReviewComment;
    protected List<POrderChecklist> pOrderChecklists;
    protected ArrayList<POrderDoc> pOrderDocs;
    protected POrderComment pOrderComment;
    protected List<POrderComment> pOrderComments;
    protected boolean openAPP;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private Logger logger = LoggerFactory.getLogger(ProcessPOrderBn.class);
    private User applicantUser;
    private Applicant applicant;
    private UploadedFile file;
    private POrderDoc pOrderDoc;
    private boolean showWithdrawn;

    public abstract void init();

    public abstract String newApp();

    public void prepareUpload() {
        pOrderDoc = new POrderDoc();
    }

    public void initComment(){
        pOrderComment = new POrderComment();
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
    }

    public StreamedContent fileDownload(POrderDoc doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void feeRecievedListener() {
        logger.error("Inside feeRecievedListener");
        User user = userService.findUser(userSession.getLoggedINUserID());
        pOrderBase.setUpdatedBy(user);
        pOrderBase.setProcessor(user);
        RetObject retObject = pOrderService.NotifyFeeRecieved(pOrderBase);
        if(!retObject.getMsg().equals("persist")){
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), retObject.getMsg());
        }else{
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global.success"), retObject.getMsg());
            pOrderBase = (POrderBase) retObject.getObj();
            initVariables();

        }

    }

    public abstract void initVariables();


        public void deleteDoc(POrderDoc attach) {
        try {
            facesContext = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_delete"), attach.getFileName() + resourceBundle.getString("is_deleted"));
            pOrderService.delete(pOrderDoc);
            pOrderDocs = null;
            facesContext.addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), attach.getFileName() + resourceBundle.getString("cannot_delte"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        try {
            if(pOrderDoc==null)
                pOrderDoc = new POrderDoc();
            pOrderDoc.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        pOrderDoc.setPipOrder(get);
        pOrderDoc.setFileName(file.getFileName());
        pOrderDoc.setContentType(file.getContentType());
        pOrderDoc.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        pOrderDoc.setRegState(AmdmtState.NEW_APPLICATION);
//        userSession.setFile(file);
    }

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        RetObject retObject = pOrderService.updatePIPOrder(pOrderBase);
        return "/public/processpiporderlist.faces";
    }

    public String withdraw() {
        pOrderBase.setState(AmdmtState.FEEDBACK);
        pOrderComment.setPurOrder((PurOrder) pOrderBase);
        pOrderComment.setExternal(true);
        pOrderComments = ((PurOrder) pOrderBase).getpOrderComments();
        if (pOrderComments == null)
            pOrderComments = new ArrayList<POrderComment>();
        pOrderComments.add(pOrderComment);
        return saveApp();
    }

    public abstract void addDocument();

    public void submitComment(){
        if(pOrderComments==null) {
            pOrderComments = new ArrayList<POrderComment>();
        }
        setPOrderForComment();
        pOrderBase.setState(AmdmtState.SUBMITTED);
        pOrderComment.setDate(new Date());
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderBase.setReviewState(pOrderComment.getRecomendType());
        pOrderComments.add(pOrderComment);
        String retObject = newApp();
    }

    public void addComment(){
        if(pOrderComments==null) {
            pOrderComments = new ArrayList<POrderComment>();
        }
        setPOrderForComment();
        pOrderComment.setDate(new Date());
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        if(userSession.isCsd())
            pOrderBase.setReviewState(RecomendType.FEEDBACK);
        else
            pOrderBase.setReviewState(RecomendType.COMMENT);
        pOrderComment.setRecomendType(pOrderBase.getReviewState());
        pOrderComments.add(pOrderComment);
        String retObject = newApp();
    }

    protected abstract void setPOrderForComment();

    public void initApprove(){
        pOrderComment = new POrderComment();
//        pOrderComment.set RecomendType(RecomendType.ACCEPTED);
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
        setPOrderForComment();
        pOrderComments.add(pOrderComment);
    }

    public void initReject(){
        pOrderComment = new POrderComment();
//        pOrderComment.setRecomendType(RecomendType.ACCEPTED);
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
        setPOrderForComment();
        pOrderComments.add(pOrderComment);
    }

    public String approveOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext,"msgs");
        if(pOrderBase.getReviewState().equals(RecomendType.RECOMENDED)) {
//            pOrderBase.setReviewState(RecomendType.ACCEPTED);
            pOrderBase.setState(AmdmtState.APPROVED);
            pOrderBase.setApprovalDate(new Date());
            pOrderBase.setExpiryDate(JsfUtils.addDate(new Date(), globalEntityLists.getWorkspace().getPipRegDuration()));
            return newApp();
        }else{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"),"Invalid Operation!!!" ));
            return "";
        }
    }

    public String rejectOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext,"msgs");
        if(pOrderBase.getReviewState().equals(RecomendType.NOT_RECOMENDED)) {
//            pOrderBase.setReviewState(RecomendType.ACCEPTED);
            pOrderBase.setState(AmdmtState.REJECTED);
            pOrderBase.setApprovalDate(new Date());
            return newApp();
        }else{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "Invalid Operation!!!"));
            return "";
        }

    }


    public abstract String cancel();

    public POrderBase getpOrderBase() {
        return pOrderBase;
    }

    public void setpOrderBase(POrderBase pOrderBase) {
        this.pOrderBase = pOrderBase;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
    }

    public List<POrderChecklist> getpOrderChecklists() {
        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public POrderDoc getpOrderDoc() {
        return pOrderDoc;
    }

    public void setpOrderDoc(POrderDoc pOrderDoc) {
        this.pOrderDoc = pOrderDoc;
    }

    public ArrayList<POrderDoc> getpOrderDocs() {
        if (pOrderDocs == null) {
            pOrderDocs = (ArrayList<POrderDoc>) getpOrderService().findPOrderDocs(pOrderBase);
        }
        return pOrderDocs;
    }

    public void setpOrderDocs(ArrayList<POrderDoc> pOrderDocs) {
        this.pOrderDocs = pOrderDocs;
    }

    public POrderComment getpOrderComment() {
        if(pOrderComment==null)
            pOrderComment = new POrderComment();
        return pOrderComment;
    }

    public void setpOrderComment(POrderComment pOrderComment) {
        this.pOrderComment = pOrderComment;
    }

    public List<POrderComment> getpOrderComments() {
        return pOrderComments;
    }

    public void setpOrderComments(ArrayList<POrderComment> pOrderComments) {
        this.pOrderComments = pOrderComments;
    }

    public boolean isDisplayReview() {
        if(userSession.isStaff()) {
            if (pOrderBase.getState() != null) {
                if (pOrderBase.getState().equals(AmdmtState.NEW_APPLICATION)||
                        pOrderBase.getState().equals(AmdmtState.REVIEW)||pOrderBase.getState().equals(AmdmtState.FEEDBACK)) {
                    displayReview = true;
                }else if(pOrderBase.getReviewState().equals(RecomendType.FEEDBACK)) {
                    displayReview = true;
                }else {
                    displayReview = false;
                }
            } else {
                displayReview = true;
            }
        }else{
            displayReview = false;
        }
        return displayReview;
    }

    public void setDisplayReview(boolean displayReview) {
        this.displayReview = displayReview;
    }

    public boolean isDisplayReviewComment() {
            if (pOrderBase.getState() != null) {
                if (pOrderBase.getState().equals(AmdmtState.NEW_APPLICATION)||
                        pOrderBase.getState().equals(AmdmtState.REVIEW) || pOrderBase.getState().equals(AmdmtState.FEEDBACK))
                    displayReviewComment = true;
                else
                    displayReviewComment = false;
                if(userSession.isCsd()){
                    if(pOrderBase.getState().equals(AmdmtState.SUBMITTED)){
                        displayReviewComment = true;
                    }
                }
            }
        return displayReviewComment;
    }

    public void setDisplayReviewComment(boolean displayReviewComment) {
        this.displayReviewComment = displayReviewComment;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public boolean isOpenAPP() {
        if(pOrderBase!=null&&pOrderBase.getState()!=null){
            if (pOrderBase.getState().equals(AmdmtState.NEW_APPLICATION) || pOrderBase.getState().equals(AmdmtState.REVIEW) || pOrderBase.getState().equals(AmdmtState.FEEDBACK))
            openAPP = true;
            else {
                openAPP = false;
            }
        }
        return openAPP;
    }

    public void setOpenAPP(boolean openAPP) {
        this.openAPP = openAPP;
    }

    public boolean isShowWithdrawn() {
        if (pOrderBase != null && pOrderBase.getState() != null) {
            if (pOrderBase.getState().equals(AmdmtState.WITHDRAWN) || pOrderBase.getState().equals(AmdmtState.APPROVED)
                    || pOrderBase.getState().equals(AmdmtState.REJECTED))
                showWithdrawn = false;
            else
                showWithdrawn = true;
        } else {
            showWithdrawn = false;
        }
        return showWithdrawn;
    }

    public void setShowWithdrawn(boolean showWithdrawn) {
        this.showWithdrawn = showWithdrawn;
    }
}
