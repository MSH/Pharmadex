package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum ProdDrugType {
    PHARMACEUTICAL,
    BIOLOGICAL,
    RADIO_PHARMA,
    VACCINE,
    MEDICAL_DEVICE,
    COMPLIMENTARY_MEDS;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
