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
	private UtilsByReports utilsByReports;
	
	private ProdApplications prodApp;
	private Product product;

    public List<ProdApplications> getSubmittedApplications(UserSession userSession) {
        List<ProdApplications> prodApplicationses = null;
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (userSession.isAdmin()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FEE);
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isModerator()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            params.put("moderatorId", userSession.getLoggedINUserID());
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isReviewer()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            if (workspaceDAO.findAll().get(0).isDetailReview()) {
                params.put("reviewer", userSession.getLoggedINUserID());
                return prodApplicationsDAO.findProdAppByReviewer(params);
            } else
                params.put("reviewerId", userSession.getLoggedINUserID());
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isHead()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            regState.add(RegState.REJECTED);

            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isStaff()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FEE);
            regState.add(RegState.FOLLOW_UP);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isCompany()) {
            List<RegState> regState = new ArrayList<RegState>();
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

            params.put("regState", regState);
            params.put("userId", userSession.getLoggedINUserID());
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isLab()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isClinical()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.FOLLOW_UP);
            params.put("regState", regState);
            params.put("prodAppType", ProdAppType.NEW_CHEMICAL_ENTITY);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        }

        if (userSession.isModerator() || userSession.isReviewer() || userSession.isHead() || userSession.isLab()) {
            Collections.sort(prodApplicationses, new Comparator<ProdApplications>() {
                @Override
                public int compare(ProdApplications o1, ProdApplications o2) {
                    return o1.getPriorityDate().compareTo(o2.getPriorityDate());
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
	public String createRegCert(ProdApplications prodApp) throws IOException, JRException, SQLException {
		this.prodApp = prodApp;
		this.product = prodApp.getProduct();
		File invoicePDF = null;
		invoicePDF = File.createTempFile("" + product.getProdName() + "_registration", ".pdf");
		JasperPrint jasperPrint = initRegCert();
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
		prodApp.setRegCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
		prodApplicationsDAO.updateApplication(prodApp);
		return "created";
	}
    
    public JasperPrint initRegCert() throws JRException, SQLException {
		product = productDAO.findProduct(prodApp.getProduct().getId());

		Connection conn = entityManager.unwrap(Session.class).connection();
		URL resource = getClass().getResource("/reports/reg_letter.jasper");
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApp, product);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNUMBER, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_REG_NUMBER, "", false);
		//utilsByReports.putNotNull(UtilsByReports., "", false);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_ADDRESS1, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_SHELFINE, "", false);
		
		//inn
		utilsByReports.putNotNull(UtilsByReports.KEY_INN, "", true);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_PACKSIZE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_STORAGE, "", false);
		//excipient
		utilsByReports.putNotNull(UtilsByReports.KEY_EXCIPIENT, "", true);
		
		String fnm = (product != null) ? product.getFnm():"";
		boolean flag = (fnm != null && fnm.length() > 0) ? true: false;
		utilsByReports.putNotNull(UtilsByReports.KEY_FNM, flag);
		
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
				else //if(type.equals(ProdAppType.GENERIC))
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
		
		fl = false;
		utilsByReports.putNotNull(UtilsByReports.KEY_GEN, fl);
		
		utilsByReports.putNotNull(UtilsByReports.KEY_REG_DATE, "", false);
		utilsByReports.putNotNull(UtilsByReports.KEY_EXPIRY_DATE, "", false);
		
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
			
			//utilsByReports.putNotNull(UtilsByReports., "", false);
			//utilsByReports.putNotNull(UtilsByReports., "", false);
			//utilsByReports.putNotNull(UtilsByReports., "", false);
			//utilsByReports.putNotNull(UtilsByReports., "", false);
		    
			utilsByReports.putNotNull(UtilsByReports.KEY_PRODSTRENGTH, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_DOSFORM, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_PACKSIZE, "", false);
			utilsByReports.putNotNull(UtilsByReports.KEY_MANUFNAME, "", false);

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
}
