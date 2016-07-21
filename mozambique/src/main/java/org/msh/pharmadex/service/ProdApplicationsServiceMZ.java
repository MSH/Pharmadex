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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 */
@Service
@Transactional
public class ProdApplicationsServiceMZ implements Serializable {

	private static final long serialVersionUID = 4431326838232306604L;

	private java.util.ResourceBundle bundle;
	private FacesContext context;

	@Resource
	private ProdApplicationsDAO prodApplicationsDAO;
	@Autowired
	private WorkspaceDAO workspaceDAO;
	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private ProdAppLetterDAO prodAppLetterDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ProdApplicationsService prodApplicationsService;

	@Autowired
	private UtilsByReportsMZ utilsByReports;

	private ProdApplications prodApp;
	private Product product;

	/**
	 * Applications are in states by role users
	 * They do not have other conditionals
	 */
	public List<ProdApplications> getSubmittedApplications(UserSession userSession) {
		List<ProdApplications> prodApplicationses = null;
		HashMap<String, Object> params = new HashMap<String, Object>();

		List<RegState> regState = new ArrayList<RegState>();
		if (userSession.isAdmin()) {
			regState.add(RegState.FEE);
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
		} 
		if (userSession.isModerator()) {
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			//params.put("moderatorId", userSession.getLoggedINUserID());
		} 
		if (userSession.isReviewer()) {
			regState.add(RegState.REVIEW_BOARD);
			/*if (workspaceDAO.findAll().get(0).isDetailReview()) {
				params.put("reviewer", userSession.getLoggedINUserID());
				return prodApplicationsDAO.findProdAppByReviewer(params);
			} else
				params.put("reviewerId", userSession.getLoggedINUserID());*/
		} 
		if (userSession.isHead()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.FEE);
			regState.add(RegState.VERIFY);
			regState.add(RegState.SCREENING);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.NOT_RECOMMENDED);
			regState.add(RegState.REJECTED);
		} 
		if (userSession.isStaff()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.SCREENING);
			regState.add(RegState.FEE);
			regState.add(RegState.VERIFY);
			regState.add(RegState.FOLLOW_UP);
		}
		if (userSession.isCompany()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.FEE);
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
			regState.add(RegState.REGISTERED);
			regState.add(RegState.CANCEL);
			regState.add(RegState.SUSPEND);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.NOT_RECOMMENDED);
			regState.add(RegState.REJECTED);
			params.put("userId", userSession.getLoggedINUserID());
		}
		if (userSession.isLab()) {
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.NOT_RECOMMENDED);
		}
		if (userSession.isClinical()) {
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.FOLLOW_UP);
			params.put("prodAppType", ProdAppType.NEW_CHEMICAL_ENTITY);
		}

		params.put("regState", regState);
		prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);

		if (userSession.isModerator() || userSession.isReviewer() || userSession.isHead() || userSession.isLab()) {
			Collections.sort(prodApplicationses, new Comparator<ProdApplications>() {
				@Override
				public int compare(ProdApplications o1, ProdApplications o2) {
					Date d1 = o1.getPriorityDate();
					Date d2 = o2.getPriorityDate();
					if(d1 != null && d2 != null)
						return d1.compareTo(d2);
					return 0;
				}
			});
		}

		return prodApplicationses;
	}

	@Transactional
	public String registerProd(ProdApplications prodApp) {
		this.prodApp = prodApp;
		return prodApplicationsService.registerProd(this.prodApp);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createRegCert(ProdApplications prodApp, String gestorDeCTRM) throws IOException, JRException, SQLException {
		this.prodApp = prodApp;
		this.product = prodApp.getProduct();
		File invoicePDF = null;
		invoicePDF = File.createTempFile("" + product.getProdName() + "_registration", ".pdf");
		JasperPrint jasperPrint = initRegCert(gestorDeCTRM);
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
		prodApp.setRegCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
		prodApplicationsDAO.updateApplication(prodApp);
		return "created";
	}

	public JasperPrint initRegCert(String gestor) throws JRException, SQLException {
		product = productDAO.findProduct(prodApp.getProduct().getId());

		Connection conn = entityManager.unwrap(Session.class).connection();
		URL resource = getClass().getResource("/reports/reg_letter.jasper");

		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApp, product);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNUMBER, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_REG_NUMBER, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_SUBMIT_DATE, "", false);

		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_SHELFINE, "", false);

		//inn
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_INN, "", false);

		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PACKSIZE, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_STORAGE, "", false);
		//excipient
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_EXCIPIENT, "", false);

		String fnm = (product != null) ? product.getFnm():"";
		boolean flag = (fnm != null && fnm.length() > 0) ? true: false;
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_FNM, flag);

		int t = 0;
		if(prodApp != null){
			ProdAppType type = prodApp.getProdAppType();
			if(type != null){
				if(type.equals(ProdAppType.NEW_CHEMICAL_ENTITY))
					t = 1;
				else if(type.equals(ProdAppType.GENERIC))
					t = 2;
				else if(type.equals(ProdAppType.RECOGNIZED))
					t = 3;
				else if(type.equals(ProdAppType.RENEW))
					t = 4;
			}
		}
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPTYPE, t);

		t = 0;
		if(product != null){
			ProdDrugType type = product.getDrugType();
			if(type != null){
				if(type.equals(ProdDrugType.PHARMACEUTICAL))
					t = 1;
				else if(type.equals(ProdDrugType.MEDICAL_DEVICE))
					t = 2;
				else if(type.equals(ProdDrugType.RADIO_PHARMA))
					t = 3;
				else if(type.equals(ProdDrugType.VACCINE))
					t = 4;
				else if(type.equals(ProdDrugType.BIOLOGICAL))
					t = 5;
				else if(type.equals(ProdDrugType.COMPLIMENTARY_MEDS))
					t = 6;
			}
		}
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DRUGTYPE, t);

		boolean fl = false;
		if(product != null){
			fl = product.isNewChemicalEntity();
			// or by ProdAppType type = prodApp.getProdAppType(); if(type.equals(ProdAppType.NEW_CHEMICAL_ENTITY))
		}
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_SUBACT, fl);

		fl = false;
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_GEN, fl);

		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_REG_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_EXPIRY_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReportsMZ.KEY_GESTOR, gestor);

		return JasperFillManager.fillReport(resource.getFile(), param, conn);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createAckLetter(ProdApplications prodApp) {
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");

		Product prod = prodApp.getProduct();
		try {
			File invoicePDF = File.createTempFile("" + prod.getProdName() + "_ack", ".pdf");

			JasperPrint jasperPrint;
			Connection conn = entityManager.unwrap(Session.class).connection();

			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, prod);

			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DOSREC_DATE, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODNAME, "", false);

			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_GENNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODSTRENGTH, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PACKSIZE, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_MANUFNAME, "", false);

			URL resource = getClass().getClassLoader().getResource("/reports/letter.jasper");
			if(resource != null){
				jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
				net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
				byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));

				ProdAppLetter attachment = new ProdAppLetter();
				attachment.setRegState(prodApp.getRegState());
				attachment.setFile(file);
				attachment.setProdApplications(prodApp);
				attachment.setFileName(invoicePDF.getName());
				attachment.setTitle(bundle.getString("Letter.title_acknow"));
				attachment.setUploadedBy(prodApp.getCreatedBy());
				attachment.setComment(bundle.getString("Letter.comment_acknow"));
				attachment.setContentType("application/pdf");
				attachment.setLetterType(LetterType.ACK_SUBMITTED);
				prodAppLetterDAO.save(attachment);
				conn.close();
				return "persist";
			}
			return "error";
		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return "error";
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return "error";
		} catch (SQLException e) {
			e.printStackTrace();
			return "error";
		}
	}

	public List<ProdApplications> getProcessProdAppList(UserSession userSession) {
		List<ProdApplications> prodApplicationses = null;
		HashMap<String, Object> params = new HashMap<String, Object>();

		List<RegState> regState = new ArrayList<RegState>();
		if (userSession.isAdmin()) {
			regState.add(RegState.FEE);
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
		} 
		if (userSession.isModerator()) {
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			params.put("moderatorId", userSession.getLoggedINUserID());
		} 
		if (userSession.isReviewer()) {
			regState.add(RegState.REVIEW_BOARD);
			if (workspaceDAO.findAll().get(0).isDetailReview()) {
				params.put("regState", regState);
				params.put("reviewer", userSession.getLoggedINUserID());
				return prodApplicationsDAO.findProdAppByReviewer(params);
			} else
				params.put("reviewerId", userSession.getLoggedINUserID());
		} 
		if (userSession.isHead()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.FEE);
			regState.add(RegState.VERIFY);
			regState.add(RegState.SCREENING);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.NOT_RECOMMENDED);
			regState.add(RegState.REJECTED);
		} 
		if (userSession.isStaff()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.SCREENING);
			regState.add(RegState.FEE);
			regState.add(RegState.VERIFY);
			regState.add(RegState.FOLLOW_UP);
		}
		if (userSession.isCompany()) {
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.FEE);
			regState.add(RegState.NEW_APPL);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.SCREENING);
			regState.add(RegState.VERIFY);
			regState.add(RegState.REGISTERED);
			regState.add(RegState.CANCEL);
			regState.add(RegState.SUSPEND);
			regState.add(RegState.DEFAULTED);
			regState.add(RegState.NOT_RECOMMENDED);
			regState.add(RegState.REJECTED);
			params.put("userId", userSession.getLoggedINUserID());
		}
		if (userSession.isLab()) {
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.FOLLOW_UP);
			regState.add(RegState.RECOMMENDED);
			regState.add(RegState.NOT_RECOMMENDED);
		}
		if (userSession.isClinical()) {
			regState.add(RegState.VERIFY);
			regState.add(RegState.REVIEW_BOARD);
			regState.add(RegState.FOLLOW_UP);
			params.put("prodAppType", ProdAppType.NEW_CHEMICAL_ENTITY);
		}

		params.put("regState", regState);
		prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);

		if (userSession.isModerator() || userSession.isReviewer() || userSession.isHead() || userSession.isLab()) {
			Collections.sort(prodApplicationses, new Comparator<ProdApplications>() {
				@Override
				public int compare(ProdApplications o1, ProdApplications o2) {
					Date d1 = o1.getPriorityDate();
					Date d2 = o2.getPriorityDate();
					if(d1 != null && d2 != null)
						return d1.compareTo(d2);
					return 0;
				}
			});
		}

		return prodApplicationses;
	}
}
