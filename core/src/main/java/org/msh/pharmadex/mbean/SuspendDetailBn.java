/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.SuspensionStatus;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class SuspendDetailBn implements Serializable {


    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{suspendService}")
    private SuspendService suspendService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{prodAppLetterDAO}")
    private ProdAppLetterDAO prodAppLetterDAO;

    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;

    private Logger logger = LoggerFactory.getLogger(SuspendDetailBn.class);
    private UploadedFile file;
    private SuspDetail suspDetail;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private SuspComment suspComment;
    private List<SuspComment> suspComments;
    private List<ProdAppLetter> prodAppLetters;
    private ProdAppLetter prodAppLetter;
    private User moderator;
    private User reviewer;
    private User loggedInUser;
    private boolean showSuspend;
    private JasperPrint jasperPrint;

    @PostConstruct
    private void init() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            if (suspDetail == null) {
                String suspID = facesContext.getExternalContext().getRequestParameterMap().get("suspDetailID");
                if (suspID != null && !suspID.equals("")) {
                    Long suspDetailID = Long.valueOf(suspID);
                    suspDetail = suspendService.findSuspendDetail(suspDetailID);
                    suspComments = suspDetail.getSuspComments();
                    prodAppLetters = suspDetail.getProdAppLetters();
                    prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
                    product = prodApplications.getProduct();
                } else {
                    Long prodAppID = Long.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("prodAppID"));
                    if (prodAppID != null) {
                        prodApplications = prodApplicationsService.findProdApplications(prodAppID);
                        product = prodApplications.getProduct();
                        suspComments = new ArrayList<SuspComment>();
                        suspDetail = new SuspDetail(prodApplications, suspComments);
                        suspDetail.setSuspensionStatus(SuspensionStatus.REQUESTED);
                        suspDetail.setCreatedBy(getLoggedInUser());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Transactional
    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            if (suspComments == null) {
                suspComments = new ArrayList<SuspComment>();
            }

            suspComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
            suspComment.setDate(new Date());
            suspComment.setSuspDetail(suspDetail);
            suspComments.add(suspComment);
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));

//            RetObject retObject = suspendService.saveSuspend(suspDetail);
//            if (retObject.getMsg().equals("success")) {
//                suspDetail = (SuspDetail) retObject.getObj();
//                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
//
//            } else if (retObject.getMsg().equals("close_def")) {
//                facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));
//
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
    }

//    public void downloadSuspLetter(){
//        File invoicePDF = null;
//        Flash flash = JsfUtils.flashScope();
//        flash.put("prodAppID", prodApplications.getId());
//        flash.put("suspDetailID", suspDetail.getId());
//        facesContext = FacesContext.getCurrentInstance();
//        try {
//            invoicePDF = File.createTempFile("" + prodApplications.getProduct().getProdName() + "_susp", ".pdf");
//        User user = userService.findUser(userSession.getLoggedINUserID());
//        jasperPrint = suspendService.generateSuspLetter(suspDetail);
//
//        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
//        byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
//
//        ProdAppLetter attachment = new ProdAppLetter();
//        attachment.setRegState(prodApplications.getRegState());
//        attachment.setFile(file);
//        attachment.setProdApplications(prodApplications);
//        attachment.setFileName(invoicePDF.getName());
//        attachment.setTitle("Suspension Letter");
//        attachment.setUploadedBy(prodApplications.getCreatedBy());
//        attachment.setComment("Automatically generated Letter");
//        attachment.setContentType("application/pdf");
//        attachment.setLetterType(LetterType.SUSP_NOTIF_LETTER);
//        prodAppLetterDAO.save(attachment);
//
//        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        httpServletResponse.addHeader("Content-disposition", "attachment; filename=deficiency_letter.pdf");
//        httpServletResponse.setContentType("application/pdf");
//        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
//        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
//        facesContext.responseComplete();
////        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
////        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
//
//        TimeLine timeLine = new TimeLine();
//        timeLine.setRegState(RegState.FOLLOW_UP);
//        timeLine.setStatusDate(new Date());
//        timeLine.setUser(user);
//        timeLine.setComment(suspComment.getComment());
//        timeLine.setProdApplications(prodApplications);
//        prodApplications.setRegState(timeLine.getRegState());
//        RetObject retObject = timelineService.saveTimeLine(timeLine);
//        if (!retObject.getMsg().equals("persist")) {
//            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
//        }
//        } catch (IOException e) {
//            e.printStackTrace();
//            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
//        } catch (JRException e) {
//            e.printStackTrace();
//            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
//        }
//
//    }

    private User getLoggedInUser() {
        if (loggedInUser == null) {
            loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        }
        return loggedInUser;
    }

    public void assignModerator() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(getLoggedInUser());
            suspDetail.setModerator(moderator);
