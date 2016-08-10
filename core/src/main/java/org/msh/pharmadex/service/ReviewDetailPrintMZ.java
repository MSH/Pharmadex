package org.msh.pharmadex.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
		fillItems(res, prop, customReviewDAO);
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
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_1"),fetchFullApplicant(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_2"),prodApp.getProduct().getGenName(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_3"),prodApp.getProduct().getProdName(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_4"),prodApp.getProduct().getDosStrength(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_5"),fetchExcipients(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_6"),prodApp.getProduct().getPackSize(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_7"),prodApp.getProduct().getDosForm().getDosForm(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_8"),prodApp.getProduct().getIndications(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_9"),prodApp.getProduct().getShelfLife(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_10"),prodApp.getProduct().getStorageCndtn(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_11"),prodApp.getProduct().getAdminRoute().getName(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_12"),bundle.getString(prodApp.getProdAppType().getKey()),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_13"),fetchATC_Names(),null);
		fillItemRS(res,prop.getProperty("chapter1"),prop.getProperty("chapter1_14"),fetchManufacture(prodCompanyDAO, false),null);
	}

	private static void fillGeneralText(List<Map<String, Object>> res, Properties prop, ProductCompanyDAO prodCompanyDAO) {
		String chapter2t = prop.getProperty("chapter2_txt").replace("_APPLICANT_", prodApp.getApplicant().getAppName());
		chapter2t = chapter2t.replace("_MANUFACTURER_", fetchManufacture(prodCompanyDAO, true));
		fillItemRS(res,prop.getProperty("chapter2"),null,chapter2t,null);
	}
	
	private static void fillResolutionText(List<Map<String, Object>> res, Properties prop) {
		String number = prodApp.getProdRegNo() != null ? prodApp.getProdRegNo():"";
		String chapter3t = prop.getProperty("chapter3_txt").replace("_REGNUMBER_", number);
		String text = (prodApp.getExecSummary() != null ? prodApp.getExecSummary() + "\n":"") + chapter3t;
		fillItemRS(res, prop.getProperty("chapter3"), null, text, null);
	}

	private static void fillItems(List<Map<String, Object>> res, Properties prop, CustomReviewDAO customReviewDAO) {
		List<ReviewItemReport> items = customReviewDAO.getReviewListByReport(prodApp.getId());
		if(items != null && items.size() > 0){
			// header1-values
			Map<String, List<ReviewItemReport>> map = new HashMap<String, List<ReviewItemReport>>();
			for(ReviewItemReport item:items){
				String header1 = item.getHeader1();
				List<ReviewItemReport> list = map.get(header1);
				if(list == null)
					list = new ArrayList<ReviewItemReport>();
				list.add(item);
				map.put(header1, list);
			}
			if(!map.isEmpty()){
				Iterator<String> it = map.keySet().iterator();
				while(it.hasNext()){
					String header = it.next();
					List<ReviewItemReport> list = map.get(header);
					buildItemValues(res, header, list);
				}
			}
		}
	}
	
	/**
	 * String header - name of item
	 * List<ReviewItemReport> list - comments of item
	 * @param map
	 */
	private static void buildItemValues(List<Map<String, Object>> res, String header, List<ReviewItemReport> list){
		Map<String, List<ReviewItemReport>> map = new HashMap<String, List<ReviewItemReport>>();
		for(ReviewItemReport item:list){
			List<ReviewItemReport> values = map.get(item.getHeader2());
			if(values == null)
				values = new ArrayList<ReviewItemReport>();
			values.add(item);
			map.put(item.getHeader2(), values);
		}
		
		if(!map.isEmpty()){
			Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				List<ReviewItemReport> values = map.get(iterator.next());
				for(int i = 0; i < values.size(); i++){
					printItemReview(res, header, values.get(i), (i == (values.size() - 1)));
				}
			}
		}
	}
	
	private static void printItemReview(List<Map<String, Object>> res, String chapter1, ReviewItemReport item, boolean isPrintEmptyLine){
		String text = "";
		// 1) only first
		if(item.getFirstRevComment() != null && !item.getFirstRevComment().equals("") 
				&& (item.getSecondRevComment() == null || item.getSecondRevComment().equals(""))){
			text = "<b>" + item.getFirstRevName() + "</b>:<br>" + item.getFirstRevComment();
		}
		// 2) only second
		if(item.getSecondRevComment() != null && !item.getSecondRevComment().equals("") 
				&& (item.getFirstRevComment() == null || item.getFirstRevComment().equals(""))){
			text = "<b>" + item.getSecondRevName() + "</b>:<br>" + item.getSecondRevComment();
		}
		// 3) both
		if(item.getFirstRevComment() != null && !item.getFirstRevComment().equals("") 
				&& item.getSecondRevName() != null 
				&& item.getSecondRevComment() != null && !item.getSecondRevComment().equals("")){
			text = "<b>" + item.getFirstRevName() + "</b>:<br>" + item.getFirstRevComment()
			 	+ "<br><br><b>" + item.getSecondRevName() + "</b>:<br>" + item.getSecondRevComment();
		}
		if(!text.isEmpty())
			text += "<br>";
		// file
		InputStream file = null;
		if(item.getFile() != null){
			file = new ByteArrayInputStream(item.getFile());
		}
		if(!text.isEmpty() || file != null)
			fillItemRS(res, chapter1, null, text, file);
	}

	/**
	 * Fill data for a line of the report to result res
	 * @param res list of maps
	 * @param reviewChapter
	 * @param reviewItem
	 * @param reviewItemData
	 * @param reviewItemFile
	 */
	private static void fillItemRS(List<Map<String, Object>> res, String reviewChapter, String reviewItem,
			String reviewItemData, Object reviewItemFile) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("reviewChapter",reviewChapter);
		map.put("reviewItem",reviewItem);
		map.put("reviewItemData",reviewItemData);
		map.put("reviewItemFile",reviewItemFile);
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
					names += (names.isEmpty()?"":", ") + atc.getAtcName();
				}
			}
		}else
			names = "N/A";
		return names;
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
