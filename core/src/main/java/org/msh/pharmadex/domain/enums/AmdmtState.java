package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AmdmtState {
    NEW_APPLICATION,
    REVIEW,
    APPROVED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
