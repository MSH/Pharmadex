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
@Table(name="PSample")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class PSample extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PSample() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.processes.PBatch.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="PBatchID") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.processes.PBatch batch;
	
	@Column(name="Status", nullable=false, length=11)	
	private int status;
	
	@Column(name="LetterSentDate", nullable=true)	
	private java.sql.Date letterSentDate;
	
	@Column(name="SampleRecDate", nullable=true)	
	private java.sql.Date sampleRecDate;
	
	@Column(name="ResultDate", nullable=true)	
	private java.sql.Date resultDate;
	
	@Column(name="ResultSubmitDate", nullable=true)	
	private java.sql.Date resultSubmitDate;
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PSampleItems.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="PSampleID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set items = new java.util.HashSet();
	
	/**
	 * Should be converted to Enum
	 */
	public void setStatus(int value) {
		this.status = value;
	}
	
	/**
	 * Should be converted to Enum
	 */
	public int getStatus() {
		return status;
	}
	
	public void setLetterSentDate(java.sql.Date value) {
		this.letterSentDate = value;
	}
	
	public java.sql.Date getLetterSentDate() {
		return letterSentDate;
	}
	
	public void setSampleRecDate(java.sql.Date value) {
		this.sampleRecDate = value;
	}
	
	public java.sql.Date getSampleRecDate() {
		return sampleRecDate;
	}
	
	public void setResultDate(java.sql.Date value) {
		this.resultDate = value;
	}
	
	public java.sql.Date getResultDate() {
		return resultDate;
	}
	
	public void setResultSubmitDate(java.sql.Date value) {
		this.resultSubmitDate = value;
	}
	
	public java.sql.Date getResultSubmitDate() {
		return resultSubmitDate;
	}
	
	public void setBatch(org.msh.pharmadex.domain.processes.PBatch value) {
		this.batch = value;
	}
	
	public org.msh.pharmadex.domain.processes.PBatch getBatch() {
		return batch;
	}
	
	public void setItems(java.util.Set value) {
		this.items = value;
	}
	
	public java.util.Set getItems() {
		return items;
	}
	
	
	public String toString() {
		return super.toString();
	}
	
}
