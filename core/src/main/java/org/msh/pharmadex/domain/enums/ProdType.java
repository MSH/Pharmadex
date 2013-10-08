package org.msh.pharmadex.domain.enums;

public enum ProdType {
	HUMAN,
	VETENIARY,
	DEVICE,
	COMPLIMENTARY_MEDS;
	
	public String getKey() {
		return getClass().getSimpleName().concat("." + name());
	}	
	

}
