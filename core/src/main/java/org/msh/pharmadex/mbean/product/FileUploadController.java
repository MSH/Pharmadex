package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.ProdAppChecklistDAO;
import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
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
import java.util.ArrayList;

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
    WebSession webSession;

    @Autowired
    ProdAppChecklistDAO prodAppChecklistDAO;

    private ArrayList<Attachment> attachments;
    private UploadedFile file;
    private Attachment attach = new Attachment();

    private ArrayList<Checklist> checklists;
    private ProdAppChecklist prodAppChecklist = new ProdAppChecklist();

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        webSession.setFile(file);
    }

    public void prepareUpload() {
        attach = new Attachment();
    }

    public void addDocument() {
        try {
            ProdApplications prodApplications = processProdBn.getProdApplications();
            file = webSession.getFile();
            FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
            attach.setFile(IOUtils.toByteArray(file.getInputstream()));
            attach.setProdApplications(prodApplications);
            attach.setFileName(file.getFileName());
            attach.setContentType(file.getContentType());
            attach.setUploadedBy(webSession.getUser());
            attach.setRegState(prodApplications.getRegState());
            attachmentDAO.save(attach);
            setAttachments(null);
            webSession.setFile(null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage("Failed", file.getFileName() + " upload failed.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void deleteDoc(Attachment attach) {
        try {
            FacesMessage msg = new FacesMessage("Deleted", attach.getFileName() + " is deleted");
            attachmentDAO.delete(attach);
            attachments = null;
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Failed", file.getFileName() + " cannot be deleted.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public void addModuleDoc() {
        try {
            file = webSession.getFile();
            prodAppChecklist = webSession.getProdAppChecklist();
            prodAppChecklist.setFile(IOUtils.toByteArray(file.getInputstream()));
            prodAppChecklist.setFileName(file.getFileName());
            prodAppChecklist.setContentType(file.getContentType());
            prodAppChecklist.setUploadedBy(webSession.getUser());
            prodAppChecklist.setFileUploaded(true);
            prodAppChecklistDAO.save(prodAppChecklist);
            setChecklists(null);
            webSession.setProdAppChecklist(null);
            webSession.setFile(null);
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage("Failed", file.getFileName() + " upload failed.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
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
        webSession.setProdAppChecklist(prodAppChecklist);
        this.prodAppChecklist = prodAppChecklist;
    }
}
