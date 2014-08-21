package org.msh.pharmadex.domain.enums;

public enum ProdCategory {
    HUMAN,
    VETENIARY,
    R,
    E;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
