package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum CTDModule {

    MODULE_1,
    MODULE_2,
    MODULE_3,
    MODULE_4,
    MODULE_5,
    ALL;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
