package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;
import static org.msh.pharmadex.domain.enums.RegState.FEE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.Comment;
import org.msh.pharmadex.domain.ForeignAppStatus;
import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.Mail;
import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.RevDeficiency;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.enums.SuspensionStatus;
import org.msh.pharmadex.mbean.BackLog;
import org.msh.pharmadex.mbean.UserAccessMBean;
import org.msh.pharmadex.service.AmdmtService;
import org.msh.pharmadex.service.CommentService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.SampleTestService;
import org.msh.pharmadex.service.SuspendService;
import org.msh.pharmadex.service.SuspendServiceMZ;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

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
	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
	protected ProdApplicationsServiceMZ prodApplicationsServiceMZ;

	protected ProdApplications prodApplications;
	protected Product product;
	protected List<TimeLine> timeLineList;
	protected org.msh.pharmadex.domain.TimeLine timeLine = new org.msh.pharmadex.domain.TimeLine();
	protected FacesContext facesContext = getCurrentInstance();
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
	@ManagedProperty(value = "#{suspendServiceMZ}")
    private SuspendServiceMZ suspendServiceMZ;
	
	@ManagedProperty(value = "#{attachmentDAO}")
	private AttachmentDAO attachmentDAO;
	@ManagedProperty(value = "#{prodRegAppMbean}")
	private ProdRegAppMbean prodRegAppMbean;

	private Applicant applicant;
	private List<Comment> comments;
	private List<Mail> mails;
	private List<ProdAppAmdmt> prodAppAmdmts;
	private List<ProdAppChecklist> prodAppChecklists;
	private TimelineModel model;
	private List<Timeline> timelinesChartData;
	private Comment selComment = new Comment();
	private Mail mail = new Mail();
	private String reviewComment;
	private List<Invoice> invoices;
	private boolean checkReviewStatus = false;
	private int selectedTab;
	private User moderator;
	private boolean displaySample = false;
	private boolean displayClinical = false;
	private boolean displayReviewStatus = false;
	private UploadedFile file;
	@ManagedProperty(value = "#{reviewService}")
	private ReviewService reviewService;
	public User loggedInUser;
	private List<ProdAppLetter> letters;
	private List<ReviewInfo> reviewInfos;
	private boolean registered;
	private boolean createRegCert;
	private boolean createRejCert;
	private boolean prescreened;
	private List<ProdApplications> prevProdApps;
	private List<SuspDetail> suspDetails;
	private List<Attachment> clinicalRevs;
	private ArrayList<RevDeficiency> revDeficiencies;

	private boolean disableVerify;
	private boolean displayFir;
	private String suspId;
	private String appType;
	private String backTo;

	private List<ForeignAppStatus> foreignAppStatuses;

	@PostConstruct
	private void init() {
		try {
			facesContext = FacesContext.getCurrentInstance();
			if (facesContext.getExternalContext().getRequestParameterMap()!=null) {
				suspId = facesContext.getExternalContext().getRequestParameterMap().get("suspDetailID");
				if ("null".equals(suspId)) suspId=null;
			}
			if (suspId==null){
				if (facesContext.getExternalContext().getFlash()!=null)
					suspId = (String) facesContext.getExternalContext().getFlash().get("suspDetailID");
			}
			
			loggedInUser = userService.findUser(userSession.getLoggedINUserID());
			mail.setUser(loggedInUser);
			timeLine.setUser(loggedInUser);
			selComment.setUser(loggedInUser);
			initProdApps();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		facesContext = getCurrentInstance();
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
		if (prodApplications != null && prodApplications.getRegState() != null && (!userSession.isCompany() || userSession.isAdmin())) {
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

	public void initRegistration() {
		if (prodApplications.getProdRegNo() == null || prodApplications.getProdRegNo().equals(""))
			prodApplications.setProdRegNo(RegistrationUtil.generateRegNo("" + (productService.findAllRegisteredProduct().size() + 1), prodApplications.getProdAppNo()));
	}

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
		facesContext = getCurrentInstance();
		try {
			String id;
			Long prodAppID = Scrooge.beanParam("prodAppID");
			if (prodAppID==null)//call from another page
				prodAppID = Scrooge.beanParam("Id");
			if (prodAppID != null) {
				prodApplications = prodApplicationsService.findProdApplications(prodAppID);
				setFieldValues();
			}
			backTo = Scrooge.beanStrParam("backTo");
		} catch (Exception ex) {
			facesContext.addMessage(null, new FacesMessage("Use the link within the system to access this page."));
		}
	}

	public void setFieldValues() {
		product = prodApplications.getProduct();
		moderator = prodApplications.getModerator();
		//        prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
		timeLineList = timelineService.findTimelineByApp(prodApplications.getId());

		revDeficiencies = prodApplicationsService.findOpenRevDefByPA(prodApplications.getId());
		if(revDeficiencies.size()>0) displayFir = true;
		else displayFir = false;
	}

	public void dateChange() {
		Workspace w = workspaceDAO.findOne((long) 1);
		prodApplications.setRegExpiryDate(JsfUtils.addDate(prodApplications.getRegistrationDate(), w.getProdRegDuration()));
	}
	/**
	 * Add comment and go to list
	 * @return
	 */
	public String addComment() {
		addCommentAjax();
		return "/internal/processprodlist";
	}
	/**
	 * Add comment, but stay here
	 * @return
	 */
	public String addCommentAjax() {
		try {
			if (comments == null)
				comments = new ArrayList<Comment>();
			facesContext = getCurrentInstance();
			selComment.setDate(new Date());
			selComment.setProdApplications(prodApplications);
			selComment.setUser(loggedInUser);
			selComment = commentService.saveComment(selComment);
			comments.add(selComment);
			selComment = new Comment();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("comment_success")));
		} catch (Exception ex) {
			ex.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Log out of the system and try again."));
		}
		return "";
	}

	public String assignModerator() {
		try {

			facesContext = getCurrentInstance();
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

		return "/internal/processprodlist";
	}

	public String sendRenewAfterSuspension(){
		Long appId = Scrooge.beanParam("appID");
		prodApplications = prodApplicationsService.findProdApplications(appId);
		suspDetails = suspendService.findSuspendByProd(appId);
		SuspDetail suspApp = null;
		for (SuspDetail susp:suspDetails){
			if (susp.getSuspensionStatus().equals(SuspensionStatus.RESULT) && (susp.getDecision().equals(RecomendType.SUSPEND))){
				suspApp = susp;
				break;
			}
		}
		if (suspApp==null) return "";
		suspApp.setSuspensionStatus(SuspensionStatus.REQUESTED);
		suspApp.setDecision(null);
		suspApp.setDecisionDate(null);
		suspApp.setCanceled(false);
		suspApp.setComplete(false);
		suspApp.setDueDate(null);
		suspendService.saveSuspend(suspApp);
		Scrooge.setBeanParam("prodAppID",appId);
		Scrooge.setBeanParam("suspDetailID",suspApp.getId());
		return "/internal/suspenddetail.faces";
	}

	public String sendToRenew() {
		Scrooge.setBeanParam("StandardProcedure",(long) 0);
		Scrooge.setBeanParam("prodID", Scrooge.beanParam("prodID"));
		Scrooge.setBeanParam("appID", Scrooge.beanParam("appID"));
		return "secure/prodreginit.xhtml";
	}

	public String sendMessage() {
		facesContext = getCurrentInstance();
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
		facesContext = getCurrentInstance();
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

	public void changeStatusListener() {
		try {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void changeDCC() {
		logger.error("Inside changeStatusListener");
		try {
			save();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public void changeClinicalReviewStatus() {
		logger.error("Inside changeStatusListener");
		try {
			save();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void changeSampleRecieved() {
		logger.error("Inside changeSampleRecieved");
		try {
			save();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * At Mozambique this action delete
	 */
	public void initOpenToApp() {
		timeLine = new TimeLine();
		timeLine.setProdApplications(prodApplications);
		timeLine.setStatusDate(new Date());
		timeLine.setUser(loggedInUser);
		timeLine.setRegState(RegState.SAVED);
	}

	public String openToApplicant() {
		facesContext = getCurrentInstance();
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
		facesContext = getCurrentInstance();
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
		return "/internal/processprodlist";  //To change body of created methods use File | Settings | File Templates.
	}

	public String save() {
		facesContext = getCurrentInstance();
		try {
			prodApplications = prodApplicationsService.saveApplication(prodApplications, userSession.getLoggedINUserID());
			//            product = productService.findProduct(product.getId());
			setFieldValues();
		} catch (Exception e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(e.getMessage()));
		}
		return "/internal/processprodlist";
	}

	public String registerProduct() {
			facesContext = getCurrentInstance();
			if (!prodApplications.getRegState().equals(RegState.RECOMMENDED)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("register_fail")));
				return "";
			}

			if(prodApplications.getProdRegNo()==null||prodApplications.getProdRegNo().equals(""))
				prodApplications.setProdRegNo(RegistrationUtil.generateRegNo("" + 0, prodApplications.getProdAppNo()));

			prodApplications.setActive(true);
			prodApplications.setUpdatedBy(loggedInUser);

			String retValue = prodApplicationsService.registerProd(prodApplications);
			if(retValue.equals("created")) {
				System.out.println("Product moved to registered");
				prodApplicationsService.createRegCert(prodApplications);
				timeLineList = null;
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));
			}else{
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
			}
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
		registered = false;
		if (prodApplications != null) {
			if (prodApplications.getRegState().equals(RegState.REGISTERED))
				registered = true;
		}
		return registered;
	}

	public boolean isCreateRegCert() {
		createRegCert = false;
		if (prodApplications != null) {
			if (prodApplications.getRegState().equals(RegState.REGISTERED)){
				if(prodApplications.getRegCert() == null)
					createRegCert = true;
			}
		}
		return createRegCert;
	}

	public void setCreateRegCert(boolean createRegCert) {
		this.createRegCert = createRegCert;
	}
	
	public boolean isCreateRejCert() {
		createRejCert = false;
		if (prodApplications != null) {
			if (prodApplications.getRegState().equals(RegState.REJECTED)){
				if(prodApplications.getRejCert() == null)
					createRejCert = true;
			}
		}
		return createRejCert;
	}

	public void setCreateRejCert(boolean createRejCert) {
		this.createRejCert = createRejCert;
	}

	public boolean isSuspended(){
		if (prodApplications != null) {
			if (prodApplications.getRegState().equals(RegState.SUSPEND))
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
		}else
			displayVerify = false;
		return displayVerify;
	}

	public void setDisplayVerify(boolean displayVerify) {
		this.displayVerify = displayVerify;
	}

	public String cancel() {
		facesContext = getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		WebUtils.setSessionAttribute(request, "processProdBn", null);
		//String backTo = Scrooge.beanStrParam("backTo");
		BackLog.setBackTo(backTo);
		String res = BackLog.goToBack();
		res=res+"?faces-redirect=true";
		if (backTo!=null)
			return backTo;
		else
			if(loggedInUser != null){
				if(userSession.isReviewer()){
					return "/secure/submittedproducts";
				}
			}
			return "/internal/processprodlist";
	}

	public boolean isDisplaySample() {
		displaySample = false;
		if (prodApplications != null) {
			if (prodApplications.getProdAppType()==ProdAppType.RENEW) return false;
			if (prodApplications != null && prodApplications.getProdAppType() != null
					&& prodApplications.getProdAppType().equals(ProdAppType.RENEW)) {
				displaySample = false;
			} else {
				//Odissey. Req.18/03/16 Add SimpleRequest fuction to CSO
				if ((userSession.isStaff() || userSession.isModerator()
						|| userSession.isLab()) || userSession.isCsd()) {
					RegState regState = prodApplications.getRegState();
					if ((prodApplications != null && regState != null)) {
						if (regState.equals(RegState.NEW_APPL) || regState.equals(RegState.FEE)) {
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
		letters = prodApplicationsService.findAllLettersByProdApp(getProdApplications().getId());
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
		else{
			prescreened = false;
			displayVerify = false;
		}
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
	
	public SuspendServiceMZ getSuspendServiceMZ() {
		return suspendServiceMZ;
	}

	public void setSuspendServiceMZ(SuspendServiceMZ suspendService) {
		this.suspendServiceMZ = suspendService;
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
		if (prodApplications != null && prodApplications.getProdAppType() != null
				&& prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
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

	public boolean isDisableVerify() {
		if (prodApplications != null) {
			if (prodApplications.getRegState().ordinal() > 3) {
				disableVerify = true;

			} else {
				displayVerify = false;
			}
		}
		return disableVerify;
	}

	public boolean showFeedbackButton(){
		boolean res;
		res = userSession.isHead() && (prodApplications.getRegState().equals(RegState.REVIEW_BOARD)||prodApplications.getRegState().equals(RegState.VERIFY));
		return res;
	}

	public String returnToSuspendMode() {
		if (suspId == null){
			return "";
		}else{
			facesContext.getExternalContext().getRequestParameterMap().put("suspDetailID",suspId);
			return "internal/suspenddetail";
		}
	}

	public void setDisableVerify(boolean disableVerify) {
		this.disableVerify = disableVerify;
	}

	public boolean isDisplayFir() {
		return displayFir;
	}

	public void setDisplayFir(boolean displayFir) {
		this.displayFir = displayFir;
	}

	public ArrayList<RevDeficiency> getRevDeficiencies() {
		return revDeficiencies;
	}

	public void setRevDeficiencies(ArrayList<RevDeficiency> revDeficiencies) {
		this.revDeficiencies = revDeficiencies;
	}
	public String getSuspId() {
		return suspId;
	}

	public void setSuspId(String suspId) {
		this.suspId = suspId;
	}

	public List<ForeignAppStatus> getForeignAppStatuses() {
		foreignAppStatuses = prodRegAppMbean.getForeignAppStatuses();
		return foreignAppStatuses;
	}

	public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
		this.foreignAppStatuses = foreignAppStatuses;
	}

	public List<ProdAppChecklist> getProdAppChecklists() {
		prodAppChecklists = prodRegAppMbean.getProdAppChecklists();
		return prodAppChecklists;
	}

	public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
		this.prodAppChecklists = prodAppChecklists;
	}

	public ProdRegAppMbean getProdRegAppMbean() {
		return prodRegAppMbean;
	}

	public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
		this.prodRegAppMbean = prodRegAppMbean;
	}


	public String getAppType() {
		String res = resourceBundle.getString(prodApplications.getProdAppType().getKey());
		if (prodApplications.getProdAppType().equals(ProdAppType.VARIATION)){
			if (prodApplications.getMjVarQnt()>0){
				res = res + resourceBundle.getString("variationType_major").toLowerCase() + " " + String.valueOf(prodApplications.getMjVarQnt());
			}
			if (prodApplications.getMnVarQnt()>0 && (prodApplications.getMjVarQnt()>0)){
				res = res + " " + "/" + " ";
			}
			if (prodApplications.getMnVarQnt()>0){
				res = res + " " + resourceBundle.getString("variationType_minor").toLowerCase() + " " + String.valueOf(prodApplications.getMnVarQnt());
			}
		}
		appType = "("+res+")";
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
	public boolean canChangeModerator() {
		if (!(userSession.isStaff() || userSession.isAdmin())) return false;
		if (isRegistered() || isSuspended()) return false;
		return true;
	}

	public String addToBackTo(String param){
		if (backTo!=null){
			if (!"".equals(backTo)) {
				backTo = backTo + ";" + param;
				return backTo;
			}
		}
		backTo = param;
		return backTo;
	}

	public String gotoBack(){
		BackLog.setBackTo(backTo);
		String res = BackLog.goToBack();
		res=res+"?faces-redirect=true";
		return res;
	}

	public String getBackTo() {
		return backTo;
	}

	public void setBackTo(String backTo) {
		this.backTo = backTo;
	}

	public boolean getCanNextStep() {
		if((userSession.isAdmin() || getCheckReviewStatus() || userSession.isStaff()
				|| userSession.isModerator()) && !prodApplications.getRegState().equals(RegState.REGISTERED)
				 && !prodApplications.getRegState().equals(RegState.REJECTED))
			return true;
		return false;
	}
	
	public boolean getCanChangeModerator() {
		if((userSession.isAdmin() || userSession.isStaff())
				&& !prodApplications.getRegState().equals(RegState.REGISTERED)
				 && !prodApplications.getRegState().equals(RegState.REJECTED))
			return true;
		return false;
	}

	public String buildStringByRegDlg(boolean isheader){
		if(prodApplications != null){
			if(prodApplications.getRegState().equals(RegState.REGISTERED)){
				if(isheader)
					return resourceBundle.getString("reg_cert_create");
				else
					return resourceBundle.getString("label_create");
			}
		}

		return resourceBundle.getString("register_product");
	}

	public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
		return prodApplicationsServiceMZ;
	}

	public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
		this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
	
	
}
