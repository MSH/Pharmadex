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
@Table(name="PTimeLine")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PTimeLine extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PTimeLine() {
	}
	
	@ManyToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId", referencedColumnName="userId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User user;
	
	@Column(name="RegStatus", nullable=false, length=11)	
	private int regStatus;
	
	@Column(name="StatusDate", nullable=true)	
	private java.sql.Date statusDate;
	
	@Column(name="Comment", nullable=true)	
	private String comment;
	
	/**
	 * Should be ordinal of known Enum
	 */
	public void setRegStatus(int value) {
		this.regStatus = value;
	}
	
	/**
	 * Should be ordinal of known Enum
	 */
	public int getRegStatus() {
		return regStatus;
	}
	
	public void setStatusDate(java.sql.Date value) {
		this.statusDate = value;
	}
	
	public java.sql.Date getStatusDate() {
		return statusDate;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setUser(org.msh.pharmadex.domain.User value) {
		this.user = value;
	}
	
	public org.msh.pharmadex.domain.User getUser() {
		return user;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
