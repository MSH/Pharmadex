package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AmdmtState {
    NEW_APPLICATION,
    REVIEW,
    SUBMITTED,
    FEEDBACK,
    RECOMMENDED,
    NOT_RECOMMENDED,
    APPROVED,
    REJECTED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
