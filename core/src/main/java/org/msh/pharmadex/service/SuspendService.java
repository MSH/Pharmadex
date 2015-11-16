/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.SuspendDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class SuspendService implements Serializable {


    @Autowired
    private SuspendDAO suspendDAO;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TimelineService timelineService;

    @Transactional
    public SuspDetail findSuspendDetail(Long suspDetailID) {
        SuspDetail suspDetail = suspendDAO.findOne(suspDetailID);
        if (suspDetail.getSuspComments() != null)
            Hibernate.initialize(suspDetail.getSuspComments());
        if (suspDetail.getProdAppLetters() != null)
            Hibernate.initialize(suspDetail.getProdAppLetters());
        return suspDetail;
    }

    public List<SuspDetail> findSuspendByProd(Long prodApplicationsId) {
        List<SuspDetail> suspDetails = suspendDAO.findByProdApplications_Id(prodApplicationsId);
        return suspDetails;

    }

    public RetObject createSuspLetter(SuspDetail suspDetail) {
        ProdApplications prodApp = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_samplerequest", ".pdf");
            JasperPrint jasperPrint = initRegCert(prodApp, suspDetail.getSuspComments().get(0));
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
//            attachment.setComment(sampleTest.get);
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Sample Request Letter");
            attachment.setUploadedBy(suspDetail.getCreatedBy());
            attachment.setComment("System generated Letter");
            attachment.setLetterType(LetterType.SAMPLE_REQUEST_LETTER);
            attachment.setContentType("application/pdf");
            attachment.setSuspDetail(suspDetail);

            if (suspDetail.getProdAppLetters() == null)
                suspDetail.setProdAppLetters(new ArrayList<ProdAppLetter>());
            suspDetail.getProdAppLetters().add(attachment);
            suspendDAO.saveAndFlush(suspDetail);
            return saveSuspend(suspDetail);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");
        }
    }

    public JasperPrint initRegCert(ProdApplications prodApplications, SuspComment suspComment) throws JRException, SQLException {
        String emailBody = suspComment.getComment();
        Product product = prodApplications.getProduct();
        URL resource = getClass().getResource("/reports/suspend_request.jasper");
//        Session hibernateSession = entityManager.unwrap(Session.class);
        Connection conn = entityManager.unwrap(Session.class).connection();
        HashMap param = new HashMap();
        List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
        prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
        param.put("id", prodApplications.getId());
        param.put("appName", prodApplications.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength() + product.getDosUnit());
        param.put("dosForm", product.getDosForm().getDosForm());
        param.put("manufName", product.getManufName());
        param.put("appType", "New Medicine Registration");
        param.put("subject", "Sample request letter for  " + product.getProdName());
        param.put("address1", prodApplications.getApplicant().getAddress().getAddress1());
        param.put("address2", prodApplications.getApplicant().getAddress().getAddress2());
        param.put("country", prodApplications.getApplicant().getAddress().getCountry().getCountryName());
//        param.put("cso",user.getName());
        param.put("date", new Date());
        param.put("appNumber", prodApplications.getProdAppNo());

        JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
        conn.close();


        return jasperPrint;
    }

    public RetObject saveSuspend(SuspDetail suspDetail) {
        if (suspDetail.getSuspNo() == null || suspDetail.getSuspNo().equals("")) {
            RegistrationUtil registrationUtil = new RegistrationUtil();
            String suspNo = registrationUtil.generateAppNo(suspDetail.getProdApplications().getId(), "SUS");
            suspDetail.setSuspNo(suspNo);
        }
        RetObject retObject = new RetObject();
        try {
            suspDetail = suspendDAO.save(suspDetail);
            retObject.setObj(suspDetail);
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setObj(ex.getMessage());
            retObject.setMsg("error");
        }
        return retObject;
    }

    public RetObject addNewSusp(SuspDetail suspDetail) {
        return saveSuspend(suspDetail);

    }

    public List<SuspDetail> findAll(UserSession userSession) {
        List<SuspDetail> suspDetails = null;
        if (userSession.isHead()) {
            suspDetails = suspendDAO.findAll();
        }
        if (userSession.isModerator()) {
            suspDetails = suspendDAO.findByModerator_UserId(userSession.getLoggedINUserID());
        }
        if (userSession.isReviewer()) {
            suspDetails = suspendDAO.findByReviewer_UserId(userSession.getLoggedINUserID());
        }
        return suspDetails;

    }

    @Transactional
    public RetObject suspendProduct(SuspDetail suspDetail, User loggedInUser) {
        suspDetail.setComplete(true);
        if (suspDetail.getSuspEndDate() == null)
            suspDetail.setCanceled(true);

        ProdApplications prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
        prodApplications.setRegState(suspDetail.getDecision());
        prodApplications.setRegExpiryDate(suspDetail.getSuspStDate());
        prodApplicationsService.updateProdApp(prodApplications, loggedInUser.getUserId());

        TimeLine timeLine = new TimeLine();
        timeLine.setRegState(suspDetail.getDecision());
        timeLine.setComment("The application is suspended with Suspension number " + suspDetail.getSuspNo());
        timeLine.setUser(loggedInUser);
        timeLine.setStatusDate(new Date());
        timeLine.setProdApplications(prodApplications);
        timelineService.saveTimeLine(timeLine);

        saveSuspend(suspDetail);

        return new RetObject("persist", suspDetail);


    }
}
