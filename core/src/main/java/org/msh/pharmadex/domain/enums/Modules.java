package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum Modules {

    MODULE_1,
    MODULE_2,
    MODULE_3,
    MODULE_4;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
