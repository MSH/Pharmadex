package org.msh.pharmadex.domain.enums;

/**
 * Created by utkarsh on 1/20/15.
 */
public enum UseCategory {
	SCHEDULE0, 
	SCHEDULE1, 
	SCHEDULE2, 
	SCHEDULE3,
	SCHEDULE4; 

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
