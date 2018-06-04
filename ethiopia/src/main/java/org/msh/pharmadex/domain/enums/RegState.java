package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum RegState {
    SAVED,
    NEW_APPL,
    PRE_SCREENING,
    FEE,
    VERIFY,
    SCREENING,
    FOLLOW_UP,
    NO_FOLLOW_UP,
    REVIEW_BOARD,
    RECOMMENDED,
    REGISTERED,
    REJECTED,
    DISCONTINUED,
    XFER_APPLICANCY,
    DEFAULTED,
    NOT_RECOMMENDED,
    ARCHIVE,
    SUSPEND,
    CANCEL,
    RENEWED;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
