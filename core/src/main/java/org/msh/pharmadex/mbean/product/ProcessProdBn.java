package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.*;
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

    private Applicant applicant;
    private List<Comment> comments;
    private List<Mail> mails;
    private List<Company> companies;
    private List<ProdAppAmdmt> prodAppAmdmts;
    private List<ForeignAppStatus> foreignAppStatuses;
    private List<ProdAppChecklist> prodAppChecklists;
    private TimelineModel model;
    private List<Timeline> timelinesChartData;
    private Comment selComment = new Comment();
    private Mail mail = new Mail();
    private boolean readyReg;
    private StatusUser module;
    private boolean registered;
    private String reviewComment;
    private List<Invoice> invoices;
//    private String prodID;
    private List<ProdInn> prodInns;
    private boolean checkReviewStatus = false;
    private int selectedTab;
    private User moderator;
    private boolean displayVerify = false;
    private boolean displaySample = false;
    private boolean displayReviewStatus = false;
//    private ReviewInfo reviewInfo;
    private SampleTest sampleTest;
    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;
    private UploadedFile file;
    private boolean attach;
    private JasperPrint jasperPrint;
    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;
    private User loggedInUser;
    private List<ProdAppLetter> letters;
    private List<ReviewInfo> reviewInfos;

    @PostConstruct
    private void init() {
        loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        mail.setUser(loggedInUser);
        timeLine.setUser(loggedInUser);
        selComment.setUser(loggedInUser);
    }

    public List<RegState> getRegSate() {
        if(prodApplications!=null)
            return prodApplicationsService.nextStepOptions(prodApplications.getRegState(), userSession, getCheckReviewStatus());
        return null;
    }

    public List<ReviewInfo> getReviewInfos() {
        if(reviewInfos ==null)
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
        if (prodApplications != null && prodApplications.getRegState() != null) {
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


//    public void assignProcessor() {
//        facesContext = FacesContext.getCurrentInstance();
//        if (module == null) {
//            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
//        }
//        module.setProdApplications(prodApplications);
//        module.setAssignDate(new Date());
//
//        if (!prodApplicationsService.saveProcessors(module).equalsIgnoreCase("success"))
//            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
//    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
//        prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));
//        this.prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));

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
        prodInns = product.getInns();

//        prodAppAmdmts = prodApplications.getProdAppAmdmts();
        moderator = prodApplications.getModerator();
        foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
        prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
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

//    public void initProcessor() {
//        if (prodApplications != null) {
//            module = prodApplicationsService.findStatusUser(prodApplications.getId());
//            if (module == null) {
//                module = new StatusUser(prodApplications);
//                module.setModule1(new User());
//                module.setModule2(new User());
//                module.setModule3(new User());
//                module.setModule4(new User());
//            } else {
//                if (module.getModule1() != null)
//                    module.setModule1(userService.findUser(module.getModule1().getUserId()));
//                if (module.getModule2() != null)
//                    module.setModule2(userService.findUser(module.getModule2().getUserId()));
//                if (module.getModule3() != null)
//                    module.setModule3(userService.findUser(module.getModule3().getUserId()));
//                if (module.getModule4() != null)
//                    module.setModule4(userService.findUser(module.getModule4().getUserId()));
//            }
//        }
//    }

    public void assignModerator() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            prodApplications.setUpdatedDate(new Date());
            prodApplications.setModerator(moderator);
            product = productService.updateProduct(product);
//            prodApplications = prodApplicationsService.updateProdApp(prodApplications);
//            product = prodApplications.getProd();
            setFieldValues();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("moderator_add_success")));
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "processprodbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        }


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
        if(timeLineList == null)
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

    public String addTimeline() {
        facesContext = FacesContext.getCurrentInstance();

        try {

            timeLine.setProdApplications(prodApplications);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(loggedInUser);
            String retValue = timelineService.validateStatusChange(timeLine);

            if (retValue.equalsIgnoreCase("success")) {
                prodApplications.setRegState(timeLine.getRegState());
                RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());
                if (retObject.getMsg().equals("persist")) {
                    prodApplications = (ProdApplications) retObject.getObj();
                    setFieldValues();
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void save() {
        try {
            prodApplications = prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedINUserID());
            product = productService.findProduct(product.getId());
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
        return "";
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
        if(prodApplications!=null&&prodApplications.getApplicant()!=null)
            mail.setMailto(prodApplications.getApplicant().getEmail());
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public boolean isReadyReg() {
        if ((userSession.isAdmin() || userSession.isStaff()) && prodApplications.getRegState().equals(RegState.RECOMMENDED))
            return true;
        else
            return false;
    }

    public void setReadyReg(boolean readyReg) {
        this.readyReg = readyReg;
    }

    public void setModule(StatusUser module) {
        this.module = module;
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

    public List<ProdInn> getProdInns() {
        return prodInns;
    }

    public void setProdInns(List<ProdInn> prodInns) {
        this.prodInns = prodInns;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
        return foreignAppStatuses;
    }

    public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
        this.foreignAppStatuses = foreignAppStatuses;
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
            if (prodApplications != null && (prodApplications.getRegState().equals(RegState.NEW_APPL)||prodApplications.getRegState().equals(RegState.FEE)
                    ||prodApplications.getRegState().equals(RegState.FOLLOW_UP)))
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
        if ((userSession.isStaff() || userSession.isModerator() || userSession.isLab())) {
            if ((prodApplications != null && prodApplications.getRegState() != null && prodApplications.getRegState().ordinal() > 3))
                displaySample = true;
            else
                displaySample = false;
        } else {
            displaySample = false;
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

//    public StreamedContent fileDownload() {
//        byte[] file1 = getSampleTest().getFile();
//        InputStream ist = new ByteArrayInputStream(file1);
//        StreamedContent download = new DefaultStreamedContent(ist);
////        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
//        return download;
//    }

//    public void handleFileUpload() {
//        FacesMessage msg;
//        facesContext = FacesContext.getCurrentInstance();
//        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
//
//        if (file != null) {
//            msg = new FacesMessage(resourceBundle.getString("global.success"), file.getFileName() + resourceBundle.getString("upload_success"));
//            facesContext.addMessage(null, msg);
//            try {
//                getSampleTest().setFile(IOUtils.toByteArray(file.getInputstream()));
//                saveSample();
//            } catch (IOException e) {
//                msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        } else {
//            msg = new FacesMessage(resourceBundle.getString("upload_fail"));
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }
//
//    }

//    public void generateSampleRequestLetter() throws JRException, IOException {
//        facesContext = FacesContext.getCurrentInstance();
//        if (!getProdApplications().getRegState().equals(RegState.VERIFY)) {
//            facesContext.addMessage(null, new FacesMessage("You can only issue a Sample Request letter after you have received the fee and verified the dossier for completeness"));
//        }
//        Product product = productService.findProduct(getProduct().getId());
//        jasperPrint = reportService.generateSampleRequest(product, loggedInUser);
//        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        httpServletResponse.addHeader("Content-disposition", "attachment; filename=sample_req_letter.pdf");
//        httpServletResponse.setContentType("application/pdf");
//        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
//        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
//        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
////        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
////        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
//
//        saveSample();
//    }

//    public void saveSample() {
//        save();
//        SampleTest sampleTest1 = getSampleTest();
//        sampleTest1.setLetterGenerated(true);
//        sampleTest1.setProdApplications(prodApplications);
//        sampleTest1.setUser(loggedInUser);
//        RetObject retObject = sampleTestService.saveSample(sampleTest);
//        if (retObject.getMsg().equals("persist")) {
//            sampleTest1 = (SampleTest) retObject.getObj();
//        } else {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error saving sample"));
//        }
//    }
//
//    public SampleTest getSampleTest() {
//        if (sampleTest == null) {
//            sampleTest = sampleTestService.findSampleForProd(getProdApplications().getId());
//            if (sampleTest == null)
//                sampleTest = new SampleTest();
//        }
//        return sampleTest;
//    }
//
//    public void setSampleTest(SampleTest sampleTest) {
//        this.sampleTest = sampleTest;
//    }

//    public boolean isAttach() {
//        if (getSampleTest() != null) {
//            if (getSampleTest().getFile() != null && getSampleTest().getFile().length > 0)
//                return true;
//            else
//                return false;
//        }
//        return false;
//    }
//
//    public void setAttach(boolean attach) {
//        this.attach = attach;
//    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
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

    public List<ProdAppChecklist> getProdAppChecklists() {
        return prodAppChecklists;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public List<ProdAppLetter> getLetters() {
        if(letters == null){
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
        if(prodApplications.getRegState().equals(RegState.REVIEW_BOARD))
            displayReviewStatus = true;
        else
            displayReviewStatus = false;
        return displayReviewStatus;
    }

    public void setDisplayReviewStatus(boolean displayReviewStatus) {
        this.displayReviewStatus = displayReviewStatus;
    }
}
