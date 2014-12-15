package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 12/3/14.
 */
public enum ReviewStatus {

    NOT_ASSIGNED,
    ASSIGNED,
    IN_PROGRESS,
    SUBMITTED,
    FEEDBACK,
    ACCEPTED;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

    }
