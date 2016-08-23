package org.msh.pharmadex.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ProductCompanyDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.mbean.product.ReviewItemReport;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

/**
 * Build printForm by ReviewDetail
 * @author Irina
 *
 */
@Component
public class ReviewDetailPrintMZ implements Serializable {

	private static final long serialVersionUID = 2915932566779556932L;

	private static ProdApplications prodApp;
	private static ResourceBundle bundle = null;

	/**
	 * Create data source for review detail report. Portuguese language only!!!!
	 * @param prodApplications current application
	 * @param bundle language bundle
	 * @param prop report specific properties
	 * @return at least empty map!
	 */	
	public static JRMapArrayDataSource createReviewSourcePorto(ProdApplications prodApplications, ResourceBundle bun, Properties prop, ProductCompanyDAO prodCompanyDAO,
			CustomReviewDAO customReviewDAO) {
		List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
		prodApp = prodApplications;
		bundle = bun;
		fillGeneralRS(res, prop, prodCompanyDAO);
		fillGeneralText(res, prop, prodCompanyDAO);
		fillItems(res, customReviewDAO);
		fillResolutionText(res, prop);
		return new JRMapArrayDataSource(res.toArray());
	}

	/**
	 * Fill first section of the details - general information
	 * @param res result map
	 * @param prop specific properties
	 * @param prodApplications current application
	 */
	private static void fillGeneralRS(List<Map<String, Object>> res, Properties prop, ProductCompanyDAO prodCompanyDAO) {
		if (prop.getProperty("chapter1_1") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_1"), fetchFullApplicant(), null,null,null,null);
		if (prop.getProperty("chapter1_2") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_2"),
					prodApp.getProduct().getGenName(), null,null,null,null);
		if (prop.getProperty("chapter1_3") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_3"),
					prodApp.getProduct().getProdName(), null,null,null,null);
		if (prop.getProperty("chapter1_4") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_4"), fetchFullDosStrength(), null,null,null,null);
		if (prop.getProperty("chapter1_5") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_5"), fetchExcipients(), null,null,null,null);
		if (prop.getProperty("chapter1_6") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_6"),
					prodApp.getProduct().getPackSize(), null,null,null,null);
		if (prop.getProperty("chapter1_7") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_7"),
					prodApp.getProduct().getDosForm().getDosForm(), null,null,null,null);
		if (prop.getProperty("chapter1_8") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_8"),
					prodApp.getProduct().getIndications(), null,null,null,null);
		if (prop.getProperty("chapter1_9") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_9"),
					prodApp.getProduct().getShelfLife(), null,null,null,null);
		if (prop.getProperty("chapter1_10") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_10"),
					prodApp.getProduct().getStorageCndtn(), null,null,null,null);
		if (prop.getProperty("chapter1_11") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_11"),
					prodApp.getProduct().getAdminRoute().getName(), null,null,null,null);
		if (prop.getProperty("chapter1_12") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_12"),
					bundle.getString(prodApp.getProdAppType().getKey()), null,null,null,null);
		if (prop.getProperty("chapter1_13") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_13"), fetchATC_Names(), null,null, null,null);
		if (prop.getProperty("chapter1_14") != null)
			fillItemRS(res, prop.getProperty("chapter1"), prop.getProperty("chapter1_14"),
					fetchManufacture(prodCompanyDAO, false), null,null,null,null);
	}

	private static void fillGeneralText(List<Map<String, Object>> res, Properties prop, ProductCompanyDAO prodCompanyDAO) {
		String chapter2t ="";
		if(prop.getProperty("chapter2_txt")!=null){
			chapter2t = prop.getProperty("chapter2_txt").replace("_APPLICANT_", prodApp.getApplicant().getAppName());
			chapter2t = chapter2t.replace("_MANUFACTURER_", fetchManufacture(prodCompanyDAO, true));				
		}
		if(prop.getProperty("chapter2")!=null)
			fillItemRS(res,prop.getProperty("chapter2"),null,chapter2t,null,null,null,null);
		
	}
	
	private static void fillResolutionText(List<Map<String, Object>> res, Properties prop) {
		/*String number = prodApp.getProdRegNo() != null ? prodApp.getProdRegNo():"";
		String chapter3t = "";
		if(prop.getProperty("chapter3_txt")!=null){
			chapter3t = prop.getProperty("chapter3_txt").replace("_REGNUMBER_", number);
		}
		String text = (prodApp.getExecSummary() != null ? prodApp.getExecSummary() + "\n":"") + chapter3t;		
		fillItemRS(res, prop.getProperty("chapter3"), null, text, null,null,null,null);*/
		String text = prodApp.getExecSummary() != null ? prodApp.getExecSummary():"";
		fillItemRS(res, prop.getProperty("chapter3"), null, text, null, null, null, null);
	}

	private static void fillItems(List<Map<String, Object>> res, CustomReviewDAO customReviewDAO) {
		Map<String, List<ReviewItemReport>> map = customReviewDAO.getReviewListByReportNew(prodApp.getId());
		if(map != null && !map.isEmpty()){
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()){
				String header = it.next();
				List<ReviewItemReport> list = map.get(header);
				Collections.sort(list, new Comparator<ReviewItemReport>() {
					@Override
					public int compare(ReviewItemReport o1, ReviewItemReport o2) {
						Long id1 = o1.getQuestionId();
						Long id2 = o2.getQuestionId();
						return id1.compareTo(id2);
					}
				});
				for(ReviewItemReport item:list){
					printItemReview(res, header, item);
				}
			}
		}
	}
	
	private static void printItemReview(List<Map<String, Object>> res, String chapter1, ReviewItemReport item){
		String text = "";
		//pri_processor
		//sec_processor
		if(item.getFirstRevName() != null){//item.getFirstRevName()
			text = "<b>" + bundle.getString("pri_processor") + "</b>:<br>" + item.getFirstRevComment() + "<br>";
		}
		if(item.getSecondRevName() != null){//item.getSecondRevName()
			text += (text.isEmpty()?"":"<br>") + "<b>" + bundle.getString("sec_processor") + "</b>:<br>" + item.getSecondRevComment() + "<br>";
		}

		if(!text.isEmpty())
			text += "<br>";
		// file
		InputStream file = null;
		if(item.getFile() != null){
			file = new ByteArrayInputStream(item.getFile());
		}
		if(!text.isEmpty() || file != null){
			 String reviewItemHead = item.getHeader2();
			 String pages = item.getPages();
			 String reviewQuestion = item.getReviewQuestion();
			 
			 fillItemRS(res, chapter1, null, text, file,pages,reviewItemHead,reviewQuestion);
				
						
			/* fillItemRS(res, chapter1, null, text, file, pages, null,null);*/
		}
	}

	/**
	 * Fill data for a line of the report to result res
	 * @param res list of maps
	 * @param reviewChapter
	 * @param reviewItem
	 * @param reviewItemData
	 * @param reviewItemFile
	 * @param file 
	 * @param reviewQuestion 
	 * @param pages 
	 */
	private static void fillItemRS(List<Map<String, Object>> res, String reviewChapter, String reviewItem,
				String reviewItemData, Object reviewItemFile, String pagesItem, String reviewItemHead,String reviewQuestion){
	
		Map<String,Object> map = new HashMap<String,Object>();		
		map.put("reviewChapter",reviewChapter);
		map.put("reviewItem",reviewItem);
		map.put("pagesItem",pagesItem);
		map.put("reviewItemHead",reviewItemHead);		
		map.put("reviewItemData",reviewItemData);	
		map.put("reviewItemFile",reviewItemFile);		
		map.put("reviewQuestion",reviewQuestion);		
		res.add(map);
	}

	/**
	 * Fetch full applicant data name + address
	 * @param prodApp 
	 * @return
	 */
	private static String fetchFullApplicant() {
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

	private static String fetchExcipients() {
		String excipients = "";
		if(prodApp != null){
			List<ProdExcipient> excList = prodApp.getProduct().getExcipients();
			if(excList != null && excList.size() > 0){
				for(ProdExcipient pex:excList){
					if(pex.getExcipient() != null)
						excipients += pex.getExcipient().getName() + ", ";
				}
				if(!excipients.isEmpty())
					excipients = excipients.substring(0,  excipients.length() - 2);
			}
		}
		return excipients;
	}

	private static String fetchATC_Names() {
		String names = "";
		if(prodApp != null && prodApp.getProduct() != null){
			List<Atc> atcs = prodApp.getProduct().getAtcs();
			if(atcs != null && atcs.size() > 0){
				for(Atc atc:atcs){
					names += (names.isEmpty()?"":", ") + atc.getDisplayName();
				}
			}
		}else
			names = "N/A";
		return names;
	}
	
	private static String fetchFullDosStrength() {
		String res = "";
		if(prodApp != null && prodApp.getProduct() != null){
			res = prodApp.getProduct().getDosStrength();
			if(res != null && !res.equals("")){
				if(prodApp.getProduct().getDosUnit() != null){
					String unit = prodApp.getProduct().getDosUnit().getUom();
					if(unit !=null && !unit.equals(""))
						res += " " + unit;
				}
			}else
				res = "";
		}
		return res;
	}
	
	private static String fetchManufacture(ProductCompanyDAO prodCompanyDAO, boolean onlyName) {
		String manuf = "";
		if(prodApp != null){
			List<ProdCompany> companyList = prodApp.getProduct().getProdCompanies();
			if (companyList != null){
				for(ProdCompany prod_comp:companyList){
					if (prod_comp.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
						ProdCompany pcomp = prodCompanyDAO.findCompanyByProdCompany(prod_comp.getId());
						Company company = pcomp.getCompany();
						if(company != null){
							manuf = company.getCompanyName() != null ? company.getCompanyName():"";
							if(!onlyName){
								String addr = company.getAddress().getAddress1() != null ? company.getAddress().getAddress1():"";
								if(!(addr.equals("") || addr.equals("NOT SPECIFIED")))
									manuf += "\n" + addr;
								addr = company.getAddress().getAddress2() != null ? company.getAddress().getAddress2():"";
								if(!(addr.equals("") || addr.equals("NOT SPECIFIED")))
									manuf += "\n" + addr;
							}
						}
					}
				}
			}
		}
		return manuf;
	}
}
