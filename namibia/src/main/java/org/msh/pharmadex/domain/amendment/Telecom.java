package org.msh.pharmadex.domain.amendment;

import javax.persistence.*;
/**
 * Embedded for telecommunication data
 * For convinience only
 * @author Alex Kurasoff
 *
 */
@Embeddable
public class Telecom {
	public Telecom() {
	}
	
	@Column(name="`telecom_fax`", nullable=true, length=50)	
	private String fax;
	
	public void setFax(String value) {
		this.fax = value;
	}
	
	public String getFax() {
		return fax;
	}
	
	@Column(name="`telecom_eMail`", nullable=true, length=50)	
	private String eMail;
	
	public void seteMail(String value) {
		this.eMail = value;
	}
	
	public String geteMail() {
		return eMail;
	}
	
	@Column(name="`telecom_phone`", nullable=true, length=50)	
	private String phone;
	
	public void setPhone(String value) {
		this.phone = value;
	}
	
	public String getPhone() {
		return phone;
	}
	
	@Column(name="`telecom_web`", nullable=true, length=50)	
	private String web;
	
	public void setWeb(String value) {
		this.web = value;
	}
	
	public String getWeb() {
		return web;
	}


}
