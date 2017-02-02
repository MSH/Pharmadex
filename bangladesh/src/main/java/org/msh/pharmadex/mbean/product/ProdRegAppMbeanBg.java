package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;

/**
 * Author: dudchenko
 */
@ManagedBean
@ViewScoped
public class ProdRegAppMbeanBg implements Serializable {

	@ManagedProperty(value = "#{prodRegAppMbean}")
	private ProdRegAppMbean prodRegAppMbean;

	private List<ProdCompany> prodCompanies;

	public ProdRegAppMbean getProdRegAppMbean() {
		return prodRegAppMbean;
	}

	public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
		this.prodRegAppMbean = prodRegAppMbean;
	}

	public List<ProdCompany> getProdCompanies() {
		prodCompanies = new ArrayList<ProdCompany>();

		Product prod = getProdRegAppMbean().getProduct();
		if(prod != null){
			List<ProdCompany> comps = prod.getProdCompanies();
			List<ProdInn> inns = prod.getInns();
			List<ProdExcipient> excs = prod.getExcipients();

			if(comps != null && comps.size() > 0)
				prodCompanies.addAll(comps);

			if(inns != null && inns.size() > 0){
				for(ProdInn inn:inns){
					if(inn.getCompany() != null){
						ProdCompany pcom = new ProdCompany(prod, inn.getCompany(), CompanyType.API_MANUF);
						prodCompanies.add(pcom);
					}
				}
			}

			if(excs != null && excs.size() > 0){
				for(ProdExcipient exc:excs){
					if(exc.getCompany() != null){
						ProdCompany pcom = new ProdCompany(prod, exc.getCompany(), CompanyType.EXC_MANUF);
						prodCompanies.add(pcom);
					}
				}
			}
		}
		return prodCompanies;
	}

	public void setProdCompanies(List<ProdCompany> prodCompanies) {
		this.prodCompanies = prodCompanies;
	}

	public boolean visBtnDelete(ProdCompany pr){
		return (pr.getId() != null);
	}
	/**
	 * Try to formulate only
	 * @return
	 */
	public List<CompanyType> getCompanyType() {
		List<CompanyType> list = new ArrayList<CompanyType>();
		Product prod = getProdRegAppMbean().getProduct();
		if(prod != null){
			List<ProdCompany> comps = prod.getProdCompanies();
			boolean hasManuf = false;
			if(comps != null){ //may be first entry!
				for(ProdCompany pc : comps){
					if(pc.getCompanyType().equals(CompanyType.FIN_PROD_MANUF) || pc.getCompanyType().equals(CompanyType.TOLL_MANUF)){
						hasManuf=true;
						break;
					}
				}
			}
			for(CompanyType t:CompanyType.values())
				if(!t.equals(CompanyType.API_MANUF) && !t.equals(CompanyType.EXC_MANUF)){
					if(hasManuf & (t.equals(CompanyType.TOLL_MANUF) || t.equals(CompanyType.FIN_PROD_MANUF))){
						//nothing to do!
					}else{
						list.add(t);
					}
				}
		}
		return list;
	}

}
