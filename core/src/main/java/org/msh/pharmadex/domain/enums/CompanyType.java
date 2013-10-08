package org.msh.pharmadex.domain.enums;

public enum CompanyType {
    APPLICANT,
	MANUFACTURER,
	PACKAGER,
    FPRC,
    FPRR;

	
	public String getKey() {
		return getClass().getSimpleName().concat("." + name());
	}	
	

}
