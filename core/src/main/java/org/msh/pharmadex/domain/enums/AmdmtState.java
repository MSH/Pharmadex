package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AmdmtState {
    NEW_APPLICATION,
    REVIEW,
    SUBMITTED,
    RECOMMENDED,
    APPROVED,
    NOT_RECOMMENDED,
    REJECTED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
