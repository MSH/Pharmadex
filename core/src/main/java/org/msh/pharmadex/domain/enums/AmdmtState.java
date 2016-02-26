package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AmdmtState {
    SAVED,
    NEW_APPLICATION,
    REVIEW,
    SUBMITTED,
    FEEDBACK,
    RECOMMENDED,
    NOT_RECOMMENDED,
    APPROVED,
    REJECTED,
    WITHDRAWN,
    CANCELLED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
