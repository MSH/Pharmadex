/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.SuspendDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.UserAccessMBean;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ReviewInfoBn implements Serializable {

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    private ProdApplicationsServiceET prodApplicationsServiceET;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{suspendDAO}")
    SuspendDAO suspendDAO;

    @ManagedProperty(value = "#{userAccessMBean}")
    private UserAccessMBean userAccessMBean;

    @ManagedProperty(value = "#{timelineServiceET}")
    TimelineServiceET timelineServiceET;

    private UploadedFile file;
    private ReviewInfo reviewInfo;
    private Product product;
    private ProdApplications prodApplications;
    private List<DisplayReviewQ> displayReviewQs;
    private boolean readOnly = false;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private ReviewComment reviewComment;
    private List<ReviewComment> reviewComments;
    private RevDeficiency revDeficiency;
    private List<RevDeficiency> revDeficiencies;
    private String revType;
    private boolean priReview;
    private User loggedInUser;
    private boolean submitted=false;
    private boolean reviewApproved=false;
    private String sourcePage="/internal/processreviewlist.faces";
    private int header1ActIndex=0;
    private int header2ActIndex=0;
    private boolean prevSuspended = false;

    @PostConstruct
    private void init() {
        try {
            restoreActiveIndexes();
            if (reviewInfo == null) {
                String reviewInfoID = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reviewInfoID");
                if(reviewInfoID!=null&&!reviewInfoID.equals("")) {
                    reviewInfo = reviewService.findReviewInfo(Long.valueOf(reviewInfoID));
                }else{
                    String prodAppID = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("prodAppID");
                    if(prodAppID!=null&&!prodAppID.equals("")) {
                        reviewInfo = reviewService.findReviewInfoByUserAndProdApp(userSession.getLoggedINUserID(), Long.valueOf(prodAppID));
                    }
                }
                if(reviewInfo!=null) {
                    ReviewStatus reviewStatus = reviewInfo.getReviewStatus();
                    if (reviewStatus.equals(ReviewStatus.SUBMITTED) || reviewStatus.equals(ReviewStatus.ACCEPTED)) {
                        readOnly = true;
                    }
                    reviewComments = getReviewComments();
                }
                loggedInUser = userService.findUser(userSession.getLoggedINUserID());
                String srcPage = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sourcePage");
                if (srcPage!=null)
                    sourcePage = srcPage;
            }
            if (prodApplications==null) return;
            if (prodApplications.getRegState()==null) return;
            if (prodApplications.getRegState().equals(RegState.SUSPEND)) {
                if (prodApplications != null) {
                    List<SuspDetail> suspDetailsList = suspendDAO.findByProdApplications_Id(prodApplications.getId());
                    if (suspDetailsList != null) {
                        for (SuspDetail suspDetail : suspDetailsList) {
                            if (!suspDetail.isComplete()) {
                                if (suspDetail.getParentId() != null) {
                                    prevSuspended = true;
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void restoreActiveIndexes(){
        Long param = Scrooge.beanParam("reviewActiveIndex1");
        if (param!=null)
            header1ActIndex = param.intValue();
        param = Scrooge.beanParam("reviewActiveIndex2");
        if (param!=null)
            header2ActIndex = param.intValue();

    }

    public void handleFileUpload() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();

        if (file != null) {
            msg = new FacesMessage(bundle.getString("global.success"), file.getFileName() + bundle.getString("upload_success"));
            facesContext.addMessage(null, msg);
            try {
                reviewInfo.setFile(IOUtils.toByteArray(file.getInputstream()));
//                saveReview();
            } catch (IOException e) {
                msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public StreamedContent fileDownload() {
        byte[] file1 = reviewInfo.getFile();
        InputStream ist = new ByteArrayInputStream(file1);
        StreamedContent download = new DefaultStreamedContent(ist);
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public StreamedContent fileDownload(ProdAppLetter doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void initComment() {
        reviewComment = new ReviewComment();
        if (getReviewComments() == null) {
            reviewInfo.setReviewComments(new ArrayList<ReviewComment>());
        }

        reviewComment.setUser(loggedInUser);
        reviewComment.setDate(new Date());
        reviewComment.setReviewInfo(reviewInfo);
    }

    public void initRevDef() {
        initComment();
        revDeficiency = new RevDeficiency();
        revDeficiency.setUser(loggedInUser);
        revDeficiency.setReviewInfo(reviewInfo);
        revDeficiency.setCreatedDate(new Date());

        List<ReviewComment> comments = getReviewComments();
        if (comments!=null)
            if (comments.size()>0) {
                for (int j = comments.size()-1; j >= 0; j--) {
                    ReviewComment rc = getReviewComments().get(j);
                    if (rc.getRecomendType() != null && rc.getRecomendType().equals(RecomendType.FIR)) {
                        if (rc.isFinalSummary()) {
                            reviewComment.setComment(rc.getComment());
                            break;
                        }
                }
            }
        }
    }

    public void findRevDef(RevDeficiency revDeficiency) {
        this.revDeficiency = revDeficiency;
        reviewComment = new ReviewComment();
        revDeficiency.setAckComment(reviewComment);
    }

    public void reviewNA(DisplayReviewInfo displayReviewInfo) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        msg = new FacesMessage("Selected question is marked not applicable.");
        reviewService.makeReviewNA(displayReviewInfo.getReviewDetailID());
        displayReviewInfo.setSave(true);
        displayReviewQs = null;
        facesContext.addMessage(null, msg);

    }


    public String saveReview() {
        reviewInfo.setUpdatedBy(loggedInUser);
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        return "";
    }

    public String reviewerFeedback() {
        reviewComments.add(reviewComment);
        reviewInfo.setReviewComments(reviewComments);
        reviewInfo.setReviewStatus(ReviewStatus.FEEDBACK);
        RetObject retObject = reviewService.saveReviewInfo(reviewInfo);
        reviewInfo = (ReviewInfo) retObject.getObj();
        return "/internal/processreg";
    }

    public String approveReview() {
        try {
            facesContext = FacesContext.getCurrentInstance();

            if (!reviewInfo.getReviewStatus().equals(ReviewStatus.SUBMITTED)) {
                facesContext.addMessage(null, new FacesMessage(bundle.getString("recommendation_empty_valid"), bundle.getString("recommendation_empty_valid")));
            }

            reviewComment = getReviewComments().get(getReviewComments().size() - 1);
            reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
            reviewInfo.setComment(reviewComment.getComment());
            saveReview();
            userSession.setProdAppID(reviewInfo.getProdApplications().getId());
            userSession.setProdID(reviewInfo.getProdApplications().getProduct().getId());
            return goBack();
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage("Log out of the system and try again."));
            return "";
        }
    }

    private void storeActiveIndexes(){
        if (header1ActIndex+header2ActIndex!=0){
            Scrooge.setBeanParam("reviewActiveIndex1",(long) header1ActIndex);
            Scrooge.setBeanParam("reviewActiveIndex2",(long) header2ActIndex);
        }
    }

    public String updateReview(DisplayReviewInfo displayReviewInfo) {
        FacesMessage msg;
        storeActiveIndexes();
        facesContext = FacesContext.getCurrentInstance();
        msg = new FacesMessage(bundle.getString("global.success") + " Selected ID == " + displayReviewInfo.getId(), "Selected ID == " + displayReviewInfo.getId());
        facesContext.addMessage(null, msg);
        userSession.setDisplayReviewInfo(displayReviewInfo);
        return "reviewdetail";

    }

    public String submitReview() {
        FacesMessage msg = null;
        facesContext = FacesContext.getCurrentInstance();
//        if (reviewInfo.getRecomendType() == null) {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please provide recommendation type.");
//            facesContext.addMessage(null, msg);
//            return "";
//        }
        String retValue = reviewService.submitReview(reviewInfo);
        if (retValue.equals("NOT_ANSWERED")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please answer all the questions");
            facesContext.addMessage(null, msg);
            return "";
        } else if (retValue.equals("SAVE")) {
            msg = new FacesMessage(bundle.getString("global.success"));
            facesContext.addMessage(null, msg);
            facesContext.getExternalContext().getFlash().put("prodAppID", reviewInfo.getProdApplications().getId());
            return "processreg";
        }
        return "";
    }

    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (reviewComment.getComment().length()>20000){
                facesContext.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,bundle.getString("global_fail"),bundle.getString("errorStringTooLong")));
                return;
            }
            RetObject retObject = reviewService.submitReviewInfo(reviewInfo, reviewComment, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("success")) {
                reviewInfo = (ReviewInfo) retObject.getObj();
                reviewComments.add(reviewComment);

                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                if (reviewComment.getRecomendType().equals(RecomendType.FIR)){
                    String comment = "FIR Requested";
                    timelineServiceET.createTimeLineEvent(prodApplications, RegState.REVIEW_BOARD, reviewComment.getUser(), comment);
                }else {
                    String summary = "";
                    if (!reviewInfo.isSecreview()){
                        summary = reviewComment.getComment();
                    }
                    reviewInfo.setExecSummary(summary);
                    String comment = (reviewInfo.isSecreview()) ? "Secondary review submitted" : "Primary review submitted";
                    timelineServiceET.createTimeLineEvent(prodApplications, RegState.REVIEW_BOARD, reviewComment.getUser(), comment);
                }
            } else if (retObject.getMsg().equals("close_def")) {
              facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));
            }
            }catch(Exception ex){
                ex.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
            }
    }

    public void printReview() {
        if(prodApplications != null){
            String s =getProdApplicationsServiceET().createReviewDetails(prodApplications);
            if(!s.equals("persist")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
            }else{
                javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
            }
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
        }
        /*
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            JasperPrint jasperPrint = reviewService.getReviewReport(reviewInfo.getId());
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
            httpServletResponse.setContentType("application/pdf");
            javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        } catch (JRException e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        } catch (IOException e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }
        FacesContext.getCurrentInstance().responseComplete();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        */
    }

    public String revDefAck() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            reviewComment.setFinalSummary(false);
            reviewComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
            reviewInfo.setReviewStatus(ReviewStatus.FIR_RECIEVED);
            reviewInfo.setSubmitDate(new Date());
            revDeficiency.setAckComment(reviewComment);
            revDeficiency.setResolved(true);
            revDeficiency.setUser(reviewComment.getUser());
            getReviewComments().add(reviewComment);
            RetObject retObject = reviewService.saveRevDeficiency(revDeficiency);

            if (retObject.getMsg().equals("success")) {
                revDeficiency = (RevDeficiency) retObject.getObj();
                reviewService.saveReviewInfo(reviewInfo);
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                timelineServiceET.createTimeLineEvent(prodApplications,RegState.REVIEW_BOARD,reviewComment.getUser(),"Assesment was resumed after FIR receiving.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return "";
    }


    /**
     * Generate FIR letter
     * @return
     */

    public String generateLetter() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
//            reviewComment.setRecomendType(RecomendType.APPLICANT_FEEDBACK);
            revDeficiency.setSentComment(reviewComment);
            revDeficiency.setUser(reviewComment.getUser());
            reviewInfo.setSubmitDate(new Date());
            getReviewComments().add(reviewComment);
            reviewInfo.setReviewStatus(ReviewStatus.FIR_SUBMIT);
            revDeficiency.setReviewInfo(reviewInfo);
            revDeficiency.setCreatedDate(new Date());
            RetObject retObject = reviewService.createDefLetter(revDeficiency);

            if (retObject.getMsg().equals("success")) {
                reviewInfo = (ReviewInfo) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                reviewComments = getReviewComments();
                revDeficiencies = null;
                String fs_msg = bundle.getString(RegState.FOLLOW_UP.getKey());
                timelineServiceET.createTimeLineEvent(prodApplications,RegState.FOLLOW_UP,reviewComment.getUser(),fs_msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return "";
    }

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public Product getProduct() {
        if (product != null && product.getId() != null) {
            System.out.println("product id == " + product.getId());
        } else {
            reviewInfo = getReviewInfo();
            if (reviewInfo != null) {
                prodApplications = reviewInfo.getProdApplications();
                product = productService.findProduct(reviewInfo.getProdApplications().getProduct().getId());
            }
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public List<DisplayReviewQ> getDisplayReviewQs() {
        if (displayReviewQs == null) {
            displayReviewQs = reviewService.getDisplayReviewSum(getReviewInfo(), userSession.getLoggedINUserID());
        }
        return displayReviewQs;
    }

    public List<RecomendType> getRevRecomendTypes() {
        List<RecomendType> recomendTypes = new ArrayList<RecomendType>();
        if (!prodApplications.getRegState().equals(RegState.SUSPEND)){
            recomendTypes.add(RecomendType.RECOMENDED);
            recomendTypes.add(RecomendType.NOT_RECOMENDED);
            recomendTypes.add(RecomendType.FIR);
        }else{
            recomendTypes.add(RecomendType.REGISTER);
            if (!prevSuspended) recomendTypes.add(RecomendType.SUSPEND);
            recomendTypes.add(RecomendType.CANCEL);
        }
        return recomendTypes;
    }

    public void onChangeHdr1(TabChangeEvent event){
        AccordionPanel tv = (AccordionPanel) event.getSource();
        header1ActIndex = tv.getIndex();
    }

    public void onChangeHdr2(TabChangeEvent event){
        TabView tv = (TabView) event.getSource();
        header2ActIndex = tv.getIndex();
    }

    public void setDisplayReviewQs(List<DisplayReviewQ> displayReviewQs) {
        this.displayReviewQs = displayReviewQs;
    }

    public boolean isReadOnly() {
        if (reviewInfo == null)
            getReviewInfo();
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null)
            getProduct();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ReviewComment getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(ReviewComment reviewComment) {
        this.reviewComment = reviewComment;
    }

    public List<ReviewComment> getReviewComments() {
        if (reviewInfo != null && reviewComments == null)
            reviewComments = reviewService.findReviewComments(reviewInfo.getId());
        return reviewComments;
    }

    public void setReviewComments(List<ReviewComment> reviewComments) {
        this.reviewComments = reviewComments;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public List<RevDeficiency> getRevDeficiencies() {
        if (revDeficiencies == null) {
            revDeficiencies = prodApplicationsService.findRevDefByRI(getReviewInfo());
        }
        return revDeficiencies;
    }

    public void setRevDeficiencies(List<RevDeficiency> revDeficiencies) {
        this.revDeficiencies = revDeficiencies;
    }

    public RevDeficiency getRevDeficiency() {
        return revDeficiency;
    }

    public void setRevDeficiency(RevDeficiency revDeficiency) {
        this.revDeficiency = revDeficiency;
    }

    public UserAccessMBean getUserAccessMBean() {
        return userAccessMBean;
    }

    public void setUserAccessMBean(UserAccessMBean userAccessMBean) {
        this.userAccessMBean = userAccessMBean;
    }

    public String getRevType() {
        if (reviewInfo != null) {
            if (reviewInfo.getReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId())) {
                revType = bundle.getString("pri_processor");
            } else if (reviewInfo.getSecReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getSecReviewer().getUserId()))
                revType = bundle.getString("sec_processor");
        }
        return revType;
    }

    public void setRevType(String revType) {
        this.revType = revType;
    }

    public boolean isPriReview() {
        if (reviewInfo != null) {
            if (reviewInfo.getReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getReviewer().getUserId())) {
                if (reviewInfo.getReviewStatus().equals(ReviewStatus.ASSIGNED) || reviewInfo.getReviewStatus().equals(ReviewStatus.IN_PROGRESS)
                        || reviewInfo.getReviewStatus().equals(ReviewStatus.FIR_RECIEVED))
                    priReview = true;
                else {
                    if (!reviewInfo.isSecreview() && (reviewInfo.getReviewStatus().equals(ReviewStatus.FEEDBACK) ||
                            reviewInfo.getReviewStatus().equals(ReviewStatus.FIR_RECIEVED)))
                        priReview = true;
                    else
                        priReview = false;
                }
            }
            if (reviewInfo.getSecReviewer() != null && userSession.getLoggedINUserID().equals(reviewInfo.getSecReviewer().getUserId())) {
                if (reviewInfo.getReviewStatus().equals(ReviewStatus.SEC_REVIEW)||reviewInfo.getReviewStatus().equals(ReviewStatus.FEEDBACK))
                    priReview = true;
                else
                    priReview = false;
            }
        }
        //priReview = true;
        return priReview;
    }

    public String goBack(){
        String[] src = sourcePage.split(":");
        if (src.length==1) return sourcePage; //return to list
        String id=src[0];
        String page = src[1];
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (page.contains("suspenddetail")){
            context.getFlash().put("suspDetailID",id);
            return page;
        }else if (page.contains("process")){
            context.getFlash().put("prodAppID",id);
            return page;
        }
        return "";
    }
    public boolean isSubmitted() {
        if (reviewInfo!=null){
            submitted = reviewInfo.getReviewStatus().equals(ReviewStatus.SUBMITTED) ||
                    reviewInfo.getReviewStatus().equals(ReviewStatus.ACCEPTED);
        }
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }


    public boolean isReviewApproved() {
        if (reviewInfo!=null){
            reviewApproved = reviewInfo.getReviewStatus().equals(ReviewStatus.ACCEPTED);
        }
        return reviewApproved;
    }

    public void setReviewApproved(boolean reviewApproved) {
        this.reviewApproved = reviewApproved;
    }



    public void setPriReview(boolean priReview) {
        this.priReview = priReview;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public int getHeader1ActIndex() {
        return header1ActIndex;
    }

    public void setHeader1ActIndex(int header1ActIndex) {
        this.header1ActIndex = header1ActIndex;
    }

    public int getHeader2ActIndex() {
        return header2ActIndex;
    }

    public void setHeader2ActIndex(int header2ActIndex) {
        this.header2ActIndex = header2ActIndex;
    }

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }

    public SuspendDAO getSuspendDAO() {
        return suspendDAO;
    }

    public void setSuspendDAO(SuspendDAO suspendDAO) {
        this.suspendDAO = suspendDAO;
    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }
}
