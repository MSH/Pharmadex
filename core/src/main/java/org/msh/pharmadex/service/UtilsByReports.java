package org.msh.pharmadex.service;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.hibernate.Hibernate;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.springframework.stereotype.Component;

/**
 * Author: dudchenko
 */
@Component
public class UtilsByReports implements Serializable {

	private static final long serialVersionUID = 5110624647990815527L;

	//private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");	
	
	public static final String FTR_DATASOUTCE = "filterDataSource";
	public static final String FTR_DATASOUTCE1  = "filterDataSource1";
	public static final String FTR_DATASOUTCE2  = "filterDataSource2";
	public static final String FTR_A1DATASOUTCE ="filterA1DataSource";
	public static final String FTR_A2DATASOUTCE = "filterA2DataSource";
	public static final String FTR_F16DATASOUTCE = "filterF16DataSource";
	public static final String FTR_F13DATASOUTCE = "filterF13DataSource";
	public static final String FTR_F8DATASOUTCE = "filterF8DataSource";
	public static final String FTR_F9DATASOUTCE = "filterF9DataSource";
	public static final String FLD_DEFICITEM_NAME = "defItemName";
	public static final String FLD_DEFICITEM_NAMEMZ = "defItemNameMZ";
	public static final String FLD_PROD_NAME = "prodName";
	public static final String FLD_REG_NUMBER = "regNumber";
	
	public static String KEY_MODERNAME = "moderName";
	public static String KEY_MODINITIALS = "modInitials";
	public static String KEY_SCRINITIALS = "scrInitials";
	public static String KEY_APPNAME = "appName";
	public static String KEY_FULLAPPNAME = "fullAppName";
	public static String KEY_ADDRESS1 = "address1";
	public static String KEY_ADDRESS2 = "address2";
	public static String KEY_COUNTRY = "country";
	public static String KEY_COUNTRY_NAME = "countryName";
	public static String KEY_APPNUMBER = "appNumber";
	public static String KEY_ID = "id";
	public static String KEY_PRODNAME = "prodName";
	public static String KEY_GENNAME = "genName";
	public static String KEY_PRODSTRENGTH = "prodStrength";
	public static String KEY_DOSFORM = "dosForm";
	public static String KEY_MANUFNAME = "manufName";
	public static String KEY_FULLMANUFNAME = "fullManufName";
	public static String KEY_SUBJECT = "subject";
	public static String KEY_SUBJECT_1 = "subject1";
	public static String KEY_BODY = "body";
	public static String KEY_REGISTRAR = "registrar";
	public static String KEY_APPTYPE = "appType";
	public static String KEY_APPTYPENUM = "appTypeNum";
	public static String KEY_SUMMARY = "summary";
	public static String KEY_SAMPLEQTY = "sampleQty";
	public static String KEY_DUEDATE = "DueDate";
	public static String KEY_CURDATE ="curDate";
	public static String KEY_DUEDATEMZ = "DueDateMZ";
	public static String KEY_COMPANY_NAME = "companyName";
	public static String KEY_COMPANY_PHONE = "companyPhone";
	public static String KEY_COMPANY_FAX  = "companyFax";
	public static String KEY_COMPANY_EMAIL  = "companyEmail";
	
	public static String KEY_REASON = "reason";
	public static String KEY_BATCHNO = "batchNo";
	public static String KEY_REPORT_DATE = "reportDate";
	public static String KEY_DECISION_DATE = "DecisionDate";
	public static String KEY_DECISION = "decision";
	public static String KEY_SHELFINE = "shelfine";
	public static String KEY_INN = "inn";
	public static String KEY_PACKSIZE = "packsize";
	public static String KEY_STORAGE = "storage";
	public static String KEY_EXCIPIENT = "excipient";

	public static String KEY_FNM = "fnm";
	public static String KEY_DRUGTYPE = "drugType";
	public static String KEY_SUBACT = "subact";
	public static String KEY_GEN = "gen";
	public static String KEY_REG_DATE = "regDate";
	public static String KEY_REG_HEAD = "regHead";
	public static String KEY_APPROVED_EXPERT="approvedExpert";
	public static String KEY_DESIGNATION ="designation";
	public static String KEY_EXPIRY_DATE = "expiryDate";
	public static String KEY_REG_NUMBER = "regNumber";
	public static String KEY_SUBMIT_DATE = "submitDate";
	public static String KEY_DOSREC_DATE = "dosRecDate";
	public static String KEY_GESTOR = "gestorDeCTRM";
	public static String KEY_PROD_DETAILS = "fullName";

