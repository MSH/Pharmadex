package org.msh.pharmadex.domain.amendment;

/**
 * Author: usrivastava
 */
public enum AmdType {
    PROD_NAME,
    PROD_MANUF,
    APPLICANT;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
