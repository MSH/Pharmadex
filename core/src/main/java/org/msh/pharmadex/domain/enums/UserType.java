package org.msh.pharmadex.domain.enums;

public enum UserType {
    STAFF,
    COMPANY,
    INSPECTOR,
    TIPC,
    PORT_INSPECTOR,
    EXTERNAL;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
