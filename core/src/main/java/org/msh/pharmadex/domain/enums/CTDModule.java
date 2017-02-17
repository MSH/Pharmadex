package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum CTDModule {

    MODULE_1,
    MODULE_2,
    MODULE_3,
    MODULE_4,
    ALL;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
    
    public static CTDModule valueOfList(String val){
    	if(val != null && val.length() > 0){
    		for(CTDModule m : values()){
    			if(m.name().equals(val))
    				return m;
    		}
    	}
    	return ALL;
    }
}
