package org.msh.pharmadex.domain.enums;

public enum PayType {
	EFT,
	CACHE;
	
	public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
