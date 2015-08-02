package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 12/3/14.
 */
public enum ReviewStatus {

    NOT_ASSIGNED,
    ASSIGNED,
    IN_PROGRESS,
    SEC_REVIEW,
    APPLICANT_RFI,
    RFI_SUBMIT,
    RFI_RECIEVED,
    SUBMITTED,
    FEEDBACK,
    ACCEPTED;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

    }
