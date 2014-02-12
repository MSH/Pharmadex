package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum ProdDrugType {
    BIOLOGICAL,
    RADIO_PHARMA,
    PHARMACEUTICAL,
    MEDICAL_DEVICE,
    COMPLIMENTARY_MEDS;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
