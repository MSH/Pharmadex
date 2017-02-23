package org.msh.pharmadex.mbean;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UserRole;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public abstract class ProcessPOrderBn implements Serializable {

    protected POrderBase pOrderBase;
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;
    @ManagedProperty(value = "#{userService}")
    protected UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    protected GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{prodApplicationsService}")
    protected ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{timelineServiceET}")
    protected TimelineServiceET timelineServiceET;

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
    protected User curUser;
    protected User responsiblePerson;


    public abstract String newApp();

    public void prepareUpload() {
        pOrderDoc = new POrderDoc();
    }

    @PostConstruct
    private void init(){
        Long userId = userSession.getLoggedINUserID();
        curUser = userService.findUser(userId);
    }

    public void initComment() {
        pOrderComment = new POrderComment();
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
    }


    public StreamedContent fileDownload(POrderDoc doc) {
        InputStream ist=null;
        String fileName = "";
        if (AttachmentService.attStoresInDb()) {
            ist = new ByteArrayInputStream(doc.getFile());
            fileName = doc.getFileName();
        }else{
            fileName = AttachmentService.extractAttFileNameFromBLOBField(doc.getFile());
            if ("".equals(fileName)) return null;
            if ("".equals(fileName)) fileName = doc.getFileName();
            try {
                ist = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), fileName);
        return download;
    }


    private enum Tab {
        home, screen, attach, commenttab;
    }

    public void onTabChange(TabChangeEvent event) {
        facesContext = FacesContext.getCurrentInstance();
        try {
            String tabID = event.getTab().getId();
            Tab selTab = null;
            if (tabID != null && !tabID.equalsIgnoreCase("")) {
                selTab = Tab.valueOf(tabID);
            }

            switch (selTab) {
                case screen:
                    if (pOrderChecklists == null)
                        pOrderChecklists = pOrderService.findPOrderChecklists(pOrderBase);
                    break;
                case attach:
                    if (pOrderDocs == null)
                        pOrderDocs = (ArrayList<POrderDoc>) pOrderService.findPOrderDocs(pOrderBase);
                    break;
                case commenttab:
                    if (pOrderComments == null)
                        pOrderComments = pOrderService.findPOrderComments(pOrderBase);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail")));
        }
    }

    public void feeRecievedListener() {
        try {
            User user = userService.findUser(userSession.getLoggedINUserID());
            pOrderBase.setUpdatedBy(user);
            pOrderBase.setProcessor(user);
            RetObject retObject = pOrderService.NotifyFeeRecieved(pOrderBase);
            if (!retObject.getMsg().equals("persist")) {
                FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), retObject.getMsg());
            } else {
                FacesMessage msg = new FacesMessage(resourceBundle.getString("global.success"), retObject.getMsg());
                pOrderBase = (POrderBase) retObject.getObj();
                initVariables();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
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
        String fileName="";
        try {
            if(pOrderDoc==null)
                pOrderDoc = new POrderDoc();
            if (AttachmentService.attStoresInDb()){
                pOrderDoc.setFileName(file.getFileName());
                pOrderDoc.setFile(IOUtils.toByteArray(file.getInputstream()));
                fileName = file.getFileName();
            }else{
                fileName = AttachmentService.save(file.getInputstream(),file.getFileName());
                pOrderDoc.setFile(IOUtils.toByteArray(fileName));
            }
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();
        }
        pOrderDoc.setFileName(fileName);
        pOrderDoc.setContentType(file.getContentType());
        pOrderDoc.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        pOrderDoc.setRegState(AmdmtState.NEW_APPLICATION);
    }

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        RetObject retObject = pOrderService.updatePOrder(pOrderBase,curUser);
        return "/public/processpiporderlist.faces";
    }

    public abstract String withdraw();

    public abstract void addDocument();

    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            saveApp();
            pOrderBase.setState(AmdmtState.SUBMITTED);
            pOrderBase.setReviewState(pOrderComment.getRecomendType());
            if ((pOrderBase instanceof PIPOrder) || (pOrderBase instanceof PurOrder))
                timelineServiceET.createTimeLineEvent(pOrderBase,pOrderBase.getReviewState().equals(RecomendType.RECOMENDED) ? RegState.RECOMMENDED : RegState.NOT_RECOMMENDED ,curUser,"Review finished");
            enterComment();
            String retObject = newApp();
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
        }
    }

    public void addComment() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            if (userSession.isCsd())
                pOrderBase.setReviewState(RecomendType.FEEDBACK);
            else
                pOrderBase.setReviewState(RecomendType.COMMENT);
            pOrderComment.setRecomendType(pOrderBase.getReviewState());
            enterComment();
            String retObject = newApp();
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
        }
    }

    protected abstract void setPOrderForComment();

    public void initApprove() {
        pOrderComment = new POrderComment();
//        pOrderComment.set RecomendType(RecomendType.ACCEPTED);
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
        setPOrderForComment();
        if (pOrderComments == null)
            pOrderComments = pOrderService.findPOrderComments(pOrderBase);
        pOrderComments.add(pOrderComment);
    }

    public void initReject() {
        pOrderComment = new POrderComment();
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
    }

    public String approveOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (pOrderBase.getReviewState().equals(RecomendType.RECOMENDED)) {
                pOrderBase.setState(AmdmtState.APPROVED);
                timelineServiceET.createTimeLineEvent(pOrderBase,RegState.RECOMMENDED,curUser,"Order approved");
                pOrderBase.setApprovalDate(new Date());
                Date expDate = JsfUtils.addDate(new Date(), globalEntityLists.getWorkspace().getPipRegDuration());

                if(pOrderBase instanceof PurOrder) {
                    for (PurProd pp : ((PurOrder) pOrderBase).getPurProds()) {
                        ProdApplications pa = prodApplicationsService.findActiveProdAppByProd(pp.getProduct().getId());
                        if (expDate.after(pa.getRegExpiryDate())) {
                            expDate = pa.getRegExpiryDate();
                        }
                    }
                }
                pOrderBase.setExpiryDate(expDate);

                enterComment();
                return newApp();
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "You cannot approve an application that was not recommended." +
                        " Please send it back to the person who did not recommended it explaining the reasons by adding a comment."));
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
            return "";
        }
    }

    private void enterComment() {
        pOrderComments = pOrderService.findPOrderComments(pOrderBase);
        if (pOrderComments == null)
            pOrderComments = new ArrayList<POrderComment>();
        pOrderComment.setExternal(false);
        pOrderComments.add(pOrderComment);
        if (pOrderBase instanceof PIPOrder) {
            pOrderComment.setPipOrder((PIPOrder) pOrderBase);
            ((PIPOrder) pOrderBase).setpOrderComments(pOrderComments);
        } else if (pOrderBase instanceof PurOrder) {
            pOrderComment.setPurOrder((PurOrder) pOrderBase);
            ((PurOrder) pOrderBase).setpOrderComments(pOrderComments);
        }
    }

    public String rejectOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (pOrderBase.getReviewState().equals(RecomendType.NOT_RECOMENDED)) {
                timelineServiceET.createTimeLineEvent(pOrderBase,RegState.RECOMMENDED,curUser,"Order rejected");
                pOrderBase.setState(AmdmtState.REJECTED);
                pOrderBase.setApprovalDate(new Date());
                enterComment();
                return newApp();
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "You cannot reject an application that was recommended." +
                        " Please send it back to the person who recommended it explaining the rejection reason by adding a comment."));
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
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
        return pOrderDocs;
    }

    public void setpOrderDocs(ArrayList<POrderDoc> pOrderDocs) {
        this.pOrderDocs = pOrderDocs;
    }

    public POrderComment getpOrderComment() {
        if (pOrderComment == null)
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
        if (userSession.isStaff()) {
            if (pOrderBase.getState() != null) {
                if (pOrderBase.getState().equals(AmdmtState.NEW_APPLICATION) ||
                        pOrderBase.getState().equals(AmdmtState.REVIEW) || pOrderBase.getState().equals(AmdmtState.FEEDBACK)
                        || pOrderBase.getState().equals(AmdmtState.SAVED)) {
                    displayReview = true;
                } else if (pOrderBase.getReviewState().equals(RecomendType.FEEDBACK)) {
                    displayReview = true;
                } else {
                    displayReview = false;
                }
            } else {
                displayReview = true;
            }
        } else {
            displayReview = false;
        }
        return displayReview;
    }

    public void setDisplayReview(boolean displayReview) {
        this.displayReview = displayReview;
    }

    public boolean isDisplayReviewComment() {
        if (pOrderBase.getReviewState()==null) return  false;
        if (pOrderBase.getState() != null) {
            if (pOrderBase.getState().equals(AmdmtState.NEW_APPLICATION) ||
                    pOrderBase.getState().equals(AmdmtState.REVIEW) || pOrderBase.getState().equals(AmdmtState.FEEDBACK))
                displayReviewComment = true;
            else if(pOrderBase.getReviewState().equals(RecomendType.FEEDBACK))
                displayReviewComment = true;
            else
                displayReviewComment = false;
            if (userSession.isCsd() || userSession.isCst()) {
                if (pOrderBase.getState().equals(AmdmtState.SUBMITTED)) {
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
        if (pOrderBase != null && pOrderBase.getState() != null) {
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

    public List<RecomendType> getRecommendTypes() {
        List<RecomendType> recomendTypes = new ArrayList<RecomendType>();
        recomendTypes.add(RecomendType.RECOMENDED);
        recomendTypes.add(RecomendType.NOT_RECOMENDED);

        return recomendTypes;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public User getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(User responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }
}
