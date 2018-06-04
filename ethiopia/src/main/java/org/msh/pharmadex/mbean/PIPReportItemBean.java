package org.msh.pharmadex.mbean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by dudchenko.
 * By PIP report and PO report table
 */
@ManagedBean
@ViewScoped
public class PIPReportItemBean implements Serializable{

	private static final long serialVersionUID = -1115083023772450828L;
	
	private int bysort = 1;
	/** number of item */
	private int nn = 0;
	private String prodName = "";
	private String dosStren = "";
	private String unit = "";
	private String dosForm = "";
	private String descript = "";
	private Long count = new Long(0);
	private Long totalQuan = new Long(0);
	private Double totalPrice = new Double(0);

	public int getBysort() {
		return bysort;
	}

	public void setBysort(int bys) {
		this.bysort = bys;
	}
	
	public int getNn() {
		return nn;
	}

	public void setNn(int no) {
		this.nn = no;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getDosStren() {
		return dosStren;
	}

	public void setDosStren(String dosStren) {
		this.dosStren = dosStren;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDosForm() {
		return dosForm;
	}

	public void setDosForm(String dosForm) {
		this.dosForm = dosForm;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getTotalQuan() {
		return totalQuan;
	}

	public void setTotalQuan(Long totalQuan) {
		this.totalQuan = totalQuan;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
}
