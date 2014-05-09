package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
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

    @Autowired
    WebSession webSession;

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
    private List<ProdAppChecklist> checklists;
    private List<Review> reviews;
    private Review review = new Review();

    private StatusUser module;
    private boolean registered;
    private String reviewComment;
    private List<Invoice> invoices;
    private String prodID;

    private boolean checkReviewStatus = false;


    @PostConstruct
    private void init() {
        mail.setUser(userSession.getLoggedInUserObj());
        timeLine.setUser(userSession.getLoggedInUserObj());
        selComment.setUser(userSession.getLoggedInUserObj());
        review = new Review();
        review.setUser(new User());
    }

    public List<RegState> getRegSate() {
        return prodApplicationsService.nextStepOptions(prodApplications.getRegState(), userSession, getModule());
    }

    public String findReview() {
        review = reviewDAO.findByUser_UserIdAndProdApplications_Id(userSession.getLoggedInUserObj().getUserId(), prodApplications.getId());
        webSession.setReview(review);
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
        if (prodApplications == null || webSession.getProdApplications() != null) {
            initProdApps();
            webSession.setProdApplications(null);
        }
        return prodApplications;
    }

    private void initProdApps() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        prodID = params.get("id");

        if (prodID == null || prodID.equals(""))
            prodID = "" + webSession.getProduct().getId();
        webSession.setProduct(null);

        product = productService.findProduct(Long.valueOf(prodID));
        prodApplications = this.product.getProdApplications();
        applicant = this.product.getApplicant();
        prodAppAmdmts = prodApplications.getProdAppAmdmts();
        comments = prodApplications.getComments();
        invoices = prodApplications.getInvoices();
        mails = prodApplications.getMails();
        timeLineList = prodApplications.getTimeLines();
        checklists = prodApplications.getProdAppChecklists();
        reviews = prodApplications.getReviews();
        initProcessor();
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
//        prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));
//        this.prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));

    }

    public String addComment() {
        selComment.setDate(new Date());
        selComment.setProdApplications(prodApplications);
        selComment.setUser(userSession.getLoggedInUserObj());
        selComment = commentService.saveComment(selComment);
        comments.add(selComment);
        selComment = new Comment();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Comment successfully added"));
        return "";
    }


    public void assignProcessor() {
        if (module == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding processor."));
        }
        module.setProdApplications(prodApplications);
        module.setAssignDate(new Date());

        if (!prodApplicationsService.saveProcessors(module).equalsIgnoreCase("success"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding processor."));
    }

    public void initProcessorAdd() {
        review = new Review();
        review.setUser(new User());
        review.setProdApplications(prodApplications);
        review.setAssignDate(new Date());
    }

    public void assignReviewer() {
        if (review == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding processor."));
        }
        review.setProdApplications(prodApplications);
        review.setAssignDate(new Date());

        if (!prodApplicationsService.saveReviewers(review).equalsIgnoreCase("success"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding processor."));
        else
            reviews.add(review);

        review = new Review();
    }


    public void assignModerator() {
        prodApplications.setUpdatedDate(new Date());

        if (!prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedInUserObj()).equalsIgnoreCase("persisted"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding processor."));
    }


    public void initProcessor() {
        if (prodApplications != null) {
            module = prodApplicationsService.findStatusUser(prodApplications.getId());
            if (module == null) {
                module = new StatusUser(prodApplications);
                module.setModule1(new User());
                module.setModule2(new User());
                module.setModule3(new User());
                module.setModule4(new User());
            } else {
                if (module.getModule1() != null)
                    module.setModule1(userService.findUser(module.getModule1().getUserId()));
                if (module.getModule2() != null)
                    module.setModule2(userService.findUser(module.getModule2().getUserId()));
                if (module.getModule3() != null)
                    module.setModule3(userService.findUser(module.getModule3().getUserId()));
                if (module.getModule4() != null)
                    module.setModule4(userService.findUser(module.getModule4().getUserId()));
            }
        }
    }


    public String sendMessage() {
        mail.setDate(new Date());
        mail.setUser(userSession.getLoggedInUserObj());
        mail.setProdApplications(prodApplications);
        mailService.sendMail(mail, true);
        mails.add(mail);
        mail = new Mail();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Message sent successfully"));
        return "";
    }

    public String newComment() {
        System.out.println("Inside new comment");
        return "/home.faces";
    }

    public String deleteComment(Comment delComment) {
        comments.remove(delComment);
        String result = commentService.deleteComment(delComment);
        if (result.equals("deleted"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Comment successfully deleted "));
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Comment delete failed"));
        return "";
    }

    public String deleteReview(Review review) {
        reviews.remove(review);
        try {
            reviewDAO.delete(review);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Comment successfully deleted "));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Comment delete failed"));
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

    public String addTimeline() {
        if (timeLine.getRegState().equals(RegState.FEE) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (!prodApplications.isFeeReceived()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail:", "Fee not received"));
                timeLine = new TimeLine();
                return "";
            }
        } else if (timeLine.getRegState().equals(RegState.VERIFY) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (!prodApplications.isApplicantVerified()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "applicant not verified"));
                timeLine = new TimeLine();
                return "";
            } else if (!prodApplications.isProductVerified() || prodApplications.getRegState() == RegState.REGISTERED) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "product not verified"));
                timeLine = new TimeLine();
                return "";

            }
        }

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        timelineService.saveTimeLine(timeLine);
        timeLineList.add(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        product.setRegState(timeLine.getRegState());
        prodApplicationsService.updateProdApp(prodApplications);
        productService.updateProduct(prodApplications.getProd());
        timeLine = new TimeLine();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Status successfully changed"));
        return "";  //To change body of created methods use File | Settings | File Templates.
    }

    public void save() {
        prodApplications.setProdAppChecklists(checklists);
        prodApplications.setReviews(reviews);
        prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedInUserObj());
    }

    public String registerProduct() {
        if (!prodApplications.getRegState().equals(RegState.RECOMMENDED)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "Cannot Register product, Status needs to be updated"));
            return "";
        }
        timeLine = new TimeLine();
        timeLine.setRegState(RegState.REGISTERED);
        prodApplications.setRegistrationDate(new Date());
        product.setRegNo("" + (Math.random() * 100000));
        globalEntityLists.setRegProducts(null);
        addTimeline();
        prodApplicationsService.createRegCert(prodApplications);
        return "";
    }

    public String submitReview() {
        if (reviewComment.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Review comment can not be empty."));
            return "";
        }
        initProcessor();
        if (module.getModule1().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
            module.setModule1SubmitDt(new Date());
            module.setReview1(reviewComment);
        }
        if (module.getModule2().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
            module.setModule2SubmitDt(new Date());
            module.setReview2(reviewComment);
        }
        if (module.getModule3().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
            module.setModule3SubmitDt(new Date());
            module.setReview3(reviewComment);
        }
        if (module.getModule4().getUserId() == userSession.getLoggedInUserObj().getUserId()) {
            module.setModule4SubmitDt(new Date());
            module.setReview4(reviewComment);
        }

        if (module.getModule1SubmitDt() != null && module.getModule2SubmitDt() != null && module.getModule3SubmitDt() != null && module.getModule4SubmitDt() != null)
            module.setComplete(true);

        prodApplicationsService.saveProcessors(module);
        return "";
    }

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

    public List<ProdAppChecklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<ProdAppChecklist> checklists) {
        this.checklists = checklists;
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

    public StatusUser getModule() {
        if (module == null)
            initProcessor();
        return module;
    }

    public void setModule(StatusUser module) {
        this.module = module;
    }

    public void setCheckReviewStatus(boolean checkReviewStatus) {
        this.checkReviewStatus = checkReviewStatus;
    }

    public boolean getCheckReviewStatus() {
        if (prodApplications == null)
            getProdApplications();
        if (userSession.isModerator() && getModule().isComplete())
            checkReviewStatus = true;
        else
            checkReviewStatus = false;
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
}
