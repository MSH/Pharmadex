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
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.SampleTestStatus;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
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
public class SampleTestService implements Serializable {


    @Autowired
    private SampleTestDAO sampleTestDAO;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private GlobalEntityLists globalEntityLists;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SampleTest findSampleTest(Long sampleTestID) {
        SampleTest sampleTest = sampleTestDAO.findOne(sampleTestID);
        Hibernate.initialize(sampleTest.getSampleComments());
        Hibernate.initialize(sampleTest.getProdAppLetters());
        return sampleTest;
    }

    public List<SampleTest> findSampleForProd(Long prodApplicationsId) {
        List<SampleTest> sampleTests = sampleTestDAO.findByProdApplications_Id(prodApplicationsId);
        return sampleTests;

    }

    public RetObject createDefLetter(SampleTest sampleTest){
        ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_samplerequest", ".pdf");
            JasperPrint jasperPrint = initRegCert(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity());
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
//            attachment.setComment(sampleTest.get);
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Sample Request Letter");
            attachment.setUploadedBy(sampleTest.getCreatedBy());
            attachment.setComment("System generated Letter");
            attachment.setLetterType(LetterType.SAMPLE_REQUEST_LETTER);
            attachment.setContentType("application/pdf");
            attachment.setSampleTest(sampleTest);

            if(sampleTest.getProdAppLetters()==null)
                sampleTest.setProdAppLetters(new ArrayList<ProdAppLetter>());
            sampleTest.getProdAppLetters().add(attachment);
            sampleTestDAO.saveAndFlush(sampleTest);
            return saveSample(sampleTest);
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

    public JasperPrint initRegCert(ProdApplications prodApplications, SampleComment sampleComment, String quantity) throws JRException, SQLException {
        String emailBody = sampleComment.getComment();
        Product product = prodApplications.getProduct();
        URL resource = getClass().getResource("/reports/sample_request.jasper");
        Session hibernateSession = entityManager.unwrap(Session.class);
        Connection conn = SessionFactoryUtils.getDataSource(hibernateSession.getSessionFactory()).getConnection();
        HashMap param = new HashMap();
        List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
        prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
        param.put("id", prodApplications.getId());
        param.put("sampleQty", quantity);
        param.put("appName", prodApplications.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength()+product.getDosUnit());
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


    public RetObject saveSample(SampleTest sampleTest) {

        RetObject retObject = new RetObject();
        try {
            if (sampleTest.getSampleTestStatus().equals(SampleTestStatus.REQUESTED)) {
                if (sampleTest.getRecievedDt() != null) {
                    sampleTest.setSampleTestStatus(SampleTestStatus.SAMPLE_RECIEVED);
                }
            } else if (sampleTest.getSampleTestStatus().equals(SampleTestStatus.SAMPLE_RECIEVED)) {
                sampleTest.setSampleTestStatus(SampleTestStatus.RESULT);
            }
            SampleTest sampleTest1 = sampleTestDAO.save(sampleTest);
            retObject.setObj(sampleTest1);
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setObj(ex.getMessage());
            retObject.setMsg("error");
        }
        return retObject;
    }

    public RetObject addNewTest(SampleTest sampleTest) {

        return saveSample(sampleTest);

    }
}
