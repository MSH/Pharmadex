package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum InvoiceType {
    NEW,
    RENEWAL,
    AMENDMENT;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
