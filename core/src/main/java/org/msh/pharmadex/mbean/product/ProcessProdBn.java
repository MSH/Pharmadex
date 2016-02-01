package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.mbean.UserAccessMBean;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.msh.pharmadex.domain.enums.RegState.FEE;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessProdBn implements Serializable {

    private static final long serialVersionUID = -6299219761842430835L;
    public boolean showCert;
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsService}")
    protected ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{productService}")
    protected ProductService productService;
    protected ProdApplications prodApplications;
    protected Product product;
    protected List<TimeLine> timeLineList;
    protected org.msh.pharmadex.domain.TimeLine timeLine = new org.msh.pharmadex.domain.TimeLine();
    protected FacesContext facesContext = FacesContext.getCurrentInstance();
    protected java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    protected boolean displayVerify = false;
    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{amdmtService}")
    AmdmtService amdmtService;
    @ManagedProperty(value = "#{workspaceDAO}")
    WorkspaceDAO workspaceDAO;
    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{commentService}")
    private CommentService commentService;
    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    @ManagedProperty(value = "#{userAccessMBean}")
    private UserAccessMBean userAccessMBean;
    @ManagedProperty(value = "#{sampleTestService}")
    private SampleTestService sampleTestService;
    @ManagedProperty(value = "#{suspendService}")
    private SuspendService suspendService;
    @ManagedProperty(value = "#{attachmentDAO}")
    private AttachmentDAO attachmentDAO;

    private Applicant applicant;
    private List<Comment> comments;
    private List<Mail> mails;
    private List<ProdAppAmdmt> prodAppAmdmts;
    //    private List<ProdAppChecklist> prodAppChecklists;
    private TimelineModel model;
    private List<Timeline> timelinesChartData;
    private Comment selComment = new Comment();
    private Mail mail = new Mail();
    private String reviewComment;
    private List<Invoice> invoices;
    //    private String prodID;
    private boolean checkReviewStatus = false;
    private int selectedTab;
    private User moderator;
    private boolean displaySample = false;
    private boolean displayClinical = false;
    private boolean displayReviewStatus = false;
    //    private ReviewInfo reviewInfo;
    private SampleTest sampleTest;
    private UploadedFile file;
    private boolean attach;
    private JasperPrint jasperPrint;
    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;
    private User loggedInUser;
    private List<ProdAppLetter> letters;
    private List<ReviewInfo> reviewInfos;
    private boolean registered;
    private boolean prescreened;
    private List<ProdApplications> prevProdApps;
    private List<SuspDetail> suspDetails;
    private List<Attachment> clinicalRevs;

    @PostConstruct
    private void init() {
        loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        mail.setUser(loggedInUser);
        timeLine.setUser(loggedInUser);
        selComment.setUser(loggedInUser);
    }

    public String goProdDetails() {
        System.out.println("Product ID ==" + prodApplications.getId());
        JsfUtils.flashScope().put("prodAppID", prodApplications.getId());
        return "processproddetail";
    }


    public List<RegState> getRegSate() {
        if (prodApplications != null)
            return prodApplicationsService.nextStepOptions(prodApplications.getRegState(), userSession, getCheckReviewStatus());
        return null;
    }

    public List<ReviewInfo> getReviewInfos() {
        if (reviewInfos == null)
            reviewInfos = reviewService.findReviewInfos(prodApplications.getId());
        return reviewInfos;
    }

    public boolean getCheckReviewStatus() {
//        if (prodApplications.getId() == null)
        getProdApplications();
        if (prodApplications != null) {
            if (userAccessMBean.isDetailReview()) {
                for (ReviewInfo ri : getReviewInfos()) {
                    if (ri.getReviewStatus().equals(ReviewStatus.ACCEPTED))
                        checkReviewStatus = true;
                    else
                        checkReviewStatus = false;

                }
            } else {
                for (Review each : getReviews()) {
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
        return reviewService.findReviews(prodApplications.getId());
    }

    public TimelineModel getTimelinesChartData() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        getProdApplications();
        timelinesChartData = new ArrayList<Timeline>();
        Timeline timeline;
        TimelineModel model = new TimelineModel();
        if (timeLineList != null) {
            for (org.msh.pharmadex.domain.TimeLine tm : getTimeLineList()) {
                timeline = new Timeline();
                model.add(new TimelineEvent(resourceBundle.getString(tm.getRegState().getKey()), tm.getStatusDate()));
                timelinesChartData.add(timeline);
            }
        }
        return model;
    }

    public boolean isShowCert() {
        if (prodApplications != null && prodApplications.getRegState() != null && !userSession.isCompany()) {
            if (prodApplications.getRegState().equals(RegState.REGISTERED) || prodApplications.getRegState().equals(RegState.REJECTED))
                showCert = true;
            else
                showCert = false;
        }
        return showCert;
    }

    public void setShowCert(boolean showCert) {
        this.showCert = showCert;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ProdApplications getProdApplications() {
        if (prodApplications == null) {
            initProdApps();
        }
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    private void initProdApps() {
        Long prodAppID = (Long) JsfUtils.flashScope().get("prodAppID");
        if (prodAppID != null) {
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            setFieldValues();
            JsfUtils.flashScope().keep("prodAppID");
        }
    }

    public void setFieldValues() {
        product = prodApplications.getProduct();
        moderator = prodApplications.getModerator();
//        prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
        timeLineList = timelineService.findTimelineByApp(prodApplications.getId());
    }

    public void dateChange() {
        Workspace w = workspaceDAO.findOne((long) 1);
        prodApplications.setRegExpiryDate(JsfUtils.addDate(prodApplications.getRegistrationDate(), w.getProdRegDuration()));

    }

    public String addComment() {
        facesContext = FacesContext.getCurrentInstance();
        selComment.setDate(new Date());
        selComment.setProdApplications(prodApplications);
        selComment.setUser(loggedInUser);
        selComment = commentService.saveComment(selComment);
        comments.add(selComment);
        selComment = new Comment();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("comment_success")));
        return "";
    }

    public void assignModerator() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            prodApplications.setUpdatedDate(new Date());
            prodApplications.setModerator(moderator);
            RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("persist")) {
//            product = productService.updateProduct(product);
                prodApplications = (ProdApplications) retObject.getObj();
                setFieldValues();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("moderator_add_success")));
            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "processprodbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        }


    }

    public String sendToRenew() {
        save();
        Flash flash = JsfUtils.flashScope();
        flash.put("prodID", prodApplications.getProduct().getId());
        flash.put("appID", prodApplications.getApplicant().getApplcntId());
        return "renew";

    }

    public String sendToSuspend(Long suspID) {
        Flash flash = JsfUtils.flashScope();
        flash.put("prodAppID", prodApplications.getId());
        flash.put("suspDetailID", suspID);
        return "suspenddetail";
    }


    public String sendMessage() {
        facesContext = FacesContext.getCurrentInstance();
        mail.setDate(new Date());
        mail.setUser(loggedInUser);
        mail.setProdApplications(prodApplications);
        mailService.sendMail(mail, true);
        mails.add(mail);
        mail = new Mail();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("send_success")));
        return "";
    }

    public String deleteComment(Comment delComment) {
        facesContext = FacesContext.getCurrentInstance();
        comments.remove(delComment);
        String result = commentService.deleteComment(delComment);
        if (result.equals("deleted"))
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("comment_del_success")));
        else
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("comment_del_fail")));
        return "";
    }

    public List<TimeLine> getTimeLineList() {
        if (timeLineList == null)
            timeLineList = timelineService.findTimelineByApp(prodApplications.getId());
        return timeLineList;
    }

    public void setTimeLineList(List<TimeLine> timeLineList) {
        this.timeLineList = timeLineList;
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(TimeLine timeLine) {
        this.timeLine = timeLine;
    }

//    public String submitReview() {
//        facesContext = FacesContext.getCurrentInstance();
//        if (reviewComment.isEmpty()) {
//            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    resourceBundle.getString("global_fail"), resourceBundle.getString("review_comment_empty_valid")));
//            return "";
//        }
//        initProcessor();
//        if (module.getModule1().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
//            module.setModule1SubmitDt(new Date());
//            module.setReview1(reviewComment);
//        }
//        if (module.getModule2().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
//            module.setModule2SubmitDt(new Date());
//            module.setReview2(reviewComment);
//        }
//        if (module.getModule3().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
//            module.setModule3SubmitDt(new Date());
//            module.setReview3(reviewComment);
//        }
//        if (module.getModule4().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
//            module.setModule4SubmitDt(new Date());
//            module.setReview4(reviewComment);
//        }
//
//        if (module.getModule1SubmitDt() != null && module.getModule2SubmitDt() != null && module.getModule3SubmitDt() != null && module.getModule4SubmitDt() != null)
//            module.setComplete(true);
//
//        prodApplicationsService.saveProcessors(module);
//        return "";
//    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            if (prodApplications.isFeeReceived()) {
                timeLine.setRegState(FEE);
                addTimeline();
            }
        }
        if (prodApplications.getRegState().equals(FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                addTimeline();
            }
        }
        setSelectedTab(1);
    }

    public void changeClinicalReviewStatus() {
        logger.error("Inside changeStatusListener");
        if (prodApplications.isClinicalRevReceived() || prodApplications.isClinicalRevVerified()) {
            save();
        }
    }

    public void changeSampleRecieved() {
        logger.error("Inside changeSampleRecieved");
        if (prodApplications.getSampleTestRecieved()!=null&&!prodApplications.getSampleTestRecieved()) {
            save();
        }
    }

    public void initOpenToApp() {
        timeLine = new TimeLine();
        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(loggedInUser);
        timeLine.setRegState(RegState.SAVED);
    }

    public String openToApplicant() {
        facesContext = FacesContext.getCurrentInstance();
        prodApplications.setRegState(timeLine.getRegState());
        RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());
        if (retObject.getMsg().equals("persist")) {
            prodApplications = (ProdApplications) retObject.getObj();
            setFieldValues();
            timeLine.setProdApplications(prodApplications);
            timelineService.saveTimeLine(timeLine);
            timeLineList.add(timeLine);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
        } else {
            facesContext.addMessage(null, new FacesMessage(retObject.getMsg()));
        }
        return "/internal/processprodlist";
    }

    public String addTimeline() {
        facesContext = FacesContext.getCurrentInstance();

        try {

            timeLine.setStatusDate(new Date());
            timeLine.setUser(loggedInUser);
            RetObject paObject = prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());
            prodApplications = (ProdApplications) paObject.getObj();
            timeLine.setProdApplications(prodApplications);

            String retValue = timelineService.validateStatusChange(timeLine);

            if (retValue.equalsIgnoreCase("success")) {
                prodApplications.setRegState(timeLine.getRegState());
                RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());
                if (retObject.getMsg().equals("persist")) {
                    prodApplications = (ProdApplications) retObject.getObj();
                    setFieldValues();
                    timeLine.setProdApplications(prodApplications);
                    timelineService.saveTimeLine(timeLine);
                    timeLineList.add(timeLine);
                    facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
                } else {
                    facesContext.addMessage(null, new FacesMessage(retObject.getMsg()));
                }
            } else if (retValue.equalsIgnoreCase("fee_not_recieved")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("app_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("prod_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), resourceBundle.getString("prod_not_verified")));
            } else if (retValue.equalsIgnoreCase("valid_assign_moderator")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_moderator")));
            } else if (retValue.equalsIgnoreCase("valid_assign_reviewer")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_reviewer")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
        }
        timeLine = new TimeLine();
        return "";  //To change body of created methods use File | Settings | File Templates.
    }

    public void save() {
        try {
            prodApplications = prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedINUserID());
//            product = productService.findProduct(product.getId());
            setFieldValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String registerProduct() {
        facesContext = FacesContext.getCurrentInstance();
        if (!prodApplications.getRegState().equals(RegState.RECOMMENDED)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("register_fail")));
            return "";
        }


        timeLine = new TimeLine();
        timeLine.setRegState(RegState.REGISTERED);
