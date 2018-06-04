package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.*;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.service.ProdApplicationsServiceET;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.TimelineServiceET;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jasperreports.engine.JasperPrint;

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
    @ManagedProperty(value="#{timelineServiceET}")
    private TimelineServiceET timelineServiceET;

    private String changedFields;
    protected boolean displayVerify = false;
    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    private JasperPrint jasperPrint;
    private boolean showFeedBackButton;
    private List<ProdApplications> allAncestors;
    private String backTo = "";
    private FacesContext facesContext = FacesContext.getCurrentInstance();
	protected java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private List<ProdApplications> verApps;

    @PostConstruct
    private void init() {
        //Long id = Scrooge.beanParam("Id");
   	    ProdApplications pa= processProdBn.getProdApplications();
        if (pa!=null) {
            changedFields = pa.getAppComment();
            createStartOfProcessingsEvent();
        }
    	if (changedFields==null) changedFields="";
        verApps = prodApplicationsServiceET.getApplicationsOnVerifying();

    }

    /**
     * Create event PRE_SCREENING when CSO opens submitted application for the first time (i.e. registration process started)
     */
    private void createStartOfProcessingsEvent(){

        if (!processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL))
            return; //only for newly created applications
        if (!processProdBn.getUserService().userHasRole(processProdBn.getLoggedInUser(), UserRole.ROLE_STAFF))
            return; //only for CSOs
        List<TimeLine> tlist = processProdBn.getTimeLineList();
        if (tlist!=null){
            for(TimeLine tl:tlist){
                if (tl.getRegState().equals(RegState.PRE_SCREENING))
                    return;
            }
        }
        //if not found...
        timelineServiceET.createTimeLineEvent(processProdBn.prodApplications,RegState.PRE_SCREENING,
                processProdBn.getLoggedInUser(),"Application processing started");
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

    public void changeVerificationVersion(ProdApplications prodApplications){
        if (userSession.isModerator()) {
                Integer ver = 1;
                if (prodApplications.getVerNumber() != null) {
                    ver = prodApplications.getVerNumber();
                    if (prodApplications.isFeeReceived())
                        ver++;
                    else
                        ver--;
                }
            }
    }

    public void changeStatusListener() {
        ProdApplications prodApplications = processProdBn.getProdApplications();
        TimeLine timeLine = new TimeLine();

        if (prodApplications.getPriorityDate()==null)
            prodApplications.setPriorityDate(Calendar.getInstance().getTime());
        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
        if (prodApplications.getRegState().equals(RegState.FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                changeVerificationVersion(prodApplications);
                processProdBn.setTimeLine(timeLine);
                processProdBn.addTimeline();
                if (!prodApplications.getProdAppType().equals(ProdAppType.RENEW)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    resourceBundle.getString("global_warning"),
                                    "Please send sample request letter if required for processing the application."));
                }
            }
        }
        if (prodApplications.getRegState().equals(RegState.SCREENING)) {
            timeLine.setRegState(RegState.FEE)  ;
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
        if ("".equals(processProdBn.save())) return;
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
            timelineServiceET.createTimeLineEvent(prodApplications,prodApplications.getRegState(),processProdBn.getLoggedInUser(),resourceBundle.getString("status_change_success"));
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
        }else{
            facesContext.addMessage(null, new FacesMessage(retObject.getMsg()));
        }

        return "/internal/processprodlist";
    }
    
    public String registerProduct(ProdApplications prodApplications) {
    	facesContext = getCurrentInstance();
		try {
			if(prodApplications.getRegState().equals(RegState.REGISTERED))
				prodApplicationsServiceET.createRegCert(prodApplications);
			else{
				if (!prodApplications.getRegState().equals(RegState.RECOMMENDED)) {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("register_fail")));
					return "";
				}

				if(prodApplications.getProdRegNo() == null || prodApplications.getProdRegNo().equals(""))
					prodApplications.setProdRegNo(RegistrationUtil.generateRegNo("" + 0, prodApplications.getProdAppNo()));

				prodApplications.setActive(true);
				prodApplications.setUpdatedBy(getProcessProdBn().getLoggedInUser());

				String retValue = getProcessProdBn().getProdApplicationsService().registerProd(prodApplications);
				if(retValue.equals("created")) {
					prodApplicationsServiceET.createRegCert(prodApplications);
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));
                    String msg = resourceBundle.getString("appregistered");
                    timelineServiceET.createTimeLineEvent(prodApplications, prodApplications.getRegState(), processProdBn.getLoggedInUser(), msg);
				}else{
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
		}
		return null;
}

   public boolean isFieldChanged(String fieldname){
	  if (changedFields.contains(fieldname)) return true;
    	return false;
    }
    
   public boolean findInnChanged(){
  	  
       	if (changedFields.contains("inns")) return true;
      	return false;
    }
   
   public boolean findExcipientChanged(){
	  	
	       	if (changedFields.contains("excipients"))
                return true;
            else
	      	    return false;
	    }
   public boolean findAtcChanged(){
	
	       	if (changedFields.contains("Atc")) return true;
	      	return false;
	    }
   public boolean findCompaniesChanged(){
		if (changedFields.contains("ProdCompanies")) return true;
      	return false;
   }


    public void setShowFeedBackButton(boolean showFeedBackButton) {
        this.showFeedBackButton = showFeedBackButton;
    }

    public List<ProdApplications> getAllAncestors(){
        ProdApplications prod = processProdBn.getProdApplications();
        if (prod==null) return null;
        allAncestors = prodApplicationsServiceET.getAllAncestor(prod);
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

    public List<ProdApplications> getVerApps() {
        return verApps;
    }

    public void setVerApps(List<ProdApplications> verApps) {
        this.verApps = verApps;
    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }
}
