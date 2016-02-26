package org.msh.pharmadex.mbean;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.service.CurrencyService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.service.UserService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
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
    //    private POrderBase pOrderBase;
    private User applicantUser;
    private Applicant applicant;
    private POrderDoc pOrderDoc;
    private ArrayList<POrderDoc> pOrderDocs;
    private UploadedFile file;

    public void prepareUpload() {
        pOrderDoc = new POrderDoc();
    }

    public StreamedContent fileDownload(POrderDoc doc) {
        InputStream ist = new ByteArrayInputStream(doc.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, doc.getContentType(), doc.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void deleteDoc(POrderDoc attach) {
        try {
            context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(bundle.getString("global_delete"), attach.getFileName() + bundle.getString("is_deleted"));
            pOrderService.delete(pOrderDoc);
            pOrderDocs = null;
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
        try {
            if(pOrderDoc==null)
                pOrderDoc = new POrderDoc();
            pOrderDoc.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(bundle.getString("global_fail"), file.getFileName() + bundle.getString("upload_fail"));
            context.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        pOrderDoc.setPipOrder(get);
        pOrderDoc.setFileName(file.getFileName());
        pOrderDoc.setContentType(file.getContentType());
        pOrderDoc.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        pOrderDoc.setRegState(AmdmtState.NEW_APPLICATION);
//        userSession.setFile(file);
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
