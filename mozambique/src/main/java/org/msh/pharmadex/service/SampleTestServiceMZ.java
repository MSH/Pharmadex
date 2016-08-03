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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Author: dudchenko
 */
@Service
public class SampleTestServiceMZ implements Serializable {

	private static final long serialVersionUID = 5328007438726352679L;

	@Autowired
	private SampleTestDAO sampleTestDAO;

	@Autowired
	private ProdApplicationsService prodApplicationsService;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UtilsByReportsMZ utilsByReports;
	
	@Autowired
	private SampleTestService sampleTestService;

	public RetObject createSampleReqLetter(SampleTest sampleTest){
		ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
		Product product = prodApp.getProduct();
		try {
			File invoicePDF = File.createTempFile("" + product.getProdName() + "_samplerequest", ".pdf");
			JasperPrint jasperPrint = initSampleReq(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity());
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

			if(sampleTest.getProdAppLetters() == null)
				sampleTest.setProdAppLetters(new ArrayList<ProdAppLetter>());
			sampleTest.getProdAppLetters().add(attachment);
			sampleTestDAO.saveAndFlush(sampleTest);
			return sampleTestService.saveSample(sampleTest);
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

	public JasperPrint initSampleReq(ProdApplications prodApplications, SampleComment sampleComment, String quantity) throws JRException, SQLException {
		Product product = prodApplications.getProduct();
		URL resource = getClass().getResource("/reports/sample_request.jasper");
		//Connection conn = entityManager.unwrap(Session.class).connection();
		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPADDRESS, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_SUBJECT, "Sample request letter for  ", true);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPTYPE, "New Medicine Registration", true);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNUM, "", false);
		
		
		/*utilsByReports.putNotNull(UtilsByReportsMZ.KEY_ID, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_SAMPLEQTY, quantity, true);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_ADDRESS2, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_COUNTRY, "", false);*/

		param.put("date", new Date());

		if(resource != null){
			JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));
			//conn.close();
			return jasperPrint;
		}
		return null;
	}

	
}
