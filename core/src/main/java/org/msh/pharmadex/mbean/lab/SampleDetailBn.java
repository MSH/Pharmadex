/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.lab;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.SampleTestStatus;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.SampleTestService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
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
public class SampleDetailBn implements Serializable {


    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{sampleTestService}")
    private SampleTestService sampleTestService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private UploadedFile file;
    private SampleTest sampleTest;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private SampleComment sampleComment;
    private List<SampleComment> sampleComments;
    private List<ProdAppLetter> prodAppLetters;
    private ProdAppLetter prodAppLetter;

    @PostConstruct
    private void init() {
        try {
            if (sampleTest == null) {
                Long sampleTestID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sampleTestID"));
                if (sampleTestID != null) {
                    sampleTest = sampleTestService.findSampleTest(sampleTestID);
                    sampleComments = sampleTest.getSampleComments();
                    prodAppLetters = sampleTest.getProdAppLetters();
                    prodApplications = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
//                if (reviewStatus.equals(ReviewStatus.SUBMITTED) || reviewStatus.equals(ReviewStatus.ACCEPTED)) {
//                    readOnly = true;
//                }
                }
            }
        }catch (Exception ex){
        }
    }

    @Transactional
    public void submitComment() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            SampleTest sampleTestFrmDB = sampleTestService.findSampleTest(sampleTest.getId());
            sampleComments = sampleTestFrmDB.getSampleComments();
            if (sampleComments == null) {
                sampleComments = new ArrayList<SampleComment>();
                sampleTest.setSampleComments(sampleComments);
            }

            sampleComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
            sampleComment.setDate(new Date());
            sampleComment.setSampleTest(sampleTest);
            if (sampleTest.getSampleTestStatus() == null) {
                sampleTest.setSampleTestStatus(SampleTestStatus.IN_PROGRESS);
            }
            sampleComment.setSampleTestStatus(sampleTest.getSampleTestStatus());

            sampleTest.setUpdatedDate(new Date());
            sampleTest.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));

            sampleTest.getSampleComments().add(sampleComment);
            RetObject retObject = sampleTestService.saveSample(sampleTest);
            if (retObject.getMsg().equals("success")) {
                sampleTest = (SampleTest) retObject.getObj();
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));

            } else if (retObject.getMsg().equals("close_def")) {
                facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
    }

    public StreamedContent fileDownload(ProdAppLetter doc) {
        ProdAppLetter prodAppLetter = sampleTest.getProdAppLetters().get(0);
        InputStream ist = new ByteArrayInputStream(prodAppLetter.getFile());
        StreamedContent download = new DefaultStreamedContent(ist, prodAppLetter.getContentType(), prodAppLetter.getFileName());
        return download;
    }

    public void initComment() {
         sampleComment = new SampleComment();
    }

    public String saveReview() {
        RetObject retObject = sampleTestService.saveSample(sampleTest);
        sampleTest = (SampleTest) retObject.getObj();
        return "";
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
//        file = userSession.getFile();
//        prodAppLetter.setSampleTest(sampleTest);
//        getpOrderDocDAO().save(getpOrderDoc());
        prodAppLetters.add(prodAppLetter);
        sampleTest.setProdAppLetters(prodAppLetters);
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

    public SampleTestService getSampleTestService() {
        return sampleTestService;
    }

    public void setSampleTestService(SampleTestService sampleTestService) {
        this.sampleTestService = sampleTestService;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
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

    public SampleComment getSampleComment() {
        return sampleComment;
    }

    public void setSampleComment(SampleComment sampleComment) {
        this.sampleComment = sampleComment;
    }

    public List<SampleComment> getSampleComments() {
        return sampleComments;
    }

    public void setSampleComments(List<SampleComment> sampleComments) {
        this.sampleComments = sampleComments;
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
}
