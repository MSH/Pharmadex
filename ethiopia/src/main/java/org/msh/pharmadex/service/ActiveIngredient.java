package org.msh.pharmadex.service;

public class ActiveIngredient {
	String inn;
	String refStd;
	String dosage_strength;
	String uom;
	
	public String getinn() {
		return inn;
	}
	public void setinn(String inn) {
		this.inn = inn;
	}
	
	public String getrefStd() {
		return refStd;
	}
	public void setrefStd(String refStd) {
		this.refStd = refStd;
	}
	
	public String getdosage_strength() {
		return dosage_strength;
	}
	public void setdosage_strength(String dosage_strength) {
		this.dosage_strength = dosage_strength;
	}
	
	public String getuom() {
		return uom;
	}
	public void setuom(String uom) {
		this.uom = uom;
	}
	
	
}
