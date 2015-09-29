package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 1/20/15.
 */
public enum UseCategory {
    SCH_NARCOTIC,
    PRESCRIPTION,
    HOSPITAL,
    OTC;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
