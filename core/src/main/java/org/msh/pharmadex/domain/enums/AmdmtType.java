package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AmdmtType {
    NEW,
    NOTIFY,
    UPDATE;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
