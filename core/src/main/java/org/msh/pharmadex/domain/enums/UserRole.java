package org.msh.pharmadex.domain.enums;

public enum UserRole {
	ROLE_EXT,
	ROLE_ADMIN,
	ROLE_STAFF, // in Ethiopia - Customer Service Officer
	ROLE_COMPANY,
	ROLE_INSPECTOR,
	ROLE_MODERATOR,
	ROLE_REVIEWER,
	ROLE_HEAD,
	ROLE_CSD,
	ROLE_LAB,
	ROLE_LAB_MODERATOR,
	ROLE_CLINICAL,
	ROLE_CST,
	ROLE_LAB_HEAD;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
