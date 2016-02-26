package org.msh.pharmadex.domain.enums;

/**
 * Created by usrivastava on 05/22/2015.
 */
public enum SampleTestStatus {
    NONE,
    REQUESTED,
    SAMPLE_RECIEVED,
    IN_PROGRESS,
    AVAILABLE,
    RESULT;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