	public static String KEY_USE_CATEGORY = "usecat";
	/* для letter*/	
	public static String KEY_APPADDRESS = "appAddress";	
	public static String KEY_APPNUM = "appNum";/**номер в их системе (канцелярский номер) */
	public static String KEY_APPUSERNAME = "appUserName"; /** ФИО */
	public static String KEY_USERNAME = "userName"; /** ФИО из формы*/
	public static String KEY_APPUSEREMAIL = "appUserEmail"; /** email*/
	public static String KEY_APPPOST = "appPost"; /**должность*/
	public static String KEY_EXECSUMMARY = "execSummary"; /**заключение модератора*/
	public static String KEY_APPTELLFAX = "appTellFax";
	public static String KEY_APPEMAIL = "appEmail";
	public static String KEY_DAYS = "repdays";
	public static String KEY_PROD_ROUTE_ADMINISTRATION = "prodRouteAdministration";
	public static String KEY_CURUSER = "curUser" ;
	public static String KEY_APPRESPONSIBLE = "appResponsible";
	
	public static String KEY_CHEIFNAME = "cheifName";
	public static String KEY_FIRSTNAME = "firstNames";
	public static String KEY_SECONDNAME = "secondNames";
	
	public static String KEY_ACTIVEINGR="activeIngr";
	public static String KEY_PRODCONTROLLER="prodController";
	public static String KEY_RELEASERESPONSIBILITY ="releaseResponsibility";
	
	private HashMap<String, Object> param = null;
	private HashMap<String, Object> field = null;
	private ProdApplications prodApps = null;
	private Product prod = null;
	
	public void init(HashMap<String, Object> _param, ProdApplications _prodApps, Product _prod){
		this.param = _param;
		if(param == null)
			param = new HashMap<String, Object>();
		
		this.prodApps = _prodApps;
		this.prod = _prod;
	}
	public void initField(HashMap<String, Object> _field, ProdApplications _prodApps, Product _prod){
		this.field = _field;
		if(field == null)
			field = new HashMap<String, Object>();
		
		this.prodApps = _prodApps;
		this.prod = _prod;
	}
	
	public void putNotNull(String key, Object obj){
		if(param == null)
			return;
		param.put(key, obj);
	}
	
	/** onlyStr - true - add in map just string, without considering prodApps or(and) prod*/
	public void putNotNull(String key, String text, boolean onlyStr){
		if(param == null)
			return;
		if(onlyStr){
			if(text != null)
				param.put(key, text);
			else
				param.put(key, "");
		}else{
			putParamByProd(key, text);
			putParamByProdApplications(key, text);
		}
	}
	/** onlyStr - true - add in map just string, without considering prodApps or(and) prod*/
	public void putFieldNotNull(String key, String text, boolean onlyStr){
		if(field == null)
			return;
		if(onlyStr){
			if(text != null)
				field.put(key, text);
			else
				field.put(key, "");
		}else{
			putFieldProd(key, text);
			putFieldProdApplications(key, text);
		}
	}

