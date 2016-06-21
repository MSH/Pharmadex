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
@Table(name="PLetter")
@Inheritance(strategy=InheritanceType.JOINED)
public class PLetter extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PLetter() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User creator;
	
	@Column(name="LetterDate", nullable=true)	
	private java.sql.Date letterDate;
	
	@Column(name="LetterType", nullable=false, length=11)	
	private int letterType;
	
	@Column(name="LetterNumber", nullable=true, length=100)	
	private String letterNumber;
	
	@Column(name="Sent", nullable=false, length=1)	
	private boolean sent;
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.LetterFile.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="PLetterID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set files = new java.util.HashSet();
	
	public void setLetterDate(java.sql.Date value) {
		this.letterDate = value;
	}
	
	public java.sql.Date getLetterDate() {
		return letterDate;
	}
	
	public void setLetterType(int value) {
		this.letterType = value;
	}
	
	public int getLetterType() {
		return letterType;
	}
	
	public void setLetterNumber(String value) {
		this.letterNumber = value;
	}
	
	public String getLetterNumber() {
		return letterNumber;
	}
	
	public void setSent(boolean value) {
		this.sent = value;
	}
	
	public boolean getSent() {
		return sent;
	}
	
	public void setCreator(org.msh.pharmadex.domain.User value) {
		this.creator = value;
	}
	
	public org.msh.pharmadex.domain.User getCreator() {
		return creator;
	}
	
	public void setFiles(java.util.Set value) {
		this.files = value;
	}
	
	public java.util.Set getFiles() {
		return files;
	}
	
	
	public String toString() {
		return super.toString();
	}
	
}