//            RetObject retObject = suspendService.saveSuspend(suspDetail);
//            if (retObject.getMsg().equals("persist")) {
//                suspDetail = (SuspDetail) retObject.getObj();
//                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("moderator_add_success")));
//            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "suspendetailbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("processor_add_error")));
        }
    }

    public String reviewFeedback() {
        suspDetail.setSuspensionStatus(SuspensionStatus.FEEDBACK);
        suspendService.saveSuspend(suspDetail);
        return "";
    }

    public void assignReviewer() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            suspDetail.setUpdatedDate(new Date());
            suspDetail.setUpdatedBy(getLoggedInUser());
            suspDetail.setReviewer(reviewer);
            RetObject retObject = suspendService.saveSuspend(suspDetail);
            if (retObject.getMsg().equals("persist")) {
                suspDetail = (SuspDetail) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("moderator_add_success")));
            }
        } catch (Exception e) {
            logger.error("Problems saving moderator {}", "suspendetailbn", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("processor_add_error")));
        }
    }

    public StreamedContent fileDownload(ProdAppLetter doc) {
        ProdAppLetter prodAppLetter = suspDetail.getProdAppLetters().get(0);
        InputStream ist = new ByteArrayInputStream(prodAppLetter.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, prodAppLetter.getContentType(), prodAppLetter.getFileName());
        return download;
    }

    public void initComment() {
        suspComment = new SuspComment();
    }

    public String submitSuspend() {
        facesContext = FacesContext.getCurrentInstance();
        if (userSession.isHead()) {
            if (suspDetail.getModerator() == null) {
                FacesMessage fm = new FacesMessage("Please specify a moderator to process the request.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            if (suspComments.size() == 0) {
                FacesMessage fm = new FacesMessage("Please specify comment for Team Leader.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            RetObject retObject = suspendService.submitHead(suspDetail, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("error")) {
                FacesMessage fm = new FacesMessage("Error");
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
            }
        }

        if (userSession.isModerator()) {
            if (suspDetail.getReviewer() == null) {
                FacesMessage fm = new FacesMessage("Please specify a Assessor to process the request.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                return "";
            }
            try {
                RetObject retObject = suspendService.submitModeratorComment(suspDetail, userSession.getLoggedINUserID());
            } catch (SQLException e) {
                FacesMessage fm = new FacesMessage(e.getMessage());
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                e.printStackTrace();
            } catch (JRException e) {
                FacesMessage fm = new FacesMessage(e.getMessage());
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
                e.printStackTrace();
            }
        }

        if (userSession.isReviewer()) {
            if (suspDetail.getFinalSumm() == null || suspDetail.equals("")) {
                FacesMessage fm = new FacesMessage("Please provide final comment.");
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                facesContext.addMessage(null, fm);
            }
            RetObject retObject = suspendService.submitReview(suspDetail, userSession.getLoggedINUserID());
            if (retObject.getMsg().equals("error")) {
                FacesMessage fm = new FacesMessage("Error");
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
            } else {
                suspDetail = (SuspDetail) retObject.getObj();
            }
        }
        return "processcancellist";
    }

    public List<RegState> getDecisionType() {
        List<RegState> decisionType = new ArrayList<RegState>();
        decisionType.add(RegState.SUSPEND);
        decisionType.add(RegState.CANCEL);
        decisionType.add(RegState.REGISTERED);
        return decisionType;
    }


    public String saveSuspend() {
        RetObject retObject = suspendService.saveSuspend(suspDetail);
        suspDetail = (SuspDetail) retObject.getObj();
        return "";
    }

    public String suspendProduct() {
        facesContext = FacesContext.getCurrentInstance();
        if (suspDetail.getSuspStDate() == null && suspDetail.getDecisionDate() == null && suspDetail.getDecision() == null)
            return "";

        RetObject retObject = null;
        try {
            retObject = suspendService.suspendProduct(suspDetail, getLoggedInUser());
//        globalEntityLists.setRegProducts(null);

            if (retObject.getMsg().equals("persist")) {
                return "/internal/processreg";
            } else {
                FacesMessage fm = new FacesMessage(bundle.getString("global_fail"));
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
        } catch (JRException e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(e.getMessage()));
        }
        return null;

    }

    public void prepareUpload() {
        prodAppLetter = new ProdAppLetter();
    }


    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();

        file = event.getFile();
        try {
            if (prodAppLetter == null)
                prodAppLetter = new ProdAppLetter();
            prodAppLetter.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        pOrderDoc.setPipOrder(get);
        prodAppLetter.setFileName(file.getFileName());
        prodAppLetter.setContentType(file.getContentType());
        prodAppLetter.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        prodAppLetter.setRegState(prodApplications.getRegState());
//        userSession.setFile(file);

    }

    public void addDocument() {
        if (prodAppLetters == null)
            prodAppLetters = new ArrayList<ProdAppLetter>();

//        file = userSession.getFile();
//        prodAppLetter.setSuspDetail(suspDetail);q
//        getpOrderDocDAO().save(getpOrderDoc());
        prodAppLetters.add(prodAppLetter);
        suspDetail.setProdAppLetters(prodAppLetters);
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

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

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public List<ProdAppLetter> getProdAppLetters() {
        return prodAppLetters;
    }

    public void setProdAppLetters(List<ProdAppLetter> prodAppLetters) {
        this.prodAppLetters = prodAppLetters;
    }

    public ProdAppLetter getProdAppLetter() {
        return prodAppLetter;
    }

    public void setProdAppLetter(ProdAppLetter prodAppLetter) {
        this.prodAppLetter = prodAppLetter;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }

    public SuspDetail getSuspDetail() {
        return suspDetail;
    }

    public void setSuspDetail(SuspDetail suspDetail) {
        this.suspDetail = suspDetail;
    }

    public SuspComment getSuspComment() {
        return suspComment;
    }

    public void setSuspComment(SuspComment suspComment) {
        this.suspComment = suspComment;
    }

    public List<SuspComment> getSuspComments() {
        return suspComments;
    }

    public void setSuspComments(List<SuspComment> suspComments) {
        this.suspComments = suspComments;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isShowSuspend() {
        if (suspDetail != null && suspDetail.getSuspensionStatus().equals(SuspensionStatus.RESULT))
            showSuspend = true;
        else
            showSuspend = false;

        return showSuspend;
    }

    public void setShowSuspend(boolean showSuspend) {
        this.showSuspend = showSuspend;
    }

    public ProdAppLetterDAO getProdAppLetterDAO() {
        return prodAppLetterDAO;
    }

    public void setProdAppLetterDAO(ProdAppLetterDAO prodAppLetterDAO) {
        this.prodAppLetterDAO = prodAppLetterDAO;
    }

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }
}
