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
import org.msh.pharmadex.domain.User;
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
	private UtilsByReports utilsByReports;
	
	@Autowired
	private SampleTestService sampleTestService;	

	@Autowired
	UserService userService;
		 
	public RetObject createSampleReqLetter(SampleTest sampleTest,Long loggedINUserID  ){
		ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
		Product product = prodApp.getProduct();
		try {
			File invoicePDF = File.createTempFile("" + product.getProdName().split(" ")[0] + "_samplerequest", ".pdf");
			JasperPrint jasperPrint = initSampleReq(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity(),loggedINUserID);
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

	public JasperPrint initSampleReq(ProdApplications prodApplications, SampleComment sampleComment, String quantity, Long loggedINUserID ) throws JRException, SQLException {
		Product product = prodApplications.getProduct();
		URL resource = getClass().getResource("/reports/sample_request.jasper");
		//Connection conn = entityManager.unwrap(Session.class).connection();
		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, "Sample request letter for  ", true);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, "New Medicine Registration", true);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
				
		
		utilsByReports.putNotNull(UtilsByReports.KEY_APPPOST, "", false);	
		utilsByReports.putNotNull(UtilsByReports.KEY_PROD_DETAILS, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_MODINITIALS, "", false);
				
		if(loggedINUserID!=null){
			User curuser = userService.findUser(loggedINUserID);			
			if(curuser!=null){
				String res = "";
				if (curuser.getName() != null) {					
					if (!curuser.getName().equals("Sultana Razaco")) {
						String[] in = curuser.getName().split(" ");
						for (String item : in) {
							res += item.substring(0, 1).toLowerCase();
						}							
					}
				}
				utilsByReports.putNotNull(UtilsByReports.KEY_SCRINITIALS, res, true);
			}
		}
		if(prodApplications.getApplicant() != null){
			if(prodApplications.getApplicant().getContactName()!=null){
				String contName = prodApplications.getApplicant().getContactName();					
				User respUser = userService.findUserByUsername(contName);	
				if(respUser!=null){
					String res = respUser.getName()!=null?respUser.getName():"";						
					utilsByReports.putNotNull(UtilsByReports.KEY_APPRESPONSIBLE,res, true);		
				}								
			}
		}
		
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
		/*utilsByReports.putNotNull(UtilsByReports.KEY_ID, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SAMPLEQTY, quantity, true);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);*/

		param.put("date", new Date());

		if(resource != null){
			JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));
			//conn.close();
			return jasperPrint;
		}
		return null;
	}

	
}
