package org.msh.pharmadex.domain.enums;

public enum ApplicantType {
	MANUFACTURER,
	IMPORTER,
	DISTRIBUTOR,
	PACKAGER,
	DISPENSER,
	HEATH_FACILITY,
	NEW_ENTITY,
	SUPPLIER,
	WHOLESALLER;
	
	
	public String getKey() {
		return getClass().getSimpleName().concat("." + name());
	}	
	

}
