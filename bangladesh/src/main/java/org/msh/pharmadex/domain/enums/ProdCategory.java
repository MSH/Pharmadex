package org.msh.pharmadex.domain.enums;

public enum ProdCategory {
    HUMAN,
    VETERINARY,
    R,
    E;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
