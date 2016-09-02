package org.msh.pharmadex.domain.enums;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 5/13/14
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ProdAppType {

    GENERIC,
    NEW_CHEMICAL_ENTITY,
    RENEW,
    VARIATION;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
