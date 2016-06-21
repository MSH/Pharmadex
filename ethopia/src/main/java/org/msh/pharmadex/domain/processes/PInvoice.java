/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: DuKe TeAm
 * License Type: Purchased
 */
package org.msh.pharmadex.domain.processes;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="PInvoice")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PInvoice extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PInvoice() {
	}
	
	@Column(name="IssueDate", nullable=true)	
	private java.util.Date issueDate;
	
	@Column(name="Number", nullable=true, length=255)	
	private String number;
	
	@Column(name="BankName", nullable=true, length=255)	
	private String bankName;
	
	@Column(name="Sum", nullable=false, length=11)	
	private int sum;
	
	@Column(name="Purpose", nullable=false, length=11)	
	private int purpose;
	
	@Column(name="CloseDate", nullable=true)	
	private java.util.Date closeDate;
	
	@Column(name="RecieptNo", nullable=true, length=255)	
	private String recieptNo;
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.InvoiceFile.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="PInvoiceID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set attachments = new java.util.HashSet();
	
	public void setIssueDate(java.util.Date value) {
		this.issueDate = value;
	}
	
	public java.util.Date getIssueDate() {
		return issueDate;
	}
	
	public void setNumber(String value) {
		this.number = value;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setBankName(String value) {
		this.bankName = value;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	/**
	 * Sum in centes
	 */
	public void setSum(int value) {
		this.sum = value;
	}
	
	/**
	 * Sum in centes
	 */
	public int getSum() {
		return sum;
	}
	
	/**
	 * Ordinal value of some Enum. Purpose of payment
	 */
	public void setPurpose(int value) {
		this.purpose = value;
	}
	
	/**
	 * Ordinal value of some Enum. Purpose of payment
	 */
	public int getPurpose() {
		return purpose;
	}
	
	public void setCloseDate(java.util.Date value) {
		this.closeDate = value;
	}
	
	public java.util.Date getCloseDate() {
		return closeDate;
	}
	
	public void setRecieptNo(String value) {
		this.recieptNo = value;
	}
	
	public String getRecieptNo() {
		return recieptNo;
	}
	
	public void setAttachments(java.util.Set value) {
		this.attachments = value;
	}
	
	public java.util.Set getAttachments() {
		return attachments;
	}
	
	
	public String toString() {
		return super.toString();
	}
	
}
