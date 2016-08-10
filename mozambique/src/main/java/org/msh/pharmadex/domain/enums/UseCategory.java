package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 1/20/15.
 */
public enum UseCategory {
    SCH_NARCOTIC, //0
    PRESCRIPTION, //1
    HOSPITAL, //2
    OTC; //3

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
