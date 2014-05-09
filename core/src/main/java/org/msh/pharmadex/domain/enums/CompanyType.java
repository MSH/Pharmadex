package org.msh.pharmadex.domain.enums;

public enum CompanyType {
    API_MANUF,
    FIN_PROD_MANUF,
    PRI_PACKAGER,
    SEC_PACKAGER,
    FPRC,
    FPRR;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
