/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.RevDeficiency;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
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

    public SampleTest findSampleTest(SampleTest sampleTest) {
        sampleTest = sampleTestDAO.findOne(sampleTest.getId());
//        List<ReviewChecklist> reviewChecklists = review.getReviewChecklists();
//        if (reviewChecklists.size() < 1) {
//            reviewChecklists = new ArrayList<ReviewChecklist>();
//            review.setReviewChecklists(reviewChecklists);
//            List<Checklist> allChecklist = checklistService.getChecklists(review.getProdApplications().getProdAppType(), true);
//            ReviewChecklist eachReviewChecklist;
//            for (int i = 0; allChecklist.size() > i; i++) {
//                eachReviewChecklist = new ReviewChecklist();
//                eachReviewChecklist.setChecklist(allChecklist.get(i));
//                eachReviewChecklist.setReview(review);
//                reviewChecklists.add(eachReviewChecklist);
//            }
//            review.setReviewChecklists(reviewChecklists);
//        }
        return sampleTest;
    }

    public SampleTest findSampleForProd(Long prodApplicationsId) {
        List<SampleTest> sampleTests = sampleTestDAO.findByProdApplications_Id(prodApplicationsId);
        if (sampleTests.size() > 0)
            return sampleTests.get(0);
        else
            return null;

    }
    public RetObject createDefLetter(SampleTest sampleTest){
        ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_deficiency", ".pdf");
            JasperPrint jasperPrint = initRegCert(prodApp, sampleTest.getSampleComments().get(0));
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
//            attachment.setComment(sampleTest.get);
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Review Deficiency Letter");
            attachment.setUploadedBy(sampleTest.getCreatedBy());
            attachment.setComment("Automatically generated Letter");
            attachment.setContentType("application/pdf");
            sampleTest.setProdAppLetter(attachment);
            sampleTestDAO.saveAndFlush(sampleTest);
            return saveSample(sampleTest);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    public JasperPrint initRegCert(ProdApplications prodApplications, SampleComment sampleComment) throws JRException {
        String emailBody = sampleComment.getComment();
        Product product = prodApplications.getProduct();
        URL resource = getClass().getResource("/reports/sample_request.jasper");
        HashMap param = new HashMap();
        prodApplications = prodApplicationsService.findProdApplicationByProduct(product.getId());
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

        return JasperFillManager.fillReport(resource.getFile(), param);
    }


    public RetObject saveSample(SampleTest sampleTest) {

        RetObject retObject = new RetObject();
        try {
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
