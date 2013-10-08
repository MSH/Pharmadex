package org.msh.pharmadex.domain.enums;

public enum UserState {
	ACTIVE,
	BLOCKED;
	
	public String getKey() {
		return getClass().getSimpleName().concat("." + name());
	}	

}
