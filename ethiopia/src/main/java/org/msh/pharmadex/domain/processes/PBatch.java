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
@Table(name="PBatch")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PBatch extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PBatch() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User creator;
	
	@Column(name="BatchNo", nullable=true, length=255)	
	private String batchNo;
	
	@Column(name="Comment", nullable=true, length=255)	
	private String comment;
	
	@Column(name="Exp_date", nullable=true)	
	private java.util.Date exp_date;
	
	@Column(name="Manuf_date", nullable=true)	
	private java.util.Date manuf_date;
	
	@Column(name="Manuf_name", nullable=true, length=255)	
	private String manuf_name;
	
	@Column(name="Quantity", nullable=true, length=11)	
	private int quantity;
	
	public void setBatchNo(String value) {
		this.batchNo = value;
	}
	
	public String getBatchNo() {
		return batchNo;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setExp_date(java.util.Date value) {
		this.exp_date = value;
	}
	
	public java.util.Date getExp_date() {
		return exp_date;
	}
	
	public void setManuf_date(java.util.Date value) {
		this.manuf_date = value;
	}
	
	public java.util.Date getManuf_date() {
		return manuf_date;
	}
	
	public void setManuf_name(String value) {
		this.manuf_name = value;
	}
	
	public String getManuf_name() {
		return manuf_name;
	}
	
	public void setQuantity(int value) {
		this.quantity = value;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setCreator(org.msh.pharmadex.domain.User value) {
		this.creator = value;
	}
	
	public org.msh.pharmadex.domain.User getCreator() {
		return creator;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
