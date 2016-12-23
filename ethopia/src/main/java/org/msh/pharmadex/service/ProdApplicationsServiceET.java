package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.CustomLicHolderDAO;
import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductCompanyDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;


/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProdApplicationsServiceET extends ProdApplicationsService {
	
	private LicenseHolder licenseHolder;
	 
    @Autowired
    private CustomReviewDAO customReviewDAO;
    @Autowired
    private ProductCompanyDAO prodCompanyDAO;
    @Autowired
    private ProdApplicationsDAO prodApplicationsDAO;
    @Autowired
	private ReviewInfoDAO reviewInfoDAO;
 
    @Autowired
	private UtilsByReportsET utilsByReports;
    
    private ProdApplications prodApp;
    
/*  @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;
    */
    @Autowired
    private CustomLicHolderDAO customLicHolderDAO;
      	
    @Override
    public List<RegState> nextStepOptions(RegState regState, UserSession userSession, boolean reviewStatus) {
        RegState[] options = null;
        switch (regState) {
            case NEW_APPL:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.SCREENING;
                break;
            case SCREENING:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.FEE;
                break;
            case FEE:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.VERIFY;
                break;
            case VERIFY:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.REVIEW_BOARD;
                break;
            case REVIEW_BOARD:
                if (userSession.isAdmin() || userSession.isModerator()) {
                    if (reviewStatus) {
                        options = new RegState[3];
                        options[0] = RegState.FOLLOW_UP;
                        options[1] = RegState.RECOMMENDED;
                        options[2] = RegState.NOT_RECOMMENDED;
                    } else {
                        options = new RegState[1];
                        options[0] = RegState.FOLLOW_UP;
                    }
                } else {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                }
                break;
            case RECOMMENDED:
                if (userSession.isAdmin() || userSession.isModerator() || userSession.isHead()) {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                    options[0] = RegState.REJECTED;
                }
                break;
            case REGISTERED:
                options = new RegState[3];
                options[0] = RegState.DISCONTINUED;
                options[1] = RegState.XFER_APPLICANCY;
                break;
            case FOLLOW_UP:
                options = new RegState[7];
                options[0] = RegState.FEE;
                options[1] = RegState.VERIFY;
                options[2] = RegState.SCREENING;
                options[3] = RegState.REVIEW_BOARD;
                options[4] = RegState.SCREENING;
                options[5] = RegState.REVIEW_BOARD;
                options[6] = RegState.DEFAULTED;
                break;
            case NOT_RECOMMENDED:
                options = new RegState[1];
                options[0] = RegState.REJECTED;
                break;
            case DEFAULTED:
                options = new RegState[1];
                options[0] = RegState.FOLLOW_UP;
                break;
            case REJECTED:
                options = new RegState[1];
                options[0] = RegState.FOLLOW_UP;
                break;
        }
        return Arrays.asList(options);


    }
    
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
//            regState.add(RegState.VERIFY);
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

    public String generateRegNo() {
        int count = productDAO.findCountRegProduct();
        String regNO = String.format("%04d", count);
        String appNo = prodApp.getProdAppNo();
        appNo = appNo.substring(0, 4);
        String appType = "NMR";
        int dt = Calendar.getInstance().get(Calendar.YEAR);
        String year = "" + dt;
        regNO = regNO + appNo + appType + year;
        return regNO;

    }

    @Override
    public String generateAppNo(ProdApplications prodApp) {
        RegistrationUtil registrationUtil = new RegistrationUtil();
        String appType;
        if (prodApp.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
            appType = "NMR";
        }
        if (prodApp.getProdAppType().equals(ProdAppType.GENERIC) || prodApp.getProdAppType().equals(ProdAppType.GENERIC_NO_BE)) {
            appType = "GEN";
        }
        if (prodApp.getProdAppType().equals(ProdAppType.RENEW)) {
            appType = "REN";
        }else if (prodApp.getProdAppType().equals(ProdAppType.VARIATION)) {
            appType = "VAR";
        }else {
            appType = "NMR";
        }

        //return registrationUtil.generateAppNo(prodApp.getId(), "NMR");
        return registrationUtil.generateAppNo(prodApp.getId(), appType);

    }

    public List<ProdApplications> getFeedbackApplications(UserSession userSession) {
        List<ProdApplications> prodApplicationses = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<ReviewStatus> revState = new ArrayList<ReviewStatus>();
        revState.add(ReviewStatus.FIR_SUBMIT);
        params.put("reviewState", revState);
        List<RegState> regState = new ArrayList<RegState>();
        regState.add(RegState.REVIEW_BOARD);
        regState.add(RegState.FOLLOW_UP);
        params.put("regState", regState);
        if (!userSession.isAdmin()) {
            if (userSession.isModerator()) {
                params.put("moderatorId", userSession.getLoggedINUserID());
            } else if (userSession.isReviewer()) {
                if (workspaceDAO.findAll().get(0).isDetailReview()) {
                    params.put("reviewer", userSession.getLoggedINUserID());

                } else
                    params.put("reviewer", userSession.getLoggedINUserID());
            } else if (userSession.isCompany()) {
                params.put("userId", userSession.getLoggedINUserID());
            }
        }
        prodApplicationses = prodApplicationsDAO.findProdAppByReviewStatus(params);

        return prodApplicationses;
    }

    public List<ProdApplications> getNewVariationApp(UserSession userSession){
    	  List<ProdApplications> prodApplicationses = null;  
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	    List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            params.put("regState", regState);
            if (!userSession.isAdmin()) {
                if (userSession.isModerator()) {
                    params.put("moderatorId", userSession.getLoggedINUserID());
                } else if (userSession.isReviewer()) {
                    if (workspaceDAO.findAll().get(0).isDetailReview()) {
                        params.put("reviewer", userSession.getLoggedINUserID());

                    } else
                        params.put("reviewer", userSession.getLoggedINUserID());
                } else if (userSession.isCompany()) {
                    params.put("userId", userSession.getLoggedINUserID());
                }
            }
           params.put("prodAppType",ProdAppType.VARIATION);
           prodApplicationses= prodApplicationsDAO.getProdAppByParams(params);
           return prodApplicationses;

    }
    /**
     * Create review details and save it to letters (one point to find all generated documents)
     * @param prodApplications
     * @return
     */
    public String createReviewDetails(ProdApplications prodApplications) {
        prodApp = prodApplications;
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
        Properties prop = fetchReviewDetailsProperties();
        if(prop != null){
            Product prod = prodApp.getProduct();
            try {
                JasperPrint jasperPrint;
                HashMap<String, Object> param = new HashMap<String, Object>();
                UtilsByReportsET utilsByReports = new UtilsByReportsET();
                utilsByReports.init(param, prodApp, prod);
                utilsByReports.putNotNull(UtilsByReportsET.KEY_PRODNAME, "", false);
                utilsByReports.putNotNull(UtilsByReportsET.KEY_MODERNAME, "", false);

                //TODO chief name from properties!!
                JRMapArrayDataSource source = ReviewDetailPrintMZ.createReviewSourcePorto(prodApplications,bundle, prop, prodCompanyDAO, customReviewDAO, reviewInfoDAO, param);
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

	public List<ProdApplications> getAllAncestor(ProdApplications proda) {
		ProdApplications an=proda;
        List<ProdApplications> ans = null;
        if (an.getParentApplication()!=null){
            ans=new ArrayList<ProdApplications> ();
            while (an!=null) {
            if (an.getParentApplication()==null)  return ans;
                an=	prodApplicationsDAO.findProdApplications(an.getParentApplication().getId());
                if (an!=null){
                    ans.add(an);
                    an=an.getParentApplication();
                }
            }
        }
		return ans;
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createRegCert(ProdApplications _prodApp) {
		this.prodApp = _prodApp;
		Product prod = prodApp.getProduct();
		File invoicePDF = null;
		try {
			invoicePDF = File.createTempFile("" + prod.getProdName() + "_registration", ".pdf");
			JasperPrint jasperPrint = initRegCert();
			if (jasperPrint==null)
				throw new JRException("Error during creation of certificate");
			net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
			prodApp.setRegCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
			prodApplicationsDAO.updateApplication(prodApp);
			return "created";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public JasperPrint initRegCert(){
		product = productDAO.findProduct(prodApp.getProduct().getId());
		JasperPrint jasperPrint = null;
		System.out.println("product found");
		String regDt = DateFormat.getDateInstance().format(prodApp.getRegistrationDate());
		String expDt = DateFormat.getDateInstance().format(prodApp.getRegExpiryDate());

		URL resource = getClass().getResource("/reports/reg_letter.jasper");
		System.out.println("resource found");
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		utilsByReports.init(param, prodApp, product);
		param.put("prodappid", prodApp.getId());
		
		String fullNo = prodApp.getProdRegNo();	
		if (fullNo==null) {	
			fullNo=String.valueOf(prodApp.getProduct().getId())+"/"+generateAppNo(prodApp);
			//fullNo =  String.valueOf(prodApp.getId())  +"/" + String.valueOf(prodApp.getProduct().getId());;
		}				
		utilsByReports.putNotNull(UtilsByReportsET.KEY_REG_NUMBER, fullNo ,true);
		
		String certNo = "0000000000" + String.valueOf(prodApp.getId()) + String.valueOf(prodApp.getProduct().getId());
		certNo = (prodApp.getProdAppType()!=null? prodApp.getProdAppType():"")+ "/"+ certNo.substring(certNo.length() - 10, certNo.length());
		param.put("cert_no",certNo);
		
		param.put("productDescription",prodApp.getProduct().getProdDesc());
		List<UseCategory> cats = product.getUseCategories();
		String catStr="";
		for(UseCategory cat:cats){
			if (!"".equals(cat)){
				catStr = cat.name();
			}else{
				catStr = catStr +"," + cat.name();
			}
		}
		param.put("prescription",catStr.toLowerCase());
		
		utilsByReports.putNotNull(UtilsByReportsET.KEY_PRODNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_GENNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_DOSFORM, "", false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_PRODSTRENGTH, "", false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_PACKSIZE, "", false);
		
		String t = "";
		if(prodApp != null){						
			if(prodApp.getProdAppType() != null){
				if( prodApp.getProdAppType().equals(ProdAppType.GENERIC)){
					t = "Generic with bio equivalency";
				}
				else if( prodApp.getProdAppType().equals(ProdAppType.GENERIC_NO_BE)){
					t = "Generic without bio equivalency";
				}
				else{ 
					t = prodApp.getProdAppType().toString();
				}
			}
		}
		utilsByReports.putNotNull(UtilsByReportsET.KEY_APPTYPE, t);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_APPNAME, "", false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_REG_DATE,"",false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_SHELFINE,"",false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_PROD_ROUTE_ADMINISTRATION,"",false);
		utilsByReports.putNotNull(UtilsByReportsET.KEY_EXPIRY_DATE,"",false);
		
		
		/** licenseHolder*/			
		licenseHolder = customLicHolderDAO.findLicHolderByProduct(product.getId());
		if(licenseHolder!=null){			
			
			String lhName="";
			if(licenseHolder!=null){
				lhName = licenseHolder.getName();
			}
			utilsByReports.putNotNull(UtilsByReportsET.KEY_LICH_NAME,lhName,true);
			
			String licAdr = "";
			if(licenseHolder.getAddress()!=null){
				licAdr += licenseHolder.getAddress().getAddress1()!=null?licenseHolder.getAddress().getAddress1()+", ":"";
				licAdr += licenseHolder.getAddress().getAddress2()!=null?licenseHolder.getAddress().getAddress2()+", ":"";
				licAdr += licenseHolder.getAddress().getCountry()!=null?licenseHolder.getAddress().getCountry():"";
			}
			
			utilsByReports.putNotNull(UtilsByReportsET.KEY_LICH_ADDRESS,licAdr,true);
		}
		/** */		
		String ind = product.getIndications()!=null?product.getIndications():"";
		utilsByReports.putNotNull(UtilsByReportsET.KEY_INDICATION,ind,true);
		
		String ct = product.getContType()!=null? product.getContType():"";
		utilsByReports.putNotNull(UtilsByReportsET.KEY_CONTTYPE,ct,true);
		
		//TODO				
		 ArrayList<Manufac> dataList = new ArrayList<Manufac>();
		 ArrayList<ActiveIngredient> dataList1 = new ArrayList<ActiveIngredient>();
		 ArrayList<InactiveIngredient> dataList2 = new ArrayList<InactiveIngredient>();
		 
		if(product.getProdCompanies()!=null){
			List<ProdCompany> companyList = product.getProdCompanies();
			
			if (companyList != null){				
				for(ProdCompany company:companyList){
					Manufac manuf = new Manufac();	
				  if(company.getCompany()!=null){
					  String nameComp = company.getCompany().getCompanyName()!=null?company.getCompany().getCompanyName():"";
					  manuf.setcompanyName(nameComp);
					
					String addr="";
					if(company.getCompany().getAddress()!=null){
						if(company.getCompany().getAddress().getAddress1()!=null){
							if(!"".equals(company.getCompany().getAddress().getAddress1())){
								addr+=company.getCompany().getAddress().getAddress1()+", ";
							}							
						}
						if(company.getCompany().getAddress().getAddress2()!=null){
							if(!"".equals(company.getCompany().getAddress().getAddress2())){
								addr+=company.getCompany().getAddress().getAddress2()+", ";
							}
						}
						if(company.getCompany().getAddress().getZipcode()!=null){
							if(!"".equals(company.getCompany().getAddress().getZipcode())){
								addr+=company.getCompany().getAddress().getZipcode()+", ";
							}
						}
						if(company.getCompany().getAddress().getCountry()!=null){
							if(!"".equals(company.getCompany().getAddress().getCountry())){
								addr+=company.getCompany().getAddress().getCountry();
							}
						}
					}
					manuf.setaddr(addr);
				
					String typeName = "", type="";	
					  if(company.getCompanyType()!=null){
						  typeName = company.getCompanyType().name()!=null?company.getCompanyType().name():"";					  	
										
						if("API_MANUF".equals(typeName)) type = "API Manufacturer";
						if("FIN_PROD_MANUF".equals(typeName)) type = "Final Product Manufacturer";
						if("BULK_MANUF".equals(typeName)) type ="Bulk Manufacturer";
						if("PRI_PACKAGER".equals(typeName)) type = "Primary Packager";
						if("SEC_PACKAGER".equals(typeName)) type = "Secondary Packager";
						if("FPRC".equals(typeName)) type = "Finish Product Release Controller";
						if("FPRR".equals(typeName)) type = "Finish Product Release Responsibility";						 
					  }
					  manuf.setcompanyType(type);					 
				  }	
				  dataList.add(manuf);
				}
			}			
			JRBeanCollectionDataSource dataSource = new  JRBeanCollectionDataSource(dataList);
			param.put(UtilsByReportsET.FTR_DATASOUTCE,dataSource);
		
			/**	Active Ingredient(s) */	
			List<ProdInn> prodInn = product.getInns();
			if(prodInn!=null){				
				for(ProdInn el:prodInn){
					ActiveIngredient ai = new ActiveIngredient();
					if(el!=null){
						String inn = "", dosStrength="", uom ="", refStd="";
						if(el.getInn()!=null){
							inn = el.getInn().getName()!=null?el.getInn().getName():"";
						}							
						dosStrength = el.getDosStrength()!=null?el.getDosStrength():"";	
						refStd = el.getRefStd()!=null?el.getRefStd():"";
						if(el.getDosUnit()!=null){
							uom = el.getDosUnit().getUom()!=null?el.getDosUnit().getUom():"";
						}					
					
						ai.setinn(inn);
						ai.setdosage_strength(dosStrength);
						ai.setrefStd(refStd);
						ai.setuom(uom);
					}
					dataList1.add(ai);
				}
			}			
			JRBeanCollectionDataSource dataSource1 = new  JRBeanCollectionDataSource(dataList1);
			param.put(UtilsByReportsET.FTR_DATASOUTCE1,dataSource1);
			
			/**	Inactive Ingredient(s) */		
			List<ProdExcipient> prodExcipient = product.getExcipients();
			if(prodExcipient!=null){	
				for(ProdExcipient p: prodExcipient){
					InactiveIngredient inA = new InactiveIngredient();
					if(p!=null){						
						String excipient="", dosStr="", uom = "", refStd="";
						if(p.getExcipient()!=null){
							excipient = p.getExcipient().getName()!=null?p.getExcipient().getName():"";
						}	
						dosStr = p.getDosStrength()!=null?p.getDosStrength():"";
						if(p.getDosUnit()!=null){	
							uom = p.getDosUnit().getUom()!=null?p.getDosUnit().getUom():"";
						}
						refStd = p.getRefStd()!=null?p.getRefStd():"";					
						
						inA.setname(excipient);
						inA.setrefStd(refStd);
						inA.setdosage_strength(dosStr);
						inA.setuom(uom);
					}
					dataList2.add(inA);
				}				
			}
			
			JRBeanCollectionDataSource dataSource2 = new  JRBeanCollectionDataSource(dataList2);
			param.put(UtilsByReportsET.FTR_DATASOUTCE2,dataSource2);
						
			try {			
				jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, new JREmptyDataSource());
                if (jasperPrint.getPages().size()>1)
                    jasperPrint.removePage(jasperPrint.getPages().size()-1);
			} catch (JRException e) {				
				e.printStackTrace();
			} 

		}
		return jasperPrint;	
	}

	public LicenseHolder getLicenseHolder() {
		return licenseHolder;
	}

	public void setLicenseHolder(LicenseHolder licenseHolder) {
		this.licenseHolder = licenseHolder;
	}

}
