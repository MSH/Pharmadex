package org.msh.pharmadex.mbean;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.StrTools;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 *
 */
public abstract class POrderBn implements Serializable {

    @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    protected UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    protected GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{currencyService}")
    protected CurrencyService currencyService;

    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    private User applicantUser;
    private Applicant applicant;
    private POrderDoc pOrderDoc;
    private ArrayList<POrderDoc> pOrderDocs;
    private UploadedFile file;

    public void prepareUpload() {
        pOrderDoc = new POrderDoc();
    }

    public StreamedContent fileDownload(POrderDoc doc) {
        StreamedContent download = null;
        if (AttachmentService.attStoresInDb()) {
            InputStream ist = new ByteArrayInputStream(doc.getFile());
            download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
        }else{
            String fileName = pOrderService.extractAttFileName(doc);
            try {
                FileInputStream ist = new FileInputStream(fileName);
                download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                download=null;
            }

        }
        return download;
    }

    public void deleteDoc(POrderDoc attach) {
        try {
            context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(bundle.getString("global_delete"), attach.getFileName() + bundle.getString("is_deleted"));
            String res = pOrderService.delete(pOrderDoc);
            if ("".equals(res))
                pOrderDocs = null;
            else
                msg = new FacesMessage(bundle.getString("global_fail"), attach.getFileName() + res);
            context.addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"), attach.getFileName() + bundle.getString("cannot_delte"));
            context.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public abstract void currChangeListener();

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        String fileName="";
        try {
            if(pOrderDoc==null)
                pOrderDoc = new POrderDoc();
            if (AttachmentService.attStoresInDb()){
                pOrderDoc.setFileName(file.getFileName());
                pOrderDoc.setFile(IOUtils.toByteArray(file.getInputstream()));
                fileName = file.getFileName();
            }else{
                fileName = AttachmentService.save(file.getInputstream(),file.getFileName());
                pOrderDoc.setFile(IOUtils.toByteArray(fileName));
            }
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            context.addMessage(null, msg);
            e.printStackTrace();
        }
        pOrderDoc.setFileName(fileName);
        pOrderDoc.setContentType(file.getContentType());
        pOrderDoc.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        pOrderDoc.setRegState(AmdmtState.NEW_APPLICATION);
    }

    public abstract void addDocument();

    protected abstract List<PIPOrderLookUp> findAllChecklists();

    public abstract void initAddProd();

    public abstract void addProd();

    public abstract void cancelAddProd();

    public String saveOrder(){
        pOrderService.save(pOrderDocs);
        return "persist";
    }

    public abstract String cancelOrder();

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

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public POrderDoc getpOrderDoc() {
        return pOrderDoc;
    }

    public void setpOrderDoc(POrderDoc pOrderDoc) {
        this.pOrderDoc = pOrderDoc;
    }

    public ArrayList<POrderDoc> getpOrderDocs() {
        if (pOrderDocs == null) {
            pOrderDocs = findPOrdersDocs();
        }
        return pOrderDocs;
    }

    public void setpOrderDocs(ArrayList<POrderDoc> pOrderDocs) {
        this.pOrderDocs = pOrderDocs;
    }

    protected abstract ArrayList<POrderDoc> findPOrdersDocs();

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
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

    public CurrencyService getCurrencyService() {
        return currencyService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }



}
