package org.msh.pharmadex.domain.enums;

public enum ProdType {
    HUMAN,
    VETENIARY;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }


}
