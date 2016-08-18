package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.BackLog;
import org.msh.pharmadex.service.ProdApplicationsServiceET;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static javax.faces.context.FacesContext.getCurrentInstance;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnET implements Serializable {
    @ManagedProperty(value = "#{processProdBn}")
    public ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    public UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    public ProdApplicationsServiceET prodApplicationsServiceET;
    @ManagedProperty(value = "#{reviewService}")
    public ReviewService reviewService;

    private String changedFields;
    protected boolean displayVerify = false;
    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    private JasperPrint jasperPrint;
    private boolean showFeedBackButton;
    private List<ProdApplications> allAncestors;
    private String backTo = "";
    private FacesContext facesContext = FacesContext.getCurrentInstance();

    @PostConstruct
    private void init() {
        //Long id = Scrooge.beanParam("Id");
   	    ProdApplications pa= processProdBn.getProdApplications();
        if (pa!=null)changedFields=pa.getAppComment();
    	if (changedFields==null) changedFields="";
    }


    public boolean isDisplayVerify() {
        if (userSession.isAdmin() || userSession.isHead() || userSession.isModerator())
            return true;
        if ((userSession.isStaff())) {
            if (processProdBn.getProdApplications() != null && !(processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL)))
                displayVerify = true;
            else
                displayVerify = false;
        }
        return displayVerify;

    }

    public void setDisplayVerify(boolean displayVerify) {
        this.displayVerify = displayVerify;
    }

    public void prescreenfeerecvd(){
        processProdBn.prodApplications.setPrescreenfeeReceived(true);
    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        processProdBn.save();
        ProdApplications prodApplications = processProdBn.getProdApplications();
        TimeLine timeLine = new TimeLine();

        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
        if (prodApplications.getRegState().equals(RegState.FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                processProdBn.setTimeLine(timeLine);
                processProdBn.addTimeline();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Please send sample request letter if required for processing the application.",
                                "Please send sample request letter if required for processing the application."));
            }
        }
        if (prodApplications.getRegState().equals(RegState.SCREENING)) {
            timeLine.setRegState(RegState.FEE)  ;
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
            prodApplications.setPriorityDate(new Date());

        }

        processProdBn.setSelectedTab(2);
    }

    public List<RegState> getRegSate() {
        ProdApplications prodApplications = processProdBn.getProdApplications();
        if (prodApplications != null)
            return prodApplicationsServiceET.nextStepOptions(prodApplications.getRegState(), userSession, processProdBn.getCheckReviewStatus());
        return null;
    }

    public boolean isShowReturnToSuspensionButton(){
        if (!userSession.isHead()) return false;
        if (processProdBn.getSuspId()==null) return false;
        return true;
    }

    public boolean isShowFeedBackButton() {
           showFeedBackButton = false;// option have canceled
//        showFeedBackButton = userSession.isModerator()
//                && ((processProdBn.prodApplications.getRegState().equals(RegState.REVIEW_BOARD)
//                ||processProdBn.prodApplications.getRegState().equals(RegState.VERIFY)));
        return showFeedBackButton;
    }

    public String feedbackToApplicant() {
        FacesContext facesContext = getCurrentInstance();
        ProdApplications prodApplications = processProdBn.prodApplications;
        TimeLine timeLine = processProdBn.getTimeLine();
        processProdBn.prodApplications.setRegState(processProdBn.timeLine.getRegState());
        User loggedInUser = processProdBn.getUserService().findUser(userSession.getLoggedINUserID());
        List<ReviewInfo> reviewInfos = reviewService.findReviewInfos(prodApplications.getId());
        ReviewInfo review = null;
        if (reviewInfos!=null){
            if (reviewInfos.size()>0)
                review = reviewInfos.get(0);
        }
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if (review==null) {
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("FeedbackForbiden")));
            return  "";
        }
        List<ReviewComment> reviewComments = review.getReviewComments();
        if (reviewComments==null)
            reviewComments = new ArrayList<ReviewComment>();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setDate(new Date());
        reviewComment.setReviewInfo(review);
        reviewComment.setUser(loggedInUser);
        reviewComment.setRecomendType(RecomendType.FEEDBACK);
        reviewComment.setComment(this.processProdBn.timeLine.getComment());
        reviewComments.add(reviewComment);
        review.setReviewComments(reviewComments);
        review.setReviewStatus(ReviewStatus.FEEDBACK);
        RetObject retObject = reviewService.saveReviewInfo(review);
        if (retObject.getMsg().equals("persist")) {
            timeLine.setProdApplications(prodApplications);
            processProdBn.getTimelineService().saveTimeLine(timeLine);
            processProdBn.getTimeLineList().add(timeLine);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
        }else{
            facesContext.addMessage(null, new FacesMessage(retObject.getMsg()));
        }

        return "/internal/processprodlist";
    }

   public boolean isFieldChanged(String fieldname){
	  //получим список из review_info.changedFields  если в списке нет, то false
 //   	 String fieldname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("fieldvalue");
    	   
    	if (changedFields.contains(fieldname)) return true;
    	return false;
    }
    
   public boolean findInnChanged(){
  	  //получим список из review_info.changedFields  если в списке нет, то false
       	if (changedFields.contains("inns")) return true;
      	return false;
    }
   
   public boolean findExcipientChanged(){
	  	  //получим список из review_info.changedFields  если в списке нет, то false
	       	if (changedFields.contains("excipients")) return true;
	      	return false;
	    }
   public boolean findAtcChanged(){
	  	  //получим список из review_info.changedFields  если в списке нет, то false
	       	if (changedFields.contains("Atc")) return true;
	      	return false;
	    }
    
    public void setShowFeedBackButton(boolean showFeedBackButton) {
        this.showFeedBackButton = showFeedBackButton;
    }

    public List<ProdApplications> getAllAncestors(){
        ProdApplications prod = processProdBn.getProdApplications();
        allAncestors = getProdApplicationsServiceET().getAllAncestor(prod);
	    return allAncestors;
    }


    public void setAllAncestors(List<ProdApplications> allAncestors) {
        this.allAncestors = allAncestors;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public String getBackTo() {
        return backTo;
    }

    public void setBackTo(String backTo) {
        this.backTo = backTo;
    }

}
