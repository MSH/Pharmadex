package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

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
	private ProdAppChecklistService checkListService;

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
	/**
	 * Create deficiency letter and store it to letters
	 * @return
	 */
	@Transactional
	public String createDeficiencyLetterScr(ProdApplications prodApp){
		context = FacesContext.getCurrentInstance();
		prodApp = prodApplicationsService.findProdApplications(prodApp.getId());
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		Product prod = prodApp.getProduct();
		try {
			File defScrPDF = File.createTempFile("" + prod.getProdName() + "_defScr", ".pdf");
			JasperPrint jasperPrint;
			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, prodApp.getProduct());
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_GENNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPADDRESS, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPUSERNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODNAME, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PROD_DETAILS, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODSTRENGTH, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_DOSFORM, "", false);
			List<ProdAppChecklist> checkLists = checkListService.findProdAppChecklistByProdApp(prodApp.getId());
			JRMapArrayDataSource source = createDeficiencySource(checkLists);
			URL resource = getClass().getClassLoader().getResource("/reports/deficiency.jasper");
			if(source != null){
				if(resource != null){
					jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
					net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(defScrPDF));
					byte[] file = IOUtils.toByteArray(new FileInputStream(defScrPDF));
					ProdAppLetter attachment = new ProdAppLetter();
					attachment.setRegState(prodApp.getRegState());
					attachment.setFile(file);
					attachment.setProdApplications(prodApp);
					attachment.setFileName(defScrPDF.getName());
					attachment.setTitle(bundle.getString("LetterType.DEFICIENCY"));
					attachment.setUploadedBy(prodApp.getCreatedBy());
					attachment.setComment(bundle.getString("LetterType.DEFICIENCY"));
					attachment.setContentType("application/pdf");
					attachment.setLetterType(LetterType.ACK_SUBMITTED);
					prodAppLetterDAO.save(attachment);
					return "persist";
				}else{
					return "error";
				}
			}else{
				return "error";
			}

		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return "error";
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return "error";
		} 
	}

	/**
	 * Create data source for report from deficiency in check list
	 * @param checkLists
	 * @return data source or null if impossible
	 */
	private JRMapArrayDataSource createDeficiencySource(List<ProdAppChecklist> checkLists) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		if(checkLists != null){
			int i=1;
			for(ProdAppChecklist item : checkLists){
				if(item.getStaffValue() == YesNoNA.NO || (item.getValue() == YesNoNA.NO) && item.getStaffValue() == YesNoNA.NA){
					Map<String,String> mp = new HashMap<String,String>();
					mp.put(UtilsByReportsMZ.FLD_DEFICITEM_NAME, item.getChecklist().getModule() + ". " + item.getChecklist().getName());
					mp.put("itemNum", i+". ");
					i++;
					res.add(mp);
				}
			}
			return new JRMapArrayDataSource(res.toArray());
		}else{
			return null;
		}
	}

	/**
	 * Create review details and save it to letters (one point to find all generated documents)
	 * @param prodApplications
	 * @return
	 */
	@Transactional
	public String createReviewDetails(ProdApplications prodApplications) {
		prodApp = prodApplications;
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		Properties prop = fetchReviewDetailsProperties();
		if(prop != null){
			Product prod = prodApp.getProduct();
			try {
				JasperPrint jasperPrint;
				HashMap<String, Object> param = new HashMap<String, Object>();
				utilsByReports.init(param, prodApp, prod);
				utilsByReports.putNotNull(UtilsByReportsMZ.KEY_PRODNAME, "", false);
				utilsByReports.putNotNull(UtilsByReportsMZ.KEY_MODERNAME, "", false);
				//TODO chief name from properties!!
				JRMapArrayDataSource source = createReviewSourcePorto(prodApplications,bundle, prop);
				URL resource = getClass().getClassLoader().getResource("/reports/review_detail_report.jasper");
				if(source != null){
					if(resource != null){
						jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
						javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
						httpServletResponse.addHeader("Content-disposition", "attachment; filename=" +prod.getProdName() + "_Review.pdf");
						httpServletResponse.setContentType("application/pdf");
						javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
						net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
						servletOutputStream.close();
						return "persist";
					}else{
						return "error";
					}
				}else{
					return "error";
				}

			} catch (JRException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				return "error";
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				return "error";
			} 
		}else{
			return "error";
		}
	}
	/**
	 * Read necessary properties for review details (for Porto language)
	 * @return properties or null
	 */
	private Properties fetchReviewDetailsProperties() {
		Properties props = new Properties();
		InputStream in;
		try {
			in = this.getClass().getResourceAsStream("review_details.properties");
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return props;
	}

	/**
	 * Create data source for review detail report. Portuguese language only!!!!
	 * @param prodApplications current application
	 * @param bundle language bundle
	 * @param prop report specific properties
	 * @return at least empty map!
	 */
	private JRMapArrayDataSource createReviewSourcePorto(ProdApplications prodApplications, ResourceBundle bundle, Properties prop) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		fillGeneralRS(res, prop,prodApplications);
		return new JRMapArrayDataSource(res.toArray());
	}
	/**
	 * Fill first section of the details - general information
	 * @param res result map
	 * @param prop specific properties
	 * @param prodApplications current application
	 */
	private void fillGeneralRS(List<Map<String, String>> res, Properties prop, ProdApplications prodApplications) {
		res.clear();
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_1"),fetchFullApplicant(prodApplications),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_2"),prodApplications.getProduct().getGenName(),null);
		//TODO 
	}
	/**
	 * Fetch full applicant data name + address
	 * @param prodApp 
	 * @return
	 */
	private String fetchFullApplicant(ProdApplications prodApp) {
		String name = prodApp.getApplicant().getAppName();
		String addr1 = prodApp.getApplicant().getAddress().getAddress1();
		String addr2 = prodApp.getApplicant().getAddress().getAddress2();
		String addr="";
		if(addr1 != null){
			addr = addr1;
		}
		if(addr2 != null){
			addr = addr + " "+addr2;
		}
		if(addr.length()==0){
			addr="Maputo";
		}
		return name + " " + addr;
	}

	/**
	 * Fill data for a line of the report to result res
	 * @param res list of maps
	 * @param reviewChapter
	 * @param reviewItem
	 * @param reviewItemData
	 * @param reviewItemFile
	 */
	private void fillItemRS(List<Map<String, String>> res, String reviewChapter, String reviewItem,
			String reviewItemData, String reviewItemFile) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("reviewChapter",reviewChapter);
		map.put("reviewItem",reviewItem);
		map.put("reviewItemData",reviewItemData);
		map.put("reviewItemFile",reviewItemFile);
		res.add(map);
	}

	/**
	 * Create application acknowledgement letter and store it to letters!
	 * @param prodApp current prodapplication
	 * @return persist or error
	 */
	@Transactional
	public String createAckLetter(ProdApplications prodApp) {
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		//System.out.println("PRODAPPMZ REFRESHED!!!!");
		Product prod = prodApp.getProduct();
		try {
			File invoicePDF = File.createTempFile("" + prod.getProdName().split(" ")[0] + "_ack", ".pdf");

			JasperPrint jasperPrint;

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

			//letter
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPNUM, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPPOST, "", false);				
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPADDRESS, "", false);
			utilsByReports.putNotNull(UtilsByReportsMZ.KEY_APPUSERNAME, "", false);

			URL resource = getClass().getClassLoader().getResource("/reports/letter.jasper");
			if(resource != null){
				jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));
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
				return "persist";
			}
			return "error";
		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return "error";
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

	@Transactional
	public String submitExecSummary(ProdApplications prodApplications, Long loggedInUser, List<ReviewInfo> reviewInfos) {
		try {
			if (reviewInfos == null || prodApplications == null || loggedInUser == null)
				return "empty";

			boolean complete = false;
			for (ReviewInfo reviewInfo : reviewInfos) {
				if (!reviewInfo.getReviewStatus().equals(ReviewStatus.ACCEPTED)) {
					complete = false;
					return "state_error";
				} else {
					complete = true;
				}
			}

			if (prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
				// old version (!prodApplications.isClinicalRevReceived() || !prodApplications.isClinicalRevVerified() || prodApplications.getcRevAttach() == null)
				if (!prodApplications.isClinicalRevReceived() || !prodApplications.isClinicalRevVerified()) {
					complete = false;
					return "clinical_review";
				}
			}

			if (prodApplications.getProdAppType()!=ProdAppType.RENEW) {
				if (prodApplications.getSampleTestRecieved() == null || !prodApplications.getSampleTestRecieved()) {
					return "lab_status";
				}
			}

			if (complete) {
				prodApplicationsService.saveApplication(prodApplications, loggedInUser);
				return "persist";
			} else {
				return "state_error";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "error";
		}
	}

	public void deleteProdAppLetter(ProdAppLetter let){
		prodAppLetterDAO.delete(let);
	}
}
