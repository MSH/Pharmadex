package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 1/7/15.
 */
public enum AgentType {

    FIRST,
    SECOND,
    THIRD;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
