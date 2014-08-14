package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.ProdAppChecklistDAO;
import org.msh.pharmadex.domain.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class FileUploadController {

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    ProcessProdBn processProdBn;

    @Autowired
    UserSession userSession;

    @Autowired
    ProdAppChecklistDAO prodAppChecklistDAO;

    private ArrayList<Attachment> attachments;
    private UploadedFile file;
    private Attachment attach = new Attachment();
    private byte[] invoiceFile;

    private ArrayList<Checklist> checklists;
    private ProdAppChecklist prodAppChecklist = new ProdAppChecklist();

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        userSession.setFile(file);
    }

    public void prepareUpload() {
        attach = new Attachment();
    }

    public void addDocument() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            ProdApplications prodApplications = processProdBn.getProdApplications();
            file = userSession.getFile();
            FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
            attach.setFile(IOUtils.toByteArray(file.getInputstream()));
            attach.setProdApplications(prodApplications);
            attach.setFileName(file.getFileName());
            attach.setContentType(file.getContentType());
            attach.setUploadedBy(userSession.getLoggedInUserObj());
            attach.setRegState(prodApplications.getRegState());
            attachmentDAO.save(attach);
            setAttachments(null);
            userSession.setFile(null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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

    public void addModuleDoc() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            file = userSession.getFile();
            prodAppChecklist = userSession.getProdAppChecklist();
            prodAppChecklist.setFile(IOUtils.toByteArray(file.getInputstream()));
            prodAppChecklist.setFileName(file.getFileName());
            prodAppChecklist.setContentType(file.getContentType());
            prodAppChecklist.setUploadedBy(userSession.getLoggedInUserObj());
            prodAppChecklist.setFileUploaded(true);
            prodAppChecklistDAO.save(prodAppChecklist);
            setChecklists(null);
            userSession.setProdAppChecklist(null);
            userSession.setFile(null);
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
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

    public ArrayList<Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(ArrayList<Checklist> checklists) {
        this.checklists = checklists;
    }

    public ProdAppChecklist getProdAppChecklist() {
        return prodAppChecklist;
    }

    public void setProdAppChecklist(ProdAppChecklist prodAppChecklist) {
        userSession.setProdAppChecklist(prodAppChecklist);
        this.prodAppChecklist = prodAppChecklist;
    }

    public byte[] getInvoiceFile() {
        return invoiceFile;
    }

    public void setInvoiceFile(byte[] invoiceFile) {
        this.invoiceFile = invoiceFile;
    }
}
