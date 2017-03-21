package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum RegState {
	//Registration states
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
    ARCHIVE,
    //Milestones
    MS_START,
    MS_SCR_START,
    MS_SCR_END,
    MS_REV_START,
    MS_REV_END,
    MS_END;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
