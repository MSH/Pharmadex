package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.WebSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.Attachment;
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

    private ArrayList<Attachment> attachments;
    private UploadedFile file;
    Attachment attach = new Attachment();

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        file = event.getFile();
        webSession.setFile(file);
    }


    public void addDocument() {
        try {
            file = webSession.getFile();
            attach.setFile(IOUtils.toByteArray(file.getInputstream()));
            attach.setProdApplications(processProdBn.getProdApplications());
            attach.setFileName(file.getFileName());
            attach.setContentType(file.getContentType());
            attach.setUploadedBy(webSession.getUser());
            attachmentDAO.save(attach);
            setAttachments(null);
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
}
