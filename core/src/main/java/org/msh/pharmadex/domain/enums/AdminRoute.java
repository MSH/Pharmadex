package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum AdminRoute {
    ORAL,
    INJECTABLE,
    INHALATION,
    TOPICAL;


    public String getKey() {
   		return getClass().getSimpleName().concat("." + name());
   	}

}
