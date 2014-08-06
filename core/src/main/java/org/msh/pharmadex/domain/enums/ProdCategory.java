package org.msh.pharmadex.domain.enums;

public enum ProdCategory {
    HUMAN,
    VETENIARY,
    UNKNOWN;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
