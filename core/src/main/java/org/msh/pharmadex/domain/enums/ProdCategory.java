package org.msh.pharmadex.domain.enums;

public enum ProdCategory {
    HUMAN,
    VETERINARY,
    UNKNOWN;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
