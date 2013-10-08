package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum ProdDrugType {
    BIOLOGICAL,
    RADIO_PHARMA,
    NAT_HEALTH_PROD,
    PHARMACEUTICAL,
    MEDICAL_DEVICE;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
