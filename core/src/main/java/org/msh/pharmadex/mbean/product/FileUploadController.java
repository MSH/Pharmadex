package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.UserService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class FileUploadController implements Serializable {

    @ManagedProperty(value = "#{attachmentDAO}")
    AttachmentDAO attachmentDAO;

    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    @ManagedProperty(value = "#{userService}")
    UserService userService;

    private ArrayList<Attachment> attachments;
    private UploadedFile file;
    private Attachment attach = new Attachment();
    private byte[] invoiceFile;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        ProdApplications prodApplications = processProdBn.getProdApplications();
        try {
            attach.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        attach.setProdApplications(prodApplications);
        attach.setFileName(file.getFileName());
        attach.setContentType(file.getContentType());
        attach.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        attach.setRegState(prodApplications.getRegState());
//        userSession.setFile(file);
    }

    public void prepareUpload() {
        attach = new Attachment();
    }

    public void addDocument() {
        facesContext = FacesContext.getCurrentInstance();
        ProdApplications prodApplications = processProdBn.getProdApplications();
//        file = userSession.getFile();
        FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
        attachmentDAO.save(attach);
        setAttachments(null);
//        userSession.setFile(null);
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void deleteDoc(Attachment attach) {
        try {
            facesContext = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_delete"), attach.getFileName() + resourceBundle.getString("is_deleted"));
            attachmentDAO.delete(attach);
            attachments = null;
            facesContext.addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("cannot_delte"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public StreamedContent fileDownload(Attachment doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public StreamedContent moduleDocDownload(ProdAppChecklist doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public StreamedContent invoiceDownload(Invoice invoice) {
        InputStream ist = new ByteArrayInputStream(invoice.getInvoiceFile());
        Calendar c = Calendar.getInstance();
        StreamedContent download = new DefaultStreamedContent(ist, "pdf", "invoice_" + invoice.getInvoiceNumber() + "_" + c.get(Calendar.YEAR));
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public StreamedContent regCertDownload() {
        ProdApplications prodApplications = processProdBn.getProdApplications();
        InputStream ist = new ByteArrayInputStream(prodApplications.getRegCert());
        Calendar c = Calendar.getInstance();
        StreamedContent download = new DefaultStreamedContent(ist, "pdf", "registration_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR));
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public StreamedContent rejCertDownload() {
        ProdApplications prodApplications = processProdBn.getProdApplications();
        InputStream ist = new ByteArrayInputStream(prodApplications.getRejCert());
        Calendar c = Calendar.getInstance();
        StreamedContent download = new DefaultStreamedContent(ist, "pdf", "rejection_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR));
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public ArrayList<Attachment> getAttachments() {
        if (attachments == null)
            attachments = (ArrayList<Attachment>) attachmentDAO.findByProdApplications_Id(processProdBn.getProdApplications().getId());
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Attachment getAttach() {
        return attach;
    }

    public void setAttach(Attachment attach) {
        this.attach = attach;
    }

    public byte[] getInvoiceFile() {
        return invoiceFile;
    }

    public void setInvoiceFile(byte[] invoiceFile) {
        this.invoiceFile = invoiceFile;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
