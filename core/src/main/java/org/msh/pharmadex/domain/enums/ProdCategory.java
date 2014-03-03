package org.msh.pharmadex.domain.enums;

public enum ProdCategory {
    HUMAN,
    VETENIARY;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
