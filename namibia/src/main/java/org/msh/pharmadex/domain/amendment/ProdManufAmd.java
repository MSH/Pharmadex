package org.msh.pharmadex.domain.amendment;
import javax.persistence.*;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.enums.CompanyType;
/**
 * Specific Amendmaent to change Product Manufacturer (add new one or replace old one)
 * @author Alex Kurasoff
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ProdManufAmd")
public class ProdManufAmd extends Amendment {
	private static final long serialVersionUID = -861598542395272552L;
	
	@OneToOne(targetEntity=Company.class, fetch=FetchType.LAZY)	
	@JoinColumn(name="`CompanyID`", nullable = true)	
	private Company existedCompany;
	
	@Enumerated(EnumType.STRING)
    private CompanyType companyType;
	
	@Column(name="`CompName`", nullable=true, length=255)	
	private String compName;
	
	@Column(name="`ConatactName`", nullable=true, length=255)	
	private String conatactName;
	
	@Column(name="`GmpInsp`", nullable=true, length=1)	
	private Boolean gmpInsp;
	
	@Column(name="`SiteNumber`", nullable=true, length=255)	
	private String siteNumber;
	
	@Column(name="`GmpInspDate`", nullable=true)	
	private java.util.Date gmpInspDate;
	
	@Column(name="`GmpCertNo`", nullable=true, length=255)	
	private String gmpCertNo;
	
	public void setCompanyType(CompanyType value) {
		this.companyType = value;
	}
	
	public CompanyType getCompanyType() {
		return companyType;
	}
	
	public void setCompName(String value) {
		this.compName = value;
	}
	
	public String getCompName() {
		return compName;
	}
	
	public void setConatactName(String value) {
		this.conatactName = value;
	}
	
	public String getConatactName() {
		return conatactName;
	}
	
	public void setGmpInsp(boolean value) {
		setGmpInsp(new Boolean(value));
	}
	
	public void setGmpInsp(Boolean value) {
		this.gmpInsp = value;
	}
	
	public Boolean getGmpInsp() {
		return gmpInsp;
	}
	
	public void setSiteNumber(String value) {
		this.siteNumber = value;
	}
	
	public String getSiteNumber() {
		return siteNumber;
	}
	
	public void setGmpInspDate(java.util.Date value) {
		this.gmpInspDate = value;
	}
	
	public java.util.Date getGmpInspDate() {
		return gmpInspDate;
	}
	
	public void setGmpCertNo(String value) {
		this.gmpCertNo = value;
	}
	
	public String getGmpCertNo() {
		return gmpCertNo;
	}
	
	public void setExistedCompany(Company value) {
		this.existedCompany = value;
	}
	
	public Company getExistedCompany() {
		return existedCompany;
	}
	
}
