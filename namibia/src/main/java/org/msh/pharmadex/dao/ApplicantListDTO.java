package org.msh.pharmadex.dao;


/**
 * DTO for a record in Applicant list
 * @author Alex Kurasoff
 *
 */
public class ApplicantListDTO {
	private Long applcntId;
	private String name;
	private String appType;
	private String agentName;
	private String contactName;
	private String contactEmail;
	private String contactPhone;
	public Long getApplcntId() {
		return applcntId;
	}
	public void setApplcntId(Long applcntId) {
		this.applcntId = applcntId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	
	
}