	private void putParamByProd(String k, String t){ 
		Hibernate.initialize(prod);
		if(prod == null)
			return ;
		String str = "";

		if(k.equals(KEY_PRODNAME)){
			str = prod.getProdName() != null ? prod.getProdName():"";
			param.put(k, str);
		}
		if(k.equals(KEY_PROD_DETAILS)){
			str = prod.getProdName() != null ? prod.getProdName():"";
			if(str.length()>0){
				if(prod.getProdDesc() != null){
					str = str +", "+prod.getProdDesc();
				}
			}
			param.put(k, str);
		}
		if(k.equals(KEY_GENNAME)){
			str = prod.getGenName() != null ? prod.getGenName():"";
			param.put(k, str);
		}
		if(k.equals(KEY_PRODSTRENGTH)){
			str = (prod.getDosStrength() != null ? prod.getDosStrength() : "")
					+ (prod.getDosUnit() != null ? (" " + prod.getDosUnit().getUom()) : "");
			param.put(k, str);
		}
		if(k.equals(KEY_DOSFORM)){
			str = (prod.getDosForm() != null && prod.getDosForm().getDosForm() != null) ? prod.getDosForm().getDosForm():"";
			param.put(k, str);
		}
		if(k.equals(KEY_MANUFNAME)){
			if(prod.getManufName() == null)
				str = takeManufacturerName();
			else
				str = prod.getManufName();
			str = str != null?str:"";
			param.put(k, str);
		}
		if(k.equals(KEY_SUBJECT)){
			str = t + prod.getProdName() != null ? prod.getProdName():"";
			param.put(k, str);
		}
		if(k.equals(KEY_SHELFINE)){
			str = prod.getShelfLife() != null ? prod.getShelfLife():"";
			param.put(k, str);
		}
		if(k.equals(KEY_PACKSIZE)){
			str = prod.getPackSize() != null ? prod.getPackSize():"";
			param.put(k, str);
		}
		if(k.equals(KEY_STORAGE)){
			str = prod.getStorageCndtn() != null ? prod.getStorageCndtn():"";
			param.put(k, str);
		}
		if(k.equals(KEY_INN)){
			List<ProdInn> inns = prod.getInns();
			if(inns != null && inns.size() > 0){
				for(int i = 0; i < inns.size(); i++){
					if(inns.get(i).getInn() != null){
						if(i == (inns.size() - 1))
							str += inns.get(i).getInn().getName();
						else
							str += inns.get(i).getInn().getName() + " + ";
					}
				}
				param.put(k, str);
			}
		}
		if(k.equals(KEY_EXCIPIENT)){
			List<ProdExcipient> exps = prod.getExcipients();
			if(exps != null && exps.size() > 0){
				for(int i = 0; i < exps.size(); i++){
					if(exps.get(i).getExcipient() != null){
						if(i == (exps.size() - 1))
							str += exps.get(i).getExcipient().getName();
						else
							str += exps.get(i).getExcipient().getName() + ", "; //"<br>"
					}
				}
			}
			param.put(k, str);
		}
		if(k.equals(KEY_PROD_ROUTE_ADMINISTRATION)){
			if( prod.getAdminRoute()!= null )
				if(prod.getAdminRoute().getName()!=null)
					 str = prod.getAdminRoute().getName();
			param.put(k, str);
		}
		
	}
	
