package org.msh.pharmadex.domain.enums;

public enum CompanyType {
    MANUFACTURER,
    PACKAGER,
    FPRC,
    FPRR;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
