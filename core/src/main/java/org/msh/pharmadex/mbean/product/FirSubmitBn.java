package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.lab.SampleMed;
import org.msh.pharmadex.domain.lab.SampleStd;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class FirSubmitBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(FirSubmitBn.class);

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{userService}")
    UserService userService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{reviewService}")
    ReviewService reviewService;

    @ManagedProperty(value = "#{attachmentDAO}")
    AttachmentDAO attachmentDAO;

    private ProdApplications prodApplications;
    private User loggedInUser;
    private RevDeficiency revDeficiency;
    private Attachment attachment;
    private ArrayList<Attachment> attachments;
    private UploadedFile file;
    private ReviewComment reviewComment;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @PostConstruct
    private void init() {
        try {
            if (revDeficiency == null) {
                Long revDefID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("revDefID"));
                if (revDefID != null) {
                    revDeficiency = reviewService.findRevDef(revDefID);
                    prodApplications = prodApplicationsService.findProdApplications(revDeficiency.getReviewInfo().getProdApplications().getId());
                }
                loggedInUser = userService.findUser(userSession.getLoggedINUserID());
                reviewComment = new ReviewComment();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public StreamedContent fileDownload(Attachment doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void addDocument() {
        facesContext = FacesContext.getCurrentInstance();
        FacesMessage msg;
//        file = userSession.getFile();
        if (file != null) {
            msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
            attachment = attachmentDAO.save(attachment);
            setAttachments(null);
//        userSession.setFile(null);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select a file to upload.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        try {
            attachment.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        attachment.setProdApplications(prodApplications);
        attachment.setFileName(file.getFileName());
        attachment.setContentType(file.getContentType());
        attachment.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        attachment.setRegState(prodApplications.getRegState());
//        userSession.setFile(file);
    }

    public void deleteDoc(Attachment attach) {
        try {
            facesContext = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_delete"), attach.getFileName() + resourceBundle.getString("is_deleted"));
            attachmentDAO.delete(attach);
            attachments = null;
            facesContext.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("cannot_delte"));
            facesContext.addMessage(null, msg);
        }
    }

    public void prepareUpload() {
        attachment = new Attachment();
    }

    public String submitRevDef() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            String result = reviewService.submitFir(revDeficiency, reviewComment, userSession.getLoggedINUserID());
            if(result.equals("persist")) {
                facesContext.addMessage(null, new FacesMessage("global.success"));
                return "/internal/processreg";
            }else{
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "global_fail", result));
                return null;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
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

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public RevDeficiency getRevDeficiency() {
        return revDeficiency;
    }

    public void setRevDeficiency(RevDeficiency revDeficiency) {
        this.revDeficiency = revDeficiency;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
    }

    public ReviewComment getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(ReviewComment reviewComment) {
        this.reviewComment = reviewComment;
    }
}
