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
    APPL_FEE,
    REVIEW_BOARD,
    RECOMMENDED,
    REGISTERED,
    REJECTED,
    DISCONTINUED,
    XFER_APPLICANCY,
    DEFAULTED,
    NOT_RECOMMENDED,
    SUSPEND,
    CANCEL,
    RENEWED,
    ARCHIVE;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
