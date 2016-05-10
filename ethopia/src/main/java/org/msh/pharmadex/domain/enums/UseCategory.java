package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 1/20/15.
 */
public enum UseCategory {
    SCH_NARCOTIC,
    PSYCHOTROPIC,
    PRESCRIPTION,
    OTC;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
