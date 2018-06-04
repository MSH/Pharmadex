package org.msh.pharmadex.domain.enums;

/**
 * Created by Одиссей on 15.07.2016.
 */
public enum VariationType {

    MINOR,
    MAJOR;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