	private void putParamByProdApplications(String k, String t){
		
		if(prodApps == null)
			return ;
		String str = "";
		Hibernate.initialize(prodApps);
		if(k.equals(KEY_MODERNAME)){
			if(prodApps.getModerator() != null){
				str= prodApps.getModerator().getName();
				param.put(k, str);
			}
		}		
		if(k.equals(KEY_MODINITIALS)){
			if(prodApps.getModerator() != null){
				if( prodApps.getModerator().getName()!=null){
					String [] res = prodApps.getModerator().getName().split(" ");
					for (String item : res) {
						str+=item.substring(0, 1);
					}
				}			 
			
				param.put(k, str);
			}
		}
		if(k.equals(KEY_APPNAME)){
			str = (prodApps.getApplicant() != null && prodApps.getApplicant().getAppName() != null)?prodApps.getApplicant().getAppName():"";
			param.put(k, str);
		}
		if(k.equals(KEY_ADDRESS1)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress1() != null ? prodApps.getApplicant().getAddress().getAddress1():"";
				param.put(k, str);
		}
		if(k.equals(KEY_ADDRESS2)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress2() != null ? prodApps.getApplicant().getAddress().getAddress2():"";
				param.put(k, str);
		}
		if(k.equals(KEY_COUNTRY)){
			if(prodApps.getApplicant() != null && 
					prodApps.getApplicant().getAddress() != null &&
					prodApps.getApplicant().getAddress().getCountry() != null)
				str = prodApps.getApplicant().getAddress().getCountry().getCountryName() != null ? prodApps.getApplicant().getAddress().getCountry().getCountryName():"";
				param.put(k, str);
		}
		if(k.equals(KEY_APPNUMBER)){
			str = prodApps.getProdAppNo() != null ? prodApps.getProdAppNo():"";
			param.put(k, str);
		}
		if(k.equals(KEY_REG_NUMBER)){
			str = prodApps.getProdRegNo() != null ? prodApps.getProdRegNo():"";
			param.put(k, str);
		}
		if(k.equals(KEY_ID)){
			param.put(k, prodApps.getId());
		}
		if(k.equals(KEY_COMPANY_NAME)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getAppName() != null ? prodApps.getApplicant().getAppName():"";				
				param.put(k, str);
		}
		if(k.equals(KEY_COMPANY_PHONE)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getPhoneNo() != null ? prodApps.getApplicant().getPhoneNo():"";
				param.put(k, str);
		}
		if(k.equals(KEY_COMPANY_FAX)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getFaxNo() != null ? prodApps.getApplicant().getFaxNo():"";
				param.put(k, str);
		}
		if(k.equals(KEY_COMPANY_EMAIL)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getEmail() != null ? prodApps.getApplicant().getEmail():"";
				param.put(k, str);
		}
		if(k.equals(KEY_REG_DATE)){
			if(prodApps.getRegistrationDate() != null)
				str = getDateformat().format(prodApps.getRegistrationDate());
			param.put(k, str);
		}
		if(k.equals(KEY_EXPIRY_DATE)){
			if(prodApps.getRegExpiryDate() != null)
				str = getDateformat().format(prodApps.getRegExpiryDate());
			param.put(k, str);
		}
		if(k.equals(KEY_SUBMIT_DATE)){
			if(prodApps.getSubmitDate() != null)
				str = getDateformat().format(prodApps.getSubmitDate());
			param.put(k, str);
		}
		if(k.equals(KEY_DOSREC_DATE)){
			if(prodApps.getDosRecDate() != null)
				str = getDateformat().format(prodApps.getDosRecDate());
			param.put(k, str);
		}
		/** letter*/	
		if(k.equals(KEY_APPNUM)){
			if(prodApps.getProdAppNo()!=null)
				str = prodApps.getProdAppNo()!= null ? prodApps.getProdAppNo():"";
				param.put(k, str);
		}		
		if(k.equals(KEY_APPPOST)){			
			if(prodApps.getPosition()!=null)
				str = prodApps.getPosition()!= null ? prodApps.getPosition():"";			
				param.put(k, str);
		}		
		if(k.equals(KEY_APPADDRESS)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress1() != null ? prodApps.getApplicant().getAddress().getAddress1():"";
				 param.put(k, str);
		}		
		if(k.equals(KEY_APPUSERNAME)){			
			if(prodApps.getApplicantUser()!=null)
				if( prodApps.getApplicantUser().getUsername()!=null)
				str = prodApps.getApplicantUser().getName();//getUsername();	
				param.put(k, str);
		}		
		if(k.equals(KEY_APPTELLFAX)){			
			if(prodApps.getApplicant() != null){				
				str = prodApps.getApplicant().getPhoneNo()!=null? prodApps.getApplicant().getPhoneNo()+" / ":"";				
				str += prodApps.getApplicant().getFaxNo()!=null?   prodApps.getApplicant().getFaxNo():"";
				param.put(k, str);
			}
		}		
		if(k.equals(KEY_APPEMAIL)){			
			if(prodApps.getApplicant() != null){					
				str = prodApps.getApplicant().getEmail()!=null? prodApps.getApplicant().getEmail():"";
				param.put(k, str);
			}
		}

	}
	private void putFieldProd(String k, String t) {
		Hibernate.initialize(prod);
		if(prod == null)
			return ;
		String str = "";
		if(k.equals(KEY_PRODNAME) || k.equals(FLD_PROD_NAME) ){
			str = prod.getProdName() != null ? prod.getProdName():"";
			field.put(k, str);
		}		
		if(k.equals(KEY_PROD_DETAILS)){
			str = prod.getProdName() != null ? prod.getProdName():"";
			if(str.length()>0){
				if(prod.getProdDesc() != null){
					str = str +", "+prod.getProdDesc();
				}
			}
			field.put(k, str);
		}
		if(k.equals(KEY_GENNAME)){
			str = prod.getGenName() != null ? prod.getGenName():"";
			field.put(k, str);
		}
		if(k.equals(KEY_PRODSTRENGTH)){
			str = (prod.getDosStrength() != null ? prod.getDosStrength() : "")
					+ (prod.getDosUnit() != null ? (" " + prod.getDosUnit().getUom()) : "");
			field.put(k, str);
		}
		if(k.equals(KEY_DOSFORM)){
			str = (prod.getDosForm() != null && prod.getDosForm().getDosForm() != null) ? prod.getDosForm().getDosForm():"";
			field.put(k, str);
		}
		if(k.equals(KEY_MANUFNAME)){
			if(prod.getManufName() == null)
				str = takeManufacturerName();
			else
				str = prod.getManufName();
			str = str != null?str:"";
			field.put(k, str);
		}
		if(k.equals(KEY_SUBJECT)){
			str = t + prod.getProdName() != null ? prod.getProdName():"";
			field.put(k, str);
		}
		if(k.equals(KEY_SHELFINE)){
			str = prod.getShelfLife() != null ? prod.getShelfLife():"";
			field.put(k, str);
		}
		if(k.equals(KEY_PACKSIZE)){
			str = prod.getPackSize() != null ? prod.getPackSize():"";
			field.put(k, str);
		}
		if(k.equals(KEY_STORAGE)){
			str = prod.getStorageCndtn() != null ? prod.getStorageCndtn():"";
			field.put(k, str);
		}
		if(k.equals(KEY_INN)){
			List<ProdInn> inns = prod.getInns();
			if(inns != null && inns.size() > 0){
				for(int i = 0; i < inns.size(); i++){
					if(inns.get(i).getInn() != null){
						if(i == (inns.size() - 1))
							str += inns.get(i).getInn().getName();
						else
							str += inns.get(i).getInn().getName() + " + ";
					}
				}
				field.put(k, str);
			}
		}
		if(k.equals(KEY_EXCIPIENT)){
			List<ProdExcipient> exps = prod.getExcipients();
			if(exps != null && exps.size() > 0){
				for(int i = 0; i < exps.size(); i++){
					if(exps.get(i).getExcipient() != null){
						if(i == (exps.size() - 1))
							str += exps.get(i).getExcipient().getName();
						else
							str += exps.get(i).getExcipient().getName() + ", "; //"<br>"
					}
				}
			}
			field.put(k, str);
		}
		if(k.equals(KEY_PROD_ROUTE_ADMINISTRATION)){
			if( prod.getAdminRoute()!= null )
				if(prod.getAdminRoute().getName()!=null)
					 str = prod.getAdminRoute().getName();
			field.put(k, str);
		}		
		
	}

