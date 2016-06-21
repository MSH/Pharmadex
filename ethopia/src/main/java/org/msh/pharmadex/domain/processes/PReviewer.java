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
@Table(name="PReviewer")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PReviewer extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PReviewer() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User executor;
	
	@Column(name="Role", nullable=false, length=11)	
	private int role;
	
	/**
	 * Must be ordinal of Enum - First or Second reviewer
	 */
	public void setRole(int value) {
		this.role = value;
	}
	
	/**
	 * Must be ordinal of Enum - First or Second reviewer
	 */
	public int getRole() {
		return role;
	}
	
	public void setExecutor(org.msh.pharmadex.domain.User value) {
		this.executor = value;
	}
	
	public org.msh.pharmadex.domain.User getExecutor() {
		return executor;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
