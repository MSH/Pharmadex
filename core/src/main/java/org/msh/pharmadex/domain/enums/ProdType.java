package org.msh.pharmadex.domain.enums;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 3/3/14
 * Time: 8:15 AM
 * To change this template use File | Settings | File Templates.
 */
public enum ProdType {
    ALLOPATHIC,
    AYURVEDIC,
    HERBAL,
    HOMEOPATHIC,
    UNANI;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
