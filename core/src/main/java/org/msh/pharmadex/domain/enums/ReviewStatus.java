package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 12/3/14.
 */
public enum ReviewStatus {

    NOT_ASSIGNED,
    ASSIGNED,
    IN_PROGRESS,
    SEC_REVIEW,
    SUBMITTED,
    FEEDBACK,
    ACCEPTED,
    RFI_SUBMIT,
    RFI_APP_RESPONSE,
    RFI_RECIEVED,
    FIR_SUBMIT,
    FIR_APP_RESPONSE,
    FIR_RECIEVED;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

    }
