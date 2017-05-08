package org.msh.pharmadex.domain.amendment;

import javax.persistence.*;
/**
 * Specific Amendment for Applicant's data
 * @author Alex Kurasoff
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ApplAmd")
public class ApplicantAmd extends Amendment {
	private static final long serialVersionUID = 5487516649666856487L;
	
	@Column(name="`ApplName`", nullable=true, length=255)	
	private String applName;
	
	@Column(name="`LicNo`", nullable=true, length=255)	
	private String licNo;
	
	public void setApplName(String value) {
		this.applName = value;
	}
	
	public String getApplName() {
		return applName;
	}
	
	public void setLicNo(String value) {
		this.licNo = value;
	}
	
	public String getLicNo() {
		return licNo;
	}
	
	public String toString() {
		return super.toString();
	}
}
