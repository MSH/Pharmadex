/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.lab;

import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.SampleTestStatus;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.SampleTestService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class SampleTestBn implements Serializable {


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

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    private List<SampleTest> sampleTests;
    private SampleTest sampleTest;
    private SampleComment sampleComment;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @PostConstruct
    public void init(){
        if(sampleTests==null){
            if(processProdBn.getProdApplications()!=null){
                sampleTests = sampleTestService.findSampleForProd(processProdBn.getProdApplications().getId());
                sampleTest = new SampleTest(processProdBn.getProdApplications());
            }
        }
    }

    public void addSample() {
    	// by used in countries
    	createSample();

        RetObject riRetObj = sampleTestService.createDefLetter(sampleTest);
        if (!riRetObj.getMsg().equalsIgnoreCase("persist")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        } else {
            sampleTests.add(sampleTest);

        }
        sampleTest = new SampleTest();
    }
    
    public void createSample(){
    	facesContext = FacesContext.getCurrentInstance();
        ProdApplications prodApplications = processProdBn.getProdApplications();

        if (sampleTest == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        }

        if (sampleTests == null) {
            sampleTests = new ArrayList<SampleTest>();
        }

        sampleTest.setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
        sampleTest.setCreatedDate(Calendar.getInstance().getTime());
        sampleTest.setSampleTestStatus(SampleTestStatus.REQUESTED);
        sampleTest.setReqDt(new Date());
        sampleComment.setSampleTestStatus(SampleTestStatus.REQUESTED);
        sampleComment.setSampleTest(sampleTest);
        sampleComment.setDate(new Date());
        sampleComment.setUser(sampleTest.getCreatedBy());
        sampleTest.getSampleComments().add(sampleComment);

    }

    public void initSampleAdd() {
        sampleTest.setSampleComments(new ArrayList<SampleComment>());
        ProdApplications prodApplications = processProdBn.getProdApplications();
        sampleTest.setProdApplications(prodApplications);
        DosageForm dosForm = sampleTestService.findDosQuantity(sampleTest.getProdApplications().getId());
        String str = (dosForm.getSampleSize() != null ?dosForm.getSampleSize():"") + " " + (dosForm.getDosForm() != null ?dosForm.getDosForm():"");
        sampleTest.setQuantity(str);
        sampleComment = new SampleComment(sampleTest);
    }

    public void createConfirmLetter(){
        ProdApplications prodapp = processProdBn.getProdApplications();
        if (prodapp!=null){
            List<ProdAppLetter> letters = processProdBn.getLetters();
            ProdAppLetter letter = null;
            if (letters!=null){
                for(ProdAppLetter let:letters){
                       if (let.getLetterType() != null && let.getLetterType().equals(LetterType.SAMPLE_TEST_RESULT)){
                           letter = let;
                           break;
                       }
                }
            }
            if (letter==null){

            }
        }

    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public SampleTestService getSampleTestService() {
        return sampleTestService;
    }

    public void setSampleTestService(SampleTestService sampleTestService) {
        this.sampleTestService = sampleTestService;
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public List<SampleTest> getSampleTests() {
        return sampleTests;
    }

    public void setSampleTests(List<SampleTest> sampleTests) {
        this.sampleTests = sampleTests;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public SampleComment getSampleComment() {
        return sampleComment;
    }

    public void setSampleComment(SampleComment sampleComment) {
        this.sampleComment = sampleComment;
    }
}