//        prodApplications.setRegistrationDate(new Date());
        prodApplications.setProdRegNo("" + (Math.random() * 100000));
        globalEntityLists.setRegProducts(null);

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(loggedInUser);
        timeLineList.add(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        prodApplications.setActive(true);
//        prodApplications = prodApplicationsService.updateProdApp(prodApplications);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));

        prodApplicationsService.createRegCert(prodApplications);
        timeLine = new TimeLine();
        return null;
    }

    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }

    public User getUser() {
        return loggedInUser;
    }

    public Mail getMail() {
        if (prodApplications != null && prodApplications.getApplicant() != null)
            mail.setMailto(prodApplications.getApplicant().getEmail());
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public boolean isReadyReg() {
        if ((userSession.isAdmin() || userSession.isStaff()) && prodApplications.getRegState().equals(RegState.RECOMMENDED))
            return true;
        else
            return false;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public boolean isRegistered() {
        if (prodApplications != null) {
            if (prodApplications.getRegState().equals(RegState.REGISTERED))
                return true;
        }
        return false;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean getCanRegister() {
        if (userSession.isHead() || userSession.isAdmin()) {
            if (getProdApplications() != null) {
                if (getProdApplications().getRegState().equals(RegState.RECOMMENDED))
                    return true;
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean getCanReject() {
        if (userSession.isHead() || userSession.isAdmin()) {
            if (getProdApplications() != null) {
                if (getProdApplications().getRegState().equals(RegState.NOT_RECOMMENDED))
                    return true;
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<ProdAppAmdmt> getProdAppAmdmts() {
        return prodAppAmdmts;
    }

    public void setProdAppAmdmts(List<ProdAppAmdmt> prodAppAmdmts) {
        this.prodAppAmdmts = prodAppAmdmts;
    }

    public Product getProduct() {
        if (product == null)
            initProdApps();
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Comment getSelComment() {
        return selComment;
    }

    public void setSelComment(Comment selComment) {
        this.selComment = selComment;
    }

    public List<Comment> getComments() {
        if (comments == null)
            comments = commentService.findAllCommentsByApp(prodApplications.getId(), userSession.isCompany());
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public CommentService getCommentService() {
        return commentService;
    }

    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public AmdmtService getAmdmtService() {
        return amdmtService;
    }

    public void setAmdmtService(AmdmtService amdmtService) {
        this.amdmtService = amdmtService;
    }

    public WorkspaceDAO getWorkspaceDAO() {
        return workspaceDAO;
    }

    public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public UserAccessMBean getUserAccessMBean() {
        return userAccessMBean;
    }

    public void setUserAccessMBean(UserAccessMBean userAccessMBean) {
        this.userAccessMBean = userAccessMBean;
    }

    public boolean isDisplayVerify() {
        if (userSession.isAdmin() || userSession.isHead() || userSession.isModerator())
            return true;
        if ((userSession.isStaff())) {
            if (prodApplications != null && (prodApplications.getRegState().equals(RegState.NEW_APPL) || prodApplications.getRegState().equals(RegState.FEE)
                    || prodApplications.getRegState().equals(RegState.FOLLOW_UP)))
                displayVerify = true;
            else
                displayVerify = false;
        }
        return displayVerify;
    }

    public void setDisplayVerify(boolean displayVerify) {
        this.displayVerify = displayVerify;
    }

    public String cancel() {
        facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processProdBn", null);
        return "/public/registrationhome.faces";
    }

    public boolean isDisplaySample() {
        if (prodApplications != null && prodApplications.getProdAppType().equals(ProdAppType.RENEW)) {
            displaySample = false;
        } else {
            if ((userSession.isStaff() || userSession.isModerator() || userSession.isLab())) {
                RegState regState = prodApplications.getRegState();
                if ((prodApplications != null && regState != null)) {
                    if (regState.equals(RegState.SCREENING) || regState.equals(RegState.NEW_APPL) || regState.equals(RegState.FEE)) {
                        displaySample = false;
                    } else {
                        displaySample = true;
                    }
                } else {
                    displaySample = false;
                }
            } else {
                displaySample = false;
            }
        }
        return displaySample;
    }

    public void setDisplaySample(boolean displaySample) {
        this.displaySample = displaySample;
    }

    public SampleTestService getSampleTestService() {
        return sampleTestService;
    }

    public void setSampleTestService(SampleTestService sampleTestService) {
        this.sampleTestService = sampleTestService;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public List<ProdAppLetter> getLetters() {
        if (letters == null) {
            letters = prodApplicationsService.findAllLettersByProdApp(getProdApplications().getId());
        }
        return letters;
    }

    public void setLetters(List<ProdAppLetter> letters) {
        this.letters = letters;
    }

    public StreamedContent fileDownload(ProdAppLetter doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
        return download;
    }

    public boolean isDisplayReviewStatus() {
        if (prodApplications.getRegState().equals(RegState.REVIEW_BOARD))
            displayReviewStatus = true;
        else
            displayReviewStatus = false;
        return displayReviewStatus;
    }

    public void setDisplayReviewStatus(boolean displayReviewStatus) {
        this.displayReviewStatus = displayReviewStatus;
    }

    public boolean isPrescreened() {
        if (prodApplications != null && (prodApplications.getRegState().equals(RegState.NEW_APPL))
                || prodApplications.getRegState().equals(RegState.FOLLOW_UP) || prodApplications.getRegState().equals(RegState.VERIFY))
            prescreened = true;
        else
            displayVerify = false;
        return prescreened;
    }

    public void setPrescreened(boolean prescreened) {
        this.prescreened = prescreened;
    }

    public List<ProdApplications> getPrevProdApps() {
        if (prevProdApps == null) {
            prevProdApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
            for (ProdApplications pa : prevProdApps) {
                if (pa.getId().equals(prodApplications.getId())) {
                    prevProdApps.remove(pa);
                    break;
                }
            }
        }
        return prevProdApps;
    }

    public void setPrevProdApps(List<ProdApplications> prevProdApps) {
        this.prevProdApps = prevProdApps;
    }

    public List<SuspDetail> getSuspDetails() {
        if (suspDetails == null) {
            suspDetails = suspendService.findSuspendByProd(prodApplications.getId());
        }
        return suspDetails;
    }

    public void setSuspDetails(List<SuspDetail> suspDetails) {
        this.suspDetails = suspDetails;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }

    public List<Attachment> getClinicalRevs() {
        if (null != prodApplications && prodApplications.getcRevAttach() != null) {
            clinicalRevs = new ArrayList<Attachment>();
            clinicalRevs.add(attachmentDAO.findOne(prodApplications.getcRevAttach().getId()));
        }
        return clinicalRevs;
    }

    public void setClinicalRevs(List<Attachment> clinicalRevs) {
        this.clinicalRevs = clinicalRevs;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
    }

    public boolean isDisplayClinical() {
        if (prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
            if (userSession.isHead() || userSession.isModerator() || userSession.isAdmin() || userSession.isClinical())
                displayClinical = true;
        } else {
            displayClinical = false;
        }
        return displayClinical;
    }

    public void setDisplayClinical(boolean displayClinical) {
        this.displayClinical = displayClinical;
    }
}
