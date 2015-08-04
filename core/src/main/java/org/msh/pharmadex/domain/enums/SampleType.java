package org.msh.pharmadex.domain.enums;

/**
 * Created by usrivastava on 05/22/2015.
 */
public enum SampleType {
    MED_SAMPLE,
    INTERNAL_STD,
    HPLC_COLUMN,
    SPECIFIC_CHEMICAL,
    REF_STD,
    TEST_METHOD;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
