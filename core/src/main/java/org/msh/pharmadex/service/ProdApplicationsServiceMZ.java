package org.msh.pharmadex.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductCompanyDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.dao.iface.RevDeficiencyDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.RevDeficiency;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

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
	private CustomReviewDAO customReviewDAO;
	@Autowired
	private ProductCompanyDAO prodCompanyDAO;
	@Autowired
	private ProdAppLetterDAO prodAppLetterDAO;

	@Autowired
	UserService userService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ProdApplicationsService prodApplicationsService;
	@Autowired
	private ProdAppChecklistService checkListService;
	@Autowired
	private SampleTestService sampleTestService;

	@Autowired
	private ReviewInfoDAO reviewInfoDAO;

	private ProdApplications prodApp;
	private Product product;
	// pt_PT
	private Locale locale = new Locale("pt", "PT");
	@Autowired
	private UtilsByReports utilsByReports;

	@Autowired
	TimelineService timelineService;

	@Autowired
	private RevDeficiencyDAO revDeficiencyDAO;
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

	@Transactional
	public String rejectedProd(ProdApplications prodApp, String com) {
		this.prodApp = prodApp;
		return prodApplicationsService.rejectProd(this.prodApp, com);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createRegCert(ProdApplications prodApp, String gestorDeCTRM, boolean isGeneric) throws IOException, JRException, SQLException {
		this.prodApp = prodApp;
		this.product = prodApp.getProduct();
		File invoicePDF = null;
		invoicePDF = File.createTempFile("" + product.getProdName().split(" ")[0] + "_registration", ".pdf");
		JasperPrint jasperPrint = initRegCert(gestorDeCTRM, isGeneric);
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
		prodApp.setRegCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
		prodApplicationsDAO.updateApplication(prodApp);
		return "created";
	}

	public JasperPrint initRegCert(String gestor, boolean isGeneric) throws JRException, SQLException {
		product = productDAO.findProduct(prodApp.getProduct().getId());
		URL resource = getClass().getResource("/reports/reg_letter.jasper");

		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApp, product);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_REG_NUMBER, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBMIT_DATE, "", false);

		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SHELFINE, "", false);

		//inn
		utilsByReports.putNotNull(UtilsByReports.KEY_INN, "", false);

		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PACKSIZE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_STORAGE, "", false);
		//excipient
		utilsByReports.putNotNull(UtilsByReports.KEY_EXCIPIENT, "", false);

		String fnm = (product != null) ? product.getFnm():"";
		boolean flag = (fnm != null && fnm.length() > 0) ? true: false;
		utilsByReports.putNotNull(UtilsByReports.KEY_FNM, flag);

		int t = 0;
		if(prodApp != null){
			ProdAppType type = prodApp.getProdAppType();
			if(type != null){
				if(type.toString().equals("NEW_CHEMICAL_ENTITY"))
					t = 1;
				else if(type.toString().equals("GENERIC"))
					t = 2;
				else if(type.toString().equals("RECOGNIZED"))
					t = 3;
				else if(type.toString().equals("RENEW"))
					t = 4;
			}
		}
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, t);

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
		utilsByReports.putNotNull(UtilsByReports.KEY_DRUGTYPE, t);

		boolean fl = false;
		if(product != null){
			fl = product.isNewChemicalEntity();
			// or by ProdAppType type = prodApp.getProdAppType(); if(type.equals(ProdAppType.NEW_CHEMICAL_ENTITY))
		}
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBACT, fl);

		utilsByReports.putNotNull(UtilsByReports.KEY_GEN, isGeneric);

		//
		String str = "";
		List<UseCategory> cats = product.getUseCategories();
		if(cats != null && cats.size() > 0){
			for(UseCategory c:cats){
				str += c.ordinal();
			}
		}
		utilsByReports.putNotNull(UtilsByReports.KEY_USE_CATEGORY, str, true);

		utilsByReports.putNotNull(UtilsByReports.KEY_REG_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_EXPIRY_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_GESTOR, gestor);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);		
		utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);
		
		JasperReport  jasperMasterReport =  (JasperReport)JRLoader.loadObject(new File(resource.getFile())); 
		Map reportfields = new HashMap(); 
		if(jasperMasterReport!=null){
			JRMapArrayDataSource source = createRegLetterSource("table", product, prodApp);
			if(source!=null) 
				reportfields.put(UtilsByReports.FTR_DATASOUTCE, source);			
		}
				
		//subReport, set fields
        URL subresourceA1 = getClass().getResource("/reports/attachment1.jasper");
        if(subresourceA1!=null){
        	//attachment1 table 1 			
			JRMapArrayDataSource sourceTable1 = createRegLetterSource("table1", product, prodApp);
			if(sourceTable1!=null) 
				reportfields.put(UtilsByReports.FTR_DATASOUTCE1, sourceTable1);	
			
    		//attachment1 table 2	
			JRMapArrayDataSource sourceTable2 = createRegLetterSource("table2", product, prodApp);
			if(sourceTable2!=null)
				reportfields.put(UtilsByReports.FTR_DATASOUTCE2, sourceTable2);
				
				JasperReport  jasperSubReportA1 = (JasperReport)JRLoader.loadObject(new File(subresourceA1.getFile()));
		        if(jasperSubReportA1!=null){ 
		        	//attachment1    	
		    		JRMapArrayDataSource sourceFormA1 = createRegLetterSource("attachment1", product, prodApp);
		    		if(sourceFormA1!=null){
		    			reportfields.put(UtilsByReports.FTR_A1DATASOUTCE, sourceFormA1);	 	
		        		param.put("Subreport_A1", jasperSubReportA1); 
		    		}    		
		        }		  
        	
        }
        URL subresourceA2 = getClass().getResource("/reports/attachment2.jasper");
        if(subresourceA2!=null){
        	        	
        	JasperReport  jasperSubReportA2 = (JasperReport)JRLoader.loadObject(new File(subresourceA2.getFile()));
	        if(jasperSubReportA2!=null){
	        	//attachment1 sub 2	    		
	    		JRMapArrayDataSource sourceFormA2 = createRegLetterSource("attachment2", product, prodApp);
	    		if(sourceFormA2!=null){
	    			reportfields.put(UtilsByReports.FTR_A2DATASOUTCE, sourceFormA2);
	        		param.put("Subreport_A2", jasperSubReportA2); 
	    		}    		
	        }  
        }
        URL subresourceF16 = getClass().getResource("/reports/form16.jasper");
        if(subresourceF16!=null){
            JasperReport jasperSubReportF16 = (JasperReport)JRLoader.loadObject(new File(subresourceF16.getFile()));
            if(jasperSubReportF16!=null){
            	//form 16        		
        		JRMapArrayDataSource sourceForm16 = createRegLetterSource("form16", product, prodApp);
        		if(sourceForm16!=null){
        			reportfields.put(UtilsByReports.FTR_F16DATASOUTCE, sourceForm16);
            		param.put("Subreport_1", jasperSubReportF16); 
        		}    		
            } 
        }
           
        URL subresourceF13 = getClass().getResource("/reports/form13.jasper");
        if(subresourceF13!=null){
	        JasperReport jasperSubReportF13 = (JasperReport)JRLoader.loadObject(new File(subresourceF13.getFile()));
	        if(subresourceF13!=null){
	    		//form 13	    	
	    		JRMapArrayDataSource sourceForm13 = createRegLetterSource("form13", product, prodApp);
	    		if(sourceForm13!=null){
	    			reportfields.put(UtilsByReports.FTR_F13DATASOUTCE, sourceForm13);  
	        		param.put("Subreport_2", jasperSubReportF13);
	    		}    		  
	        }        
        }
        
        URL subresourceF8 = getClass().getResource("/reports/form8.jasper");
        if(subresourceF8!=null){
        	JasperReport jasperSubReportF8 = (JasperReport)JRLoader.loadObject(new File(subresourceF8.getFile()));
            if(jasperSubReportF8!=null){
            	//form 8        	
        		JRMapArrayDataSource sourceForm8 = createRegLetterSource("form8", product, prodApp);
        		if(sourceForm8!=null){
        			reportfields.put(UtilsByReports.FTR_F8DATASOUTCE , sourceForm8);
            		param.put("Subreport_3", jasperSubReportF8);
        		}        		
            } 
        }
                
        URL subresourceF9 = getClass().getResource("/reports/form9.jasper");
        if(subresourceF9!=null){
        	 JasperReport jasperSubReportF9 = (JasperReport)JRLoader.loadObject(new File(subresourceF9.getFile()));
             if(jasperSubReportF9!=null){
             	//form 9         		
         		JRMapArrayDataSource sourceForm9 = createRegLetterSource("form9", product, prodApp);
         		if(sourceForm9!=null){
         			reportfields.put(UtilsByReports.FTR_F9DATASOUTCE , sourceForm9);  
                     param.put("Subreport_4", jasperSubReportF9);
         		}    		
             }	
        }
       
        JasperPrint jasperPrint;
        /*if(reportfields.size()>0)
        	if(reportfields.size()==1)
        		jasperPrint =  JasperFillManager.fillReport(resource.getFile(), param, new JRMapArrayDataSource(new Object[] { reportfields}));
        	else
        		jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JRMapArrayDataSource(new Object[] { reportfields}));      	      
        else
        	jasperPrint =  JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));*/
        jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JRMapArrayDataSource(new Object[] { reportfields})); 
        return jasperPrint;
	/*
		String regNum = prodApp.getProdRegNo() != null ? prodApp.getProdRegNo():""; //"2019/08";//
		String prodName = product.getProdName() != null ? product.getProdName():""; //"AMPICILLIN";//
		JRMapArrayDataSource source = createRegLetterSource(prodName, regNum);
		
		 Map[] masterData = new Map[1];
		 masterData[0] = new HashMap();
         masterData[0].put(UtilsByReports.FTR_DATASOUTCE, source);
	  	
		JasperPrint jasperPrint;
		if(source != null){
			jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JRMapArrayDataSource(masterData));
			return jasperPrint;
		}else
			return  JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));*/
	}

	public String createRejectCert(ProdApplications prodApp, String summary , Long loggedINUserID ) {
		this.prodApp = prodApp;
		this.product = prodApp.getProduct();
		try {
			File invoicePDF = null;
			invoicePDF = File.createTempFile("" + product.getProdName().split(" ")[0] + "_rejection", ".pdf");
			JasperPrint jasperPrint = initRejectCert(summary,loggedINUserID );
			if(jasperPrint != null){
				net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
				prodApp.setRejCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
				prodApplicationsDAO.updateApplication(prodApp);
			}else
				return "error";
		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates. 
			return "error";
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates. 
			// return "error"; 
		}
		return "created";
	}

	private JasperPrint initRejectCert(String summary, Long loggedINUserID ) throws JRException {
		URL resource = getClass().getResource("/reports/rejection_letter.jasper");
		if(resource != null){
			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, product);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, "New Medicine Registration", true);
			utilsByReports.putNotNull(UtilsByReports.KEY_SUBJECT, "Rejection Letter  ", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);	
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_BODY, summary, true);

			utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);

			utilsByReports.putNotNull(UtilsByReports.KEY_EXECSUMMARY,summary, true);			
			utilsByReports.putNotNull(UtilsByReports.KEY_MODINITIALS, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
			
			if(prodApp.getApplicant() != null){
				if(prodApp.getApplicant().getContactName()!=null){
					setAppResponsibleUser(prodApp.getApplicant().getContactName());													
				}
			}
					
			if(loggedINUserID!=null){
				User curuser = userService.findUser(loggedINUserID);
				String res = "";
				if(curuser!=null){	
					if(prodApp!=null)
						res = getUsername(curuser);														
				}
				utilsByReports.putNotNull(UtilsByReports.KEY_SCRINITIALS, res, true);				
			}
			param.put("date", new Date());
			utilsByReports.putNotNull(UtilsByReports.KEY_REG_NUMBER, "", false);

			return JasperFillManager.fillReport(resource.getFile(), param);
		}
		return null;
	}

	/**
	 * Create deficiency letter and store it to letters
	 * @return
	 */
	public String createDeficiencyLetterScr(ProdApplications prodApp, String days, Date dueDate, List<ProdAppChecklist> checkLists){
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		Product prod = productDAO.findProduct(prodApp.getProduct().getId());
		try {
			File defScrPDF = File.createTempFile("" + prod.getProdName().split(" ")[0] + "_defScr", ".pdf");
			JasperPrint jasperPrint;
			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, prod);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PROD_DETAILS, "", false);

			utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);			
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_DAYS, days, true);		
			utilsByReports.putNotNull(UtilsByReports.KEY_DUEDATE, dueDate);

			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);
	
			if(prodApp.getApplicant() != null){
				if(prodApp.getApplicant().getContactName()!=null){
					setAppResponsibleUser(prodApp.getApplicant().getContactName());							
				}
			}
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			if(locale!=null){
				param.put( JRParameter.REPORT_LOCALE,locale );
			}

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
			if(checkLists.size()>0){
				for(ProdAppChecklist item : checkLists){
					Map<String,String> mp = new HashMap<String,String>();
					String staffRemark="";
					if(item.getStaffComment()!=null){
						if(!"".equals(item.getStaffComment()))
							staffRemark= item.getStaffComment()+"<br>";
					}
					mp.put(UtilsByReports.FLD_DEFICITEM_NAMEMZ,item.getStaffComment()+"</li>");
					mp.put(UtilsByReports.FLD_DEFICITEM_NAME, "<b>"+item.getChecklist().getModule() + ". " + item.getChecklist().getName()+"</b> "+staffRemark);
					res.add(mp);
				}
			}else{
				Map<String,String> mp = new HashMap<String,String>();
				mp.put(UtilsByReports.FLD_DEFICITEM_NAME,"");
				res.add(mp);
			}			
			return new JRMapArrayDataSource(res.toArray());
		}else{
			return null;
		}
	}

