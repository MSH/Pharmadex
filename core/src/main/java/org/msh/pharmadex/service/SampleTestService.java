/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.SampleTestStatus;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleMed;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Author: usrivastava
 */
@Service
public class SampleTestService implements Serializable {

	private static final long serialVersionUID = -4443722066709166235L;

	@Autowired
	private SampleTestDAO sampleTestDAO;

	@Autowired
	private ProdApplicationsService prodApplicationsService;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UtilsByReports utilsByReports;

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
			File invoicePDF = File.createTempFile("" + product.getProdName() + "_samplerequest", ".pdf");
			JasperPrint jasperPrint = initRegCert(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity());
			net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
			byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
			ProdAppLetter attachment = new ProdAppLetter();
			attachment.setRegState(prodApp.getRegState());
			attachment.setFile(file);
			attachment.setProdApplications(prodApp);
			attachment.setFileName(invoicePDF.getName());
			attachment.setTitle("Sample Request Letter");
			attachment.setUploadedBy(sampleTest.getCreatedBy());
			attachment.setComment("System generated Letter");
			attachment.setLetterType(LetterType.SAMPLE_REQUEST_LETTER);
			attachment.setContentType("application/pdf");
			//            attachment.setSampleTest(sampleTest);

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
		Product product = prodApplications.getProduct();
		URL resource = getClass().getResource("/reports/sample_request.jasper");
		Connection conn = entityManager.unwrap(Session.class).connection();
		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);
		utilsByReports.putNotNull(UtilsByReports.KEY_ID, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SAMPLEQTY, quantity, true);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, "New Medicine Registration", true);
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, "Sample request letter for  ", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
		
		param.put("date", new Date());

		if(resource != null){
			JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
			conn.close();
			return jasperPrint;
		} else{
			conn.close();
			return null;
		}
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

	@Transactional
	public DosageForm findDosQuantity(Long dosFormID) {
		DosageForm dosageForm = null;
		if(dosFormID!=null) {
			ProdApplications prodApplications = prodApplicationsService.findProdApplications(dosFormID);
			dosageForm = prodApplications.getProduct().getDosForm();
		}
		return dosageForm;
	}

	public RetObject addSampleMed(SampleMed sampleMed) {
		return null;
	}

	@Autowired
	private ProdAppLetterDAO prodAppLetterDAO;
	public ProdAppLetter saveProdAppLetter(ProdAppLetter prodAppLetter) {
		return prodAppLetterDAO.save(prodAppLetter);
	}
}
