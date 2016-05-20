package org.msh.pharmadex.service;

import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class ReportService implements Serializable {

	@Autowired
	LetterService letterService;

	@Autowired
	ProductService productService;

	@Autowired
	ProdApplicationsService prodApplicationsService;
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UtilsByReports utilsByReports;

	public JasperPrint reportinit(ProdApplications prodApplications) throws JRException {
		Product product = productService.findProduct(prodApplications.getProduct().getId());

		URL resource = getClass().getResource("/reports/letter.jasper");
		
		HashMap<String, Object> param = null;
		utilsByReports.init(param, prodApplications, product);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		String sub = "Product Registration for  " + (product != null?product.getProdName():"") + " recieved";
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, sub, false);
		
		String body = "Thank you for applying to register " + (product != null?product.getProdName():"") + " manufactured by " + (prodApplications.getApplicant() != null?prodApplications.getApplicant().getAppName():"")
                + ". The application number is " + prodApplications.getProdAppNo() + ". ";
		utilsByReports.putNotNull(UtilsByReports.KEY_BODY, body, true);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_REGISTRAR, "Major General Md Jahangir Hossain Mollik", true);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, "New Medicine Registration", true);
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, "Product application deficiency letter for  ", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
		
		param.put("date", new Date());
		
		return JasperFillManager.fillReport(resource.getFile(), param);
	}

	public JasperPrint generateDeficiency(List<ProdAppChecklist> prodAppChecklists, String comment) throws JRException {
		String emailBody = getEmailBody(prodAppChecklists, comment);
		URL resource = getClass().getResource("/reports/deficiency.jasper");
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		if(prodAppChecklists != null && prodAppChecklists.size() > 0){
			ProdAppChecklist prodAppChecklist = prodAppChecklists.get(0);
			if(prodAppChecklist != null){
				ProdApplications prodApplications = prodAppChecklist.getProdApplications();
				if(prodApplications != null){
					if(prodApplications.getProduct() != null){
						Product product = productService.findProduct(prodApplications.getProduct().getId());

						utilsByReports.init(param, prodApplications, product);
						
						utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_ID, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, "New Medicine Registration", true);
						utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, "Product application deficiency letter for  ", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_BODY, emailBody, true);
						utilsByReports.putNotNull(UtilsByReports.KEY_SUMMARY, comment, true);
						utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
						utilsByReports.putNotNull(UtilsByReports.KEY_REGISTRAR, "Major General Md Jahangir Hossain Mollik", true);
						
						param.put("date", new Date());
					}
				}
			}
		}

		return JasperFillManager.fillReport(resource.getFile(), param);

	}

	private String getEmailBody(List<ProdAppChecklist> prodAppChecklists, String summary) {
		String emailBody;

		emailBody = "<p>The following deficiency were found by module</p>";

		if (prodAppChecklists != null) {
			emailBody += "<p><ul>";
			for (ProdAppChecklist papp : prodAppChecklists) {
				if (papp.isSendToApp() && papp.getChecklist().isHeader()) {
					emailBody += "<li>";
					if(papp.getChecklist()!=null&&papp.getChecklist().getModuleNo()!=null)
						emailBody += "<p><b>" + papp.getChecklist().getModuleNo() + ": ";
					emailBody += papp.getChecklist().getName() + "</b>";
					emailBody += "<br>" + papp.getAppRemark() + "</p>";
					emailBody += "</li>";
				}
			}
			emailBody += "</ul></p>";
		}
		return emailBody;
	}

	public JasperPrint generateSampleRequest(Product product, User user) throws JRException {
		URL resource = getClass().getResource("/reports/sample_request.jasper");

		List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
		ProdApplications prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApplications, product);

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
		param.put("cso", user.getName());
		return JasperFillManager.fillReport(resource.getFile(), param);
	}


	public JasperPrint reportinit() throws JRException, SQLException {
		JasperPrint jasperPrint;
		Connection conn = entityManager.unwrap(Session.class).connection();

		HashMap param = new HashMap();
		param.put("id", new Long(4));
		URL resource = getClass().getResource("/reports/letter.jasper");
		jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
		conn.close();
		return jasperPrint;
	}
}