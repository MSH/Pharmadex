package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ProcessProdBn implements Serializable {

    private static final long serialVersionUID = -6299219761842430835L;
    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private MailService mailService;

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    AmdmtService amdmtService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewDAO reviewDAO;

    private ProdApplications prodApplications;
    private Product product;
    private Applicant applicant;
    private List<Comment> comments;
    private List<TimeLine> timeLineList;
    private List<Mail> mails;
    private List<Company> companies;
    private List<ProdAppAmdmt> prodAppAmdmts;
    private TimelineModel model;

    private List<Timeline> timelinesChartData;

    private Comment selComment = new Comment();
    private org.msh.pharmadex.domain.TimeLine timeLine = new org.msh.pharmadex.domain.TimeLine();
    private Mail mail = new Mail();
    private boolean readyReg;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<Review> reviews;
    private Review review = new Review();

    private StatusUser module;
    private boolean registered;
    private String reviewComment;
    private List<Invoice> invoices;
    private String prodID;
    private List<ProdInn> prodInns;

    private boolean checkReviewStatus = false;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private int selectedTab;

    @PostConstruct
    private void init() {
        mail.setUser(userSession.getLoggedInUserObj());
        timeLine.setUser(userSession.getLoggedInUserObj());
        selComment.setUser(userSession.getLoggedInUserObj());
        review = new Review();
        review.setUser(new User());
    }

    public List<RegState> getRegSate() {
        return prodApplicationsService.nextStepOptions(prodApplications.getRegState(), userSession, getCheckReviewStatus());
    }

    public String findReview() {
        review = reviewDAO.findByUser_UserIdAndProdApplications_Id(userSession.getLoggedInUserObj().getUserId(), prodApplications.getId());
        userSession.setReview(review);
        return "/internal/review";
    }

    public TimelineModel getTimelinesChartData() {
        getProdApplications();
        timelinesChartData = new ArrayList<Timeline>();
        Timeline timeline;
        TimelineModel model = new TimelineModel();
        for (org.msh.pharmadex.domain.TimeLine tm : getTimeLineList()) {
            timeline = new Timeline();
            model.add(new TimelineEvent(tm.getRegState().name(), tm.getStatusDate()));
            timelinesChartData.add(timeline);
        }
        return model;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ProdApplications getProdApplications() {
        if (prodApplications == null || userSession.getProdApplications() != null) {
            initProdApps();
            userSession.setProdApplications(null);
        }
        return prodApplications;
    }

    private void initProdApps() {
        facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        prodID = params.get("id");

        if (prodID == null || prodID.equals(""))
            prodID = "" + userSession.getProduct().getId();
        userSession.setProduct(null);

        product = productService.findProduct(Long.valueOf(prodID));
        setFieldValues();
//        initProcessor();
    }

    private void setFieldValues() {
        prodApplications = product.getProdApplications();
        prodInns = product.getInns();
        applicant = product.getApplicant();
        prodAppAmdmts = prodApplications.getProdAppAmdmts();
        comments = prodApplications.getComments();
        invoices = prodApplications.getInvoices();
        mails = prodApplications.getMails();
        timeLineList = prodApplications.getTimeLines();
        prodAppChecklists = prodApplications.getProdAppChecklists();
        reviews = prodApplications.getReviews();
    }

    @Autowired
    WorkspaceDAO workspaceDAO;
    public void dateChange() {
        Workspace w = workspaceDAO.findOne((long) 1);
        prodApplications.setRegExpiryDate(JsfUtils.addDate(prodApplications.getRegistrationDate(), w.getProdRegDuration()));

    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
//        prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));
//        this.prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));

    }

    public String addComment() {
        facesContext = FacesContext.getCurrentInstance();
        selComment.setDate(new Date());
        selComment.setProdApplications(prodApplications);
        selComment.setUser(userSession.getLoggedInUserObj());
        selComment = commentService.saveComment(selComment);
        comments.add(selComment);
        selComment = new Comment();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("comment_success")));
        return "";
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

    public void initProcessorAdd() {
        review = new Review();
        review.setUser(new User());
        review.setProdApplications(prodApplications);
        review.setAssignDate(new Date());
    }

    public void assignReviewer() {
        facesContext = FacesContext.getCurrentInstance();
        if (review == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        }
        review.setProdApplications(prodApplications);
        review.setAssignDate(new Date());

        if (!prodApplicationsService.saveReviewers(review).equalsIgnoreCase("success"))
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        else
            reviews.add(review);

        review = new Review();
    }


    public void assignModerator() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            prodApplications.setUpdatedDate(new Date());
            prodApplications = prodApplicationsService.updateProdApp(prodApplications);
            product = prodApplications.getProd();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("moderator_add_success")));
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "processprodbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        }


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


    public String sendMessage() {
        facesContext = FacesContext.getCurrentInstance();
        mail.setDate(new Date());
        mail.setUser(userSession.getLoggedInUserObj());
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

    public String deleteReview(Review review) {
        reviews.remove(review);
        facesContext = FacesContext.getCurrentInstance();
        try {
            reviewDAO.delete(review);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    resourceBundle.getString("global.success"), resourceBundle.getString("comment_del_success")));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("comment_del_fail")));
            e.printStackTrace();
        }
        return "";
    }


    public List<TimeLine> getTimeLineList() {
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

    public void changeStatusListener(){
        logger.error("Inside changeStatusListener");
        if(prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            addTimeline();
        }
        if(prodApplications.getRegState().equals(RegState.FEE)){
            if(prodApplications.isApplicantVerified()&&prodApplications.isProductVerified()&&prodApplications.isDossierReceived()){
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
            timeLine.setUser(userSession.getLoggedInUserObj());
            String retValue = timelineService.validateStatusChange(timeLine);

            if(retValue.equalsIgnoreCase("success")) {
                timeLineList.add(timeLine);
                prodApplications.setRegState(timeLine.getRegState());
                product.setRegState(timeLine.getRegState());
                prodApplications = prodApplicationsService.updateProdApp(prodApplications);
                product = productService.findProduct(prodApplications.getProd().getId());
                setFieldValues();
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
            }else if(retValue.equalsIgnoreCase("fee_not_recieved")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            }else if(retValue.equalsIgnoreCase("app_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            }else if(retValue.equalsIgnoreCase("prod_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), resourceBundle.getString("prod_not_verified")));
            }else if(retValue.equalsIgnoreCase("valid_assign_moderator")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_moderator")));
            }else if(retValue.equalsIgnoreCase("valid_assign_reviewer")) {
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
//        prodApplications.setProdAppChecklists(prodAppChecklists);
//        prodApplications.setReviews(reviews);
        try {
            product = productService.updateProduct(product);
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
        product.setRegNo("" + (Math.random() * 100000));
        globalEntityLists.setRegProducts(null);

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        timeLineList.add(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        product.setRegState(timeLine.getRegState());
//        prodApplications = prodApplicationsService.updateProdApp(prodApplications);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));

        prodApplicationsService.createRegCert(prodApplications);
        timeLine = new TimeLine();
        return "";
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

    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }

    public User getUser() {
        return userSession.getLoggedInUserObj();
    }

    public Mail getMail() {
        mail.setMailto(prodApplications.getProd().getApplicant().getEmail());
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

    public List<ProdAppChecklist> getProdAppChecklists() {
        return prodAppChecklists;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public List<User> getProcessors() {
        return userService.findProcessors();
    }

    public List<User> getModerators() {
        return userService.findModerators();
    }

    public List<User> completeProcessorList(String query) {
        return JsfUtils.completeSuggestions(query, getProcessors());
    }

    public List<User> completeModeratorList(String query) {
        return JsfUtils.completeSuggestions(query, getModerators());
    }

//    public StatusUser getModule() {
//        if (module == null)
//            initProcessor();
//        return module;
//    }

    public void setModule(StatusUser module) {
        this.module = module;
    }

    public void setCheckReviewStatus(boolean checkReviewStatus) {
        this.checkReviewStatus = checkReviewStatus;
    }

    public boolean getCheckReviewStatus() {
//        if (prodApplications.getId() == null)
            getProdApplications();
        for (Review each : prodApplications.getReviews()) {
            if (!each.isSubmitted()) {
                checkReviewStatus = false;
                break;
            } else {
                checkReviewStatus = true;
            }
        }
        return checkReviewStatus;

    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public boolean isRegistered() {
        if (prodApplications.getRegState().equals(RegState.REGISTERED))
            return true;
        else
            return false;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean getCanRegister() {
        if (userSession.isHead() || userSession.isAdmin()) {
            if (getProdApplications().getRegState().equals(RegState.RECOMMENDED))
                return true;
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
            getProdApplications();
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
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Review> getReviews() {
        if(reviews == null ) {
            if(product!=null) {
                product = productService.findProduct(product.getId());
                setFieldValues();
//                initProcessor();
            }
        }
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
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
}
