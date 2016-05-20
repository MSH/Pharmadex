package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.HashMap;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.springframework.stereotype.Component;

/**
 * Author: dudchenko
 */
@Component
public class UtilsByReports implements Serializable {

	private static final long serialVersionUID = 5110624647990815527L;

	public static String KEY_APPNAME = "appName";
	public static String KEY_ADDRESS1 = "address1";
	public static String KEY_ADDRESS2 = "address2";
	public static String KEY_COUNTRY = "country";
	public static String KEY_COUNTRY_NAME = "countryName";
	public static String KEY_APPNUMBER = "appNumber";
	public static String KEY_ID = "id";
	public static String KEY_PRODNAME = "prodName";
	public static String KEY_PRODSTRENGTH = "prodStrength";
	public static String KEY_DOSFORM = "dosForm";
	public static String KEY_MANUFNAME = "manufName";
	public static String KEY_SUBJECT = "subject";
	public static String KEY_SUBJECT_1 = "subject1";
	public static String KEY_BODY = "body";
	public static String KEY_REGISTRAR = "registrar";
	public static String KEY_APPTYPE = "appType";
	public static String KEY_SUMMARY = "summary";
	public static String KEY_SAMPLEQTY = "sampleQty";
	public static String KEY_DUEDATE = "DueDate";
	public static String KEY_COMPANY_NAME = "companyName";

	private HashMap<String, Object> param = null;
	private ProdApplications prodApps = null;
	private Product prod = null;
	
	public void init(HashMap<String, Object> _param, ProdApplications _prodApps, Product _prod){
		this.param = _param;
		if(param == null)
			param = new HashMap<String, Object>();
		
		this.prodApps = _prodApps;
		this.prod = _prod;
	}
	
	/** onlyStr - true - add in map just string, without considering prodApps or(and) prod*/
	public void putNotNull(String key, String text, boolean onlyStr){
		if(param == null)
			return;
		if(onlyStr)
			param.put(key, text);
		else{
			putParamByProd(key, text);
			putParamByProdApplications(key, text);
		}
	}

	private void putParamByProd(String k, String t){
		if(prod == null)
			return ;
		String str = "";

		if(k.equals(KEY_PRODNAME)){
			str = prod.getProdName() != null ? prod.getProdName():"";
			param.put(k, str);
		}
		if(k.equals(KEY_PRODSTRENGTH)){
			str = (prod.getDosStrength() != null ? prod.getDosStrength() : "")
					+ (prod.getDosUnit() != null ? prod.getDosUnit().getUom() : "");
			param.put(k, str);
		}
		if(k.equals(KEY_DOSFORM)){
			str = (prod.getDosForm() != null && prod.getDosForm().getDosForm() != null) ? prod.getDosForm().getDosForm():"";
			param.put(k, str);
		}
		if(k.equals(KEY_MANUFNAME)){
			str = prod.getManufName() != null ? prod.getManufName():"";
				param.put(k, str);
		}
		if(k.equals(KEY_SUBJECT)){
			str = t + prod.getProdName() != null ? prod.getProdName():"";
			param.put(k, str);
		}
	}
	
	private void putParamByProdApplications(String k, String t){
		if(prodApps == null)
			return ;
		String str = "";
		
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
		if(k.equals(KEY_ID)){
			param.put(k, prodApps.getId());
		}
	}
}
