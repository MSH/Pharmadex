package org.msh.pharmadex.domain.amendment;

/**
 * Author: usrivastava
 */
public enum AmdState {
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
