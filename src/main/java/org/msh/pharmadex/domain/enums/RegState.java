package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum RegState {
    SAVED,
    NEW_APPL,
    FEE,
    VERIFY,
    SCREENING,
    FOLLOW_UP,
    REVIEW_BOARD,
    RECOMMENDED,
    REGISTERED,
    REJECTED,
    DISCONTINUED,
    XFER_APPLICANCY,
    DEFAULTED;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
