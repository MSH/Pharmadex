package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum ReminderType {
    INVOICE,
    FIRST,
    SECOND,
    THIRD,
    DE_REGISTER;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