	private void putFieldProdApplications(String k, String text) {
		
		if(prodApps == null)
			return ;
		String str = "";
		Hibernate.initialize(prodApps);
		if(k.equals(KEY_MODERNAME)){
			if(prodApps.getModerator() != null){
				str= prodApps.getModerator().getName();
				field.put(k, str);
			}
		}		
		if(k.equals(KEY_MODINITIALS)){
			if(prodApps.getModerator() != null){
				if( prodApps.getModerator().getName()!=null){
					String [] res = prodApps.getModerator().getName().split(" ");
					for (String item : res) {
						str+=item.substring(0, 1);
					}
				}			 
			
				field.put(k, str);
			}
		}
		if(k.equals(KEY_APPNAME)){
			str = (prodApps.getApplicant() != null && prodApps.getApplicant().getAppName() != null)?prodApps.getApplicant().getAppName():"";
			field.put(k, str);
		}
		if(k.equals(KEY_ADDRESS1)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress1() != null ? prodApps.getApplicant().getAddress().getAddress1():"";
				field.put(k, str);
		}
		if(k.equals(KEY_ADDRESS2)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress2() != null ? prodApps.getApplicant().getAddress().getAddress2():"";
				field.put(k, str);
		}
		if(k.equals(KEY_COUNTRY)){
			if(prodApps.getApplicant() != null && 
					prodApps.getApplicant().getAddress() != null &&
					prodApps.getApplicant().getAddress().getCountry() != null)
				str = prodApps.getApplicant().getAddress().getCountry().getCountryName() != null ? prodApps.getApplicant().getAddress().getCountry().getCountryName():"";
				field.put(k, str);
		}
		if(k.equals(KEY_APPNUMBER)){
			str = prodApps.getProdAppNo() != null ? prodApps.getProdAppNo():"";
			field.put(k, str);
		}
		if(k.equals(KEY_REG_NUMBER) || k.equals(FLD_REG_NUMBER) ){
			str = prodApps.getProdRegNo() != null ? prodApps.getProdRegNo():"";
			field.put(k, str);
		}
		if(k.equals(KEY_ID)){
			field.put(k, prodApps.getId());
		}
		if(k.equals(KEY_COMPANY_NAME)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getAppName() != null ? prodApps.getApplicant().getAppName():"";				
				field.put(k, str);
		}
		if(k.equals(KEY_COMPANY_PHONE)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getPhoneNo() != null ? prodApps.getApplicant().getPhoneNo():"";
				field.put(k, str);
		}
		if(k.equals(KEY_COMPANY_FAX)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getFaxNo() != null ? prodApps.getApplicant().getFaxNo():"";
				field.put(k, str);
		}
		if(k.equals(KEY_COMPANY_EMAIL)){
			if(prodApps.getApplicant() != null)
				str = prodApps.getApplicant().getEmail() != null ? prodApps.getApplicant().getEmail():"";
				field.put(k, str);
		}
		if(k.equals(KEY_REG_DATE)){
			if(prodApps.getRegistrationDate() != null)
				str = getDateformat().format(prodApps.getRegistrationDate());
			field.put(k, str);
		}
		if(k.equals(KEY_EXPIRY_DATE)){
			if(prodApps.getRegExpiryDate() != null)
				str = getDateformat().format(prodApps.getRegExpiryDate());
			field.put(k, str);
		}
		if(k.equals(KEY_SUBMIT_DATE)){
			if(prodApps.getSubmitDate() != null)
				str = getDateformat().format(prodApps.getSubmitDate());
			field.put(k, str);
		}
		if(k.equals(KEY_DOSREC_DATE)){
			if(prodApps.getDosRecDate() != null)
				str = getDateformat().format(prodApps.getDosRecDate());
			field.put(k, str);
		}
		/** letter*/	
		if(k.equals(KEY_APPNUM)){
			if(prodApps.getProdAppNo()!=null)
				str = prodApps.getProdAppNo()!= null ? prodApps.getProdAppNo():"";
				field.put(k, str);
		}		
		if(k.equals(KEY_APPPOST)){			
			if(prodApps.getPosition()!=null)
				str = prodApps.getPosition()!= null ? prodApps.getPosition():"";			
				field.put(k, str);
		}		
		if(k.equals(KEY_APPADDRESS)){
			if(prodApps.getApplicant() != null && prodApps.getApplicant().getAddress() != null)
				str = prodApps.getApplicant().getAddress().getAddress1() != null ? prodApps.getApplicant().getAddress().getAddress1():"";
				field.put(k, str);
		}		
		if(k.equals(KEY_APPUSERNAME)){			
			if(prodApps.getUsername()!=null)
				str = prodApps.getUsername()!= null ? prodApps.getUsername():"";			
				field.put(k, str);
		}		
		if(k.equals(KEY_APPTELLFAX)){			
			if(prodApps.getApplicant() != null){				
				str = prodApps.getApplicant().getPhoneNo()!=null? prodApps.getApplicant().getPhoneNo()+" / ":"";				
				str += prodApps.getApplicant().getFaxNo()!=null?   prodApps.getApplicant().getFaxNo():"";
				field.put(k, str);
			}
		}		
		if(k.equals(KEY_APPEMAIL)){			
			if(prodApps.getApplicant() != null){					
				str = prodApps.getApplicant().getEmail()!=null? prodApps.getApplicant().getEmail():"";
				field.put(k, str);
			}
		}

	}
	private String takeManufacturerName(){        
		String manufName = "";
		List<ProdCompany> companyList = prod.getProdCompanies();
		if (companyList != null){
			for(ProdCompany company:companyList){
				if (company.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
					manufName = company.getCompany().getCompanyName();
					return manufName;
				}
			}
		}
		return manufName;
    }
	public static DateFormat getDateformat() {
		DateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(locale!=null){
			dateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);			
		}		
		return dateFormat;
	}
	
}
