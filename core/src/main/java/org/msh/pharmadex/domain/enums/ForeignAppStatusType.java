package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum ForeignAppStatusType {
    AUTHORIZED,
    PENDING,
    WITHDRAWN,
    SUSPENDED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
