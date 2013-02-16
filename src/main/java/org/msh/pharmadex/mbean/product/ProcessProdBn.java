package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.*;
import org.primefaces.extensions.model.timeline.DefaultTimeLine;
import org.primefaces.extensions.model.timeline.DefaultTimelineEvent;
import org.primefaces.extensions.model.timeline.Timeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ProcessProdBn {

    private ProdApplications prodApplications;
    private List<Inn> selectedInns;
    private List<Comment> comments;
    private List<org.msh.pharmadex.domain.TimeLine> timeLineList;
    private List<Mail> mails;
    private List<Atc> selectedAtcs;
    private List<Company> companies;
    private List<ProdAppChecklist> prodAppChecklists;

    private List<org.primefaces.extensions.model.timeline.Timeline> timelinesChartData;




    private Comment selComment = new Comment();
    private org.msh.pharmadex.domain.TimeLine timeLine = new org.msh.pharmadex.domain.TimeLine();
    private Mail mail = new Mail();
    private boolean readyReg;


    @Autowired
    private UserSession userSession;

    @Autowired
    private DosageFormService dosageFormService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ProductService productService;

    @PostConstruct
    private void init(){
        mail.setUser(userSession.getLoggedInUserObj());
        timeLine.setUser(userSession.getLoggedInUserObj());
        selComment.setUser(userSession.getLoggedInUserObj());
    }

    public List<RegState> getRegSate() {
        return Arrays.asList(nextStepOptions(prodApplications.getRegState()));
    }

    private RegState[] nextStepOptions(RegState regState) {
        RegState[] options = null;
        switch (regState){
            case NEW_APPL:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.FEE;
                break;
            case FEE:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.VERIFY;
                break;
            case VERIFY:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.SCREENING;
                break;
            case SCREENING:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.REVIEW_BOARD;
                break;
            case REVIEW_BOARD:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.RECOMMENDED;
                break;
            case RECOMMENDED:
                options = new RegState[1];
                options[0] = RegState.REJECTED;
                break;
            case REGISTERED:
                options = new RegState[2];
                options[0] = RegState.DISCONTINUED;
                options[1] = RegState.XFER_APPLICANCY;
                break;
            case FOLLOW_UP:
                options = new RegState[7];
                options[0] = RegState.FEE;
                options[1] = RegState.VERIFY;
                options[2] = RegState.SCREENING;
                options[3] = RegState.REVIEW_BOARD;
                options[4] = RegState.SCREENING;
                options[5] = RegState.REVIEW_BOARD;
                options[6] = RegState.DEFAULTED;
                break;
        }
        return options;


    }

    public List<Timeline> getTimelinesChartData() {
        timelinesChartData = new ArrayList<Timeline>();
        Timeline timeline;
        for(org.msh.pharmadex.domain.TimeLine tm : getTimeLineList()){
            timeline = new DefaultTimeLine("prodtl", "Product Timeline");
            timeline.addEvent(new DefaultTimelineEvent(tm.getRegState().name(), tm.getStatusDate()));
            timelinesChartData.add(timeline);
        }
        return timelinesChartData;
    }

    public Comment getSelComment() {
        return selComment;
    }

    public void setSelComment(Comment selComment) {
        this.selComment = selComment;
    }

    public List<Comment> getComments() {
            comments = commentService.findAllCommentsByApp(prodApplications.getId(), userSession.isCompany());
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getDosForm(){
        System.out.println("items fetched == "+dosageFormService.findAllDosForm().size());
//        dosageFormService.findAllDosForm().size();
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
        prodApplications = prodApplicationsService.findProdApplications(prodApplications.getId());
//        prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));
//        this.prodApplications.setProd(productService.findProductById(prodApplications.getProd().getId()));

    }

    public List<ProdInn> getSelectedInns() {
        if(prodApplications!=null)
            return prodApplications.getProd().getInns();
        else
            return null;
    }

    public void setSelectedInns(List<Inn> selectedInns) {
        this.selectedInns = selectedInns;
    }

    public List<Atc> getSelectedAtcs() {
        if(prodApplications!=null)
            return prodApplications.getProd().getAtcs();
        else
            return null;
    }

    public void setSelectedAtcs(List<Atc> selectedAtcs) {
        this.selectedAtcs = selectedAtcs;
    }

    public String addComment() {
        selComment.setDate(new Date());
        selComment.setProdApplications(prodApplications);
        selComment.setUser(userSession.getLoggedInUserObj());
        selComment= commentService.saveComment(selComment);
        selComment = new Comment();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Comment successfully added"));

        return "";
    }

    public String sendMessage() {
        mail.setDate(new Date());
        mail.setUser(userSession.getLoggedInUserObj());
        mail.setProdApplications(prodApplications);
        mailService.sendMail(mail, true);
        mail = new Mail();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Message sent successfully"));
        return "";
    }

    public String newComment(){
        System.out.println("Inside new comment");
        return "/home.faces";
    }

    public String deleteComment(Comment delComment) {
        comments.remove(delComment);
        String result = commentService.deleteComment(delComment);
        if(result.equals("deleted"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "Comment successfully deleted "));
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Comment delete failed"));
        return "";
    }

    public List<TimeLine> getTimeLineList() {
        return timelineService.findTimelineByApp(prodApplications.getId());
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
        if(timeLine.getRegState().equals(RegState.FEE)||timeLine.getRegState().equals(RegState.REGISTERED)){
            if(!prodApplications.isFeeReceived()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail:","Fee not received"));
                timeLine = new TimeLine();
                return "";
            }
        }else if(timeLine.getRegState().equals(RegState.VERIFY)||timeLine.getRegState().equals(RegState.REGISTERED)){
            if(!prodApplications.isApplicantVerified()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "applicant not verified"));
                timeLine = new TimeLine();
                return "";
            }else if(!prodApplications.isProductVerified()||prodApplications.getRegState()==RegState.REGISTERED){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "product not verified"));
                timeLine = new TimeLine();
                return "";

            }
        }

        timeLine.setProdApplications(prodApplications);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        timelineService.saveTimeLine(timeLine);
        prodApplications.setRegState(timeLine.getRegState());
        prodApplicationsService.updateProdApp(prodApplications);
        timeLine = new TimeLine();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Status successfully changed"));
        return "";  //To change body of created methods use File | Settings | File Templates.
    }

    public String registerProduct() {
        if(!prodApplications.getRegState().equals(RegState.RECOMMENDED)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "Cannot Register product, Status needs to be updated"));
            return "";
        }
        timeLine = new TimeLine();
        timeLine.setRegState(RegState.REGISTERED);
        prodApplications.setRegistrationDate(new Date());
        prodApplications.getProd().setRegNo("" + (Math.random() * 100000));
        return addTimeline();
    }

    public List<Mail> getMails() {
        return mailService.findAllMailSent(prodApplications.getId());
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
        if(prodApplications!=null)
            return prodApplications.getProd().getCompanies();
        else
            return null;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        if(prodApplications!=null)
            return prodApplications.getProdAppChecklists();
        else
            return null;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public boolean isReadyReg() {
        if((userSession.isAdmin()||userSession.isStaff())&&prodApplications.getRegState().equals(RegState.RECOMMENDED))
            return true;
        else
            return false;
    }

    public void setReadyReg(boolean readyReg) {
        this.readyReg = readyReg;
    }
}
