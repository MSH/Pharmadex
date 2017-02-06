package org.msh.pharmadex.domain.enums;

import java.util.ArrayList;
import java.util.List;

public enum UserType {
    STAFF,
    COMPANY;
    //INSPECTOR,
    //TIPC,
    //EXTERNAL;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
    
    public List<UserRole> getRolesList(){
    	List<UserRole> roles = new ArrayList<UserRole>();
    	if(this.equals(STAFF)){
    		roles.add(UserRole.ROLE_STAFF);
    		roles.add(UserRole.ROLE_MODERATOR);
    		roles.add(UserRole.ROLE_REVIEWER);
    		roles.add(UserRole.ROLE_HEAD);
    		roles.add(UserRole.ROLE_ADMIN);
    	}else if(this.equals(COMPANY)){
    		roles.add(UserRole.ROLE_COMPANY);
    	}
    	return roles;
    }
}