/*	private JRMapArrayDataSource createRegLetterSource(String prodName, String regNumber) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
				Map<String,String> mp = new HashMap<String,String>();
				mp.put(UtilsByReports.FLD_PROD_NAME, prodName);
				mp.put(UtilsByReports.FLD_REG_NUMBER, regNumber);				
				res.add(mp);
			
			return new JRMapArrayDataSource(res.toArray());
		
	}
*/
	private JRMapArrayDataSource createRegLetterSource(String name, Product product, ProdApplications prodApp){
	
		HashMap<String, Object> field = new HashMap<String, Object>();
		utilsByReports.initField(field, prodApp, product);
		if("table".equals(name)){			
			utilsByReports.putFieldNotNull(UtilsByReports.FLD_PROD_NAME,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.FLD_REG_NUMBER,"",false);
		}
		
		if("table1".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPTYPE,"",false);		
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_ADDRESS1,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_COUNTRY,"",false);			
		}
		if("table2".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_ADDRESS1,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_COUNTRY,"",false);	
		}
		if("attachment1".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_PRODNAME,"",false);	 
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_GENNAME,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_PRODSTRENGTH,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_DOSFORM,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_NUMBER,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_DATE,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_EXPIRY_DATE,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPTYPE,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_ADDRESS1,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_COUNTRY,"",false);
		}
		if("attachment2".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_SHELFINE,"",false);		
		}
		if("form16".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_NUMBER,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_DATE,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPROVED_EXPERT,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_HEAD,"",false);	
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_DESIGNATION,"",false);	
		}
		if("form13".equals(name)){
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_NUMBER,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPROVED_EXPERT,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_HEAD,"",false);
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_REG_DATE,"",false);				
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_DESIGNATION,"",false);
		}
		if("form8".equals(name)){				
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);	
		}
		if("form9".equals(name)){			
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_APPNAME,"",false);	
		}
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		res.add(field);		
		return	new JRMapArrayDataSource(res.toArray());	
		
	}
			


	/**
	 * Create review details and save it to letters (one point to find all generated documents)
	 * @param prodApplications
	 * @return
	 */
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
				utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_MODERNAME, "", false);

				utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_SUBMIT_DATE, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_DOSREC_DATE, "", false);

				int t = 0;
				if(prodApp != null){
						ProdAppType type = prodApp.getProdAppType();
						if(type != null){
							if(type.toString().equals("NEW_CHEMICAL_ENTITY"))
								t = 1;
							else if(type.toString().equals("GENERIC"))
								t = 2;
							else if(type.toString().equals("RECOGNIZED"))
								t = 3;
							else if(type.toString().equals("RENEW"))
								t = 4;
						}
				}
				utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, t);

				utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_APPTELLFAX, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_APPEMAIL, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_EXECSUMMARY, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_INN, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_PROD_ROUTE_ADMINISTRATION, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_PHONE, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);
				utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_EMAIL, "", false);

				utilsByReports.putNotNull(JRParameter.REPORT_LOCALE, locale);

				//TODO chief name from properties!!
				JRMapArrayDataSource source = ReviewDetailPrintMZ.createReviewSourcePorto(prodApplications,bundle, prop, prodCompanyDAO, customReviewDAO, reviewInfoDAO, param);
				URL resource = getClass().getClassLoader().getResource("/reports/review_detail_report.jasper");
				if(source != null){
					if(resource != null){
						jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
						javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
						httpServletResponse.addHeader("Content-disposition", "attachment; filename=" +prod.getProdName().split(" ")[0] +  "_Review.pdf");
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

	public StreamedContent createReviewDetailsFile(ProdApplications prodApplications) {
		this.prodApp = prodApplications;
		this.product = prodApp.getProduct();
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");

		File detailPDF = null;
		String fileName = product.getProdName().split(" ")[0] + "_Review";
		try{
			detailPDF = File.createTempFile(fileName, ".pdf");
			JasperPrint jasperPrint = initReviewDetailsFile();
			net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(detailPDF));

			InputStream ist = new ByteArrayInputStream((IOUtils.toByteArray(new FileInputStream(detailPDF))));
			StreamedContent download = new DefaultStreamedContent(ist, "pdf", fileName + ".pdf");
			return download;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JasperPrint initReviewDetailsFile() throws JRException, SQLException {
		JasperPrint jasperPrint;

		Properties prop = fetchReviewDetailsProperties();
		if(prop == null)
			return null;
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApp, product);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MODERNAME, "", false);

		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SUBMIT_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSREC_DATE, "", false);

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
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE, t);

		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPTELLFAX, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPEMAIL, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_EXECSUMMARY, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_INN, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PROD_ROUTE_ADMINISTRATION, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_PHONE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_EMAIL, "", false);

		utilsByReports.putNotNull(JRParameter.REPORT_LOCALE, locale);

		JRMapArrayDataSource source = ReviewDetailPrintMZ.createReviewSourcePorto(prodApp, bundle, prop, prodCompanyDAO, customReviewDAO, reviewInfoDAO, param);
		URL resource = getClass().getClassLoader().getResource("/reports/review_detail_report.jasper");
		if(source != null){
			jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
			return jasperPrint;
		}else
			return  JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource(1));
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
			if(in== null){
				return null;
			}
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
	 * 31.08.2016 only one FOLLOW_UP
	 * @return
	 */
	public List<RegState> nextStepOptions() {
		RegState[] options = new RegState[1];
		options[0] = RegState.FOLLOW_UP;
		return Arrays.asList(options);
	}

	/**
	 * Create application acknowledgement letter and store it to letters!
	 * @param prodApp current prodapplication
	 * @return persist or error
	 */
	public String createAckLetter(ProdApplications prodApp, Long loggedINUserID) {
		context = FacesContext.getCurrentInstance();		 
		bundle = context.getApplication().getResourceBundle(context, "msgs");

		Product prod = productDAO.findProduct(prodApp.getProduct().getId());
		try {
			File invoicePDF = File.createTempFile("" + prod.getProdName().split(" ")[0] + "_ack", ".pdf");

			JasperPrint jasperPrint;

			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, prod);

			utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);				
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSREC_DATE, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);

			utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PACKSIZE, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);

			//letter
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPPOST, "", false);				
			utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);						
			utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);
			
			utilsByReports.putNotNull(UtilsByReports.KEY_MODINITIALS, "", false);

			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COMPANY_FAX, "", false);			
			
			if(loggedINUserID!=null){
				User curuser = userService.findUser(loggedINUserID);		
				String res = "", resIn="";
				if(curuser!=null){
					res = curuser.getName()!=null?curuser.getName():"";					
					if(prodApp!=null)
						resIn = getUsername(curuser);
				}	
				utilsByReports.putNotNull(UtilsByReports.KEY_CURUSER,res, true);
				utilsByReports.putNotNull(UtilsByReports.KEY_SCRINITIALS, resIn, true);
			}
			

			if(prodApp.getApplicant() != null){
				if(prodApp.getApplicant().getContactName()!=null){
					setAppResponsibleUser(prodApp.getApplicant().getContactName());													
				}
			}
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

	/**
	 * set parameter applicant responsible
	 * @param contName - applicant contactName 
	 */
	private void setAppResponsibleUser(String contName) {			
		User respUser = userService.findUserByUsername(contName);	
		if(respUser!=null){
			String res = respUser.getName()!=null?respUser.getName():"";						
			utilsByReports.putNotNull(UtilsByReports.KEY_APPRESPONSIBLE,res, true);		
		}

	}

	private String getUsername(User curuser ) {
		String res = "";
		if (curuser.getName() != null) {				
			if (!curuser.getName().equals("Sultana Razaco")) {
				String[] in = curuser.getName().split(" ");
				for (String item : in) {
					res += item.substring(0, 1).toLowerCase();
				}
			}			
		}
		return res;
	}

	public RetObject createReviewDeficiencyLetter(ProdApplications prodApp, String com, RevDeficiency revDeficiency){
		context = FacesContext.getCurrentInstance();
		bundle = context.getApplication().getResourceBundle(context, "msgs");
		Product prod = productDAO.findProduct(prodApp.getProduct().getId());
		Properties prop = fetchReviewDetailsProperties();
		try {
			ReviewInfo ri = reviewInfoDAO.findOne(revDeficiency.getReviewInfo().getId());
			ri.setReviewStatus (ReviewStatus.FIR_SUBMIT);

			Date dueDate = revDeficiency.getDueDate();

			File defScrPDF = File.createTempFile("" + prod.getProdName().split(" ")[0] + "_revdefScr", ".pdf");
			JasperPrint jasperPrint;
			HashMap<String, Object> param = new HashMap<String, Object>();
			utilsByReports.init(param, prodApp, prod);
			
			utilsByReports.putNotNull(UtilsByReports.KEY_APPUSERNAME, "", false);		
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1,"",false);
			utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS2,"",false);
			utilsByReports.putNotNull(UtilsByReports.KEY_COUNTRY,"",false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);	

			utilsByReports.putNotNull(UtilsByReports.KEY_APPADDRESS, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNUM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_INN, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PACKSIZE, "", false);	
			utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);

			utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_GENNAME, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);	
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_EXECSUMMARY,getSentComment(revDeficiency), true);
			utilsByReports.putNotNull(UtilsByReports.KEY_DUEDATE, dueDate);
			
			
			String res ="";
			if(prodApp != null){
				ProdAppType type = prodApp.getProdAppType();
				if(type!=null)
					res = bundle.getString(prodApp.getProdAppType().getKey());							
			}
			utilsByReports.putNotNull(UtilsByReports.KEY_APPTYPE,res,true);	
						
			if(prodApp.getApplicant() != null){
				if(prodApp.getApplicant().getContactName()!=null){
					setAppResponsibleUser(prodApp.getApplicant().getContactName());													
				}
			}			
			utilsByReports.putFieldNotNull(UtilsByReports.KEY_DOSREC_DATE,"",false);
			
			String userEmail = "";
			String userName = prodApp.getUsername();			
			if(userName!=null){				
				  User user = userService.findUserByUsername(userName);
				  if(user!=null)
					  userEmail = user.getEmail();				
			}
			utilsByReports.putNotNull(UtilsByReports.KEY_APPUSEREMAIL, userEmail, true);
			
			//TODO	
			/*List<ProdAppChecklist> checkLists = checkListService.findProdAppChecklistByProdApp(prodApp.getId());
			JRMapArrayDataSource source = createDeficiencySource(checkLists);*/
			JRMapArrayDataSource source = ReviewDetailPrintMZ.createReviewSourcePorto(prodApp,bundle, prop, prodCompanyDAO, customReviewDAO, reviewInfoDAO, param);

			URL resource = getClass().getClassLoader().getResource("/reports/rev_def_letter.jasper");
			if(source != null){
				if(resource != null){
					jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
					net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(defScrPDF));
					byte[] file = IOUtils.toByteArray(new FileInputStream(defScrPDF));
					ProdAppLetter attachment = new ProdAppLetter();
					attachment.setRegState(prodApp.getRegState());
					attachment.setComment(com);
					attachment.setFile(file);
					attachment.setProdApplications(prodApp);
					attachment.setFileName(defScrPDF.getName());
					attachment.setTitle("Further Information Request");
					attachment.setUploadedBy(prodApp.getCreatedBy());
					attachment.setComment("Automatically generated Letter");					
					attachment.setContentType("application/pdf");

					attachment.setReviewInfo(ri);	

					revDeficiency.setProdAppLetter(attachment);					
					revDeficiency.setReviewInfo(ri);
					revDeficiencyDAO.saveAndFlush(revDeficiency);

					TimeLine timeLine = new TimeLine();
					timeLine.setComment("Status changes due to further information request");
					timeLine.setRegState(RegState.FOLLOW_UP);
					timeLine.setProdApplications(prodApp);
					timeLine.setStatusDate(new Date());
					timeLine.setUser(revDeficiency.getUser());
					RetObject retObject = timelineService.saveTimeLine(timeLine);
					if (retObject.getMsg().equals("persist")) {
						timeLine = (TimeLine) retObject.getObj();
						prodApp = timeLine.getProdApplications();
						revDeficiency.getReviewInfo().setProdApplications(prodApp);
					}
					return saveReviewInfo(revDeficiency.getReviewInfo());

				}else{
					return null;
				}
			}else{
				return null;
			}

		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} 
	}

	private String getSentComment(RevDeficiency revDeficiency) {
		String result = "";	 
		if(revDeficiency.getSentComment()!=null) 
			if(revDeficiency.getSentComment().getComment() !=null)	  
				result = revDeficiency.getSentComment().getComment() ;
		return result;
	}

	@Transactional
	public RetObject saveReviewInfo(ReviewInfo reviewInfo) {
		RetObject retObject = new RetObject();
		ReviewInfo ri = reviewInfoDAO.saveAndFlush(reviewInfo);
		Hibernate.initialize(ri.getReviewComments());
		Hibernate.initialize(ri.getReviewDetails());
		retObject.setObj(reviewInfo);
		retObject.setMsg("success");
		return retObject;
	}

	public void changeStateReviewInfo(Long prodAppID){
		List<ReviewInfo> infos = reviewInfoDAO.findByProdApplications_IdOrderByAssignDateAsc(prodAppID);
		if(infos != null && infos.size() > 0){
			for(ReviewInfo rev:infos){
				rev.setReviewStatus(ReviewStatus.ASSIGNED);
				rev.setRecomendType(null);
				saveReviewInfo(rev);
			}
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
			String verification = verificationBeforeComplete(prodApplications, loggedInUser, reviewInfos);
			if(verification.equals("ok")){
				prodApplicationsService.saveApplication(prodApplications, loggedInUser);
				return "persist";
			}else 
				return verification;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "error";
		}
	}

	public String verificationBeforeComplete(ProdApplications prodApplications, Long loggedInUser, List<ReviewInfo> reviewInfos){
		try {
			if (reviewInfos == null || prodApplications == null || loggedInUser == null)
				return "empty";

			for (ReviewInfo reviewInfo : reviewInfos) {
				if (!reviewInfo.getReviewStatus().equals(ReviewStatus.ACCEPTED)) {
					return "state_error";
				}
			}

			if (prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
				// old version (!prodApplications.isClinicalRevReceived() || !prodApplications.isClinicalRevVerified() || prodApplications.getcRevAttach() == null)
				if (!prodApplications.isClinicalRevReceived() || !prodApplications.isClinicalRevVerified()) {
					return "clinical_review";
				}
			}

			if (prodApplications.getProdAppType() != ProdAppType.RENEW) {
				List<SampleTest> list = sampleTestService.findSampleForProd(prodApplications.getId());
				if(list != null && list.size() > 0){
					if (prodApplications.getSampleTestRecieved() == null || !prodApplications.getSampleTestRecieved()) {
						return "lab_status";
					}
				}
			}

			return "ok";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "error";
		}
	}

	public void deleteProdAppLetter(ProdAppLetter let){
		prodAppLetterDAO.delete(let);
	}

	public ProdApplicationsService getProdApplicationsService() {
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
		this.prodApplicationsService = prodApplicationsService;
	}


}
