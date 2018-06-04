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
@Table(name="PReview")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PReview extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PReview() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User moderator;
	
	@Column(name="Summary", nullable=false, length=11)	
	private int summary;
	
	@Column(name="AssignDate", nullable=true)	
	private java.sql.Date assignDate;
	
	@Column(name="ReviewDate", nullable=true)	
	private java.sql.Date reviewDate;
	
	@Column(name="ModeratorSummary", nullable=true)	
	private String moderatorSummary;
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PReviewer.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="PReviewID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set reviewers = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PReviewItem.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="PReviewID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set items = new java.util.HashSet();
	
	/**
	 * Should be ordinal enum Summary of review Manages review cycles
	 */
	public void setSummary(int value) {
		this.summary = value;
	}
	
	/**
	 * Should be ordinal enum Summary of review Manages review cycles
	 */
	public int getSummary() {
		return summary;
	}
	
	public void setAssignDate(java.sql.Date value) {
		this.assignDate = value;
	}
	
	public java.sql.Date getAssignDate() {
		return assignDate;
	}
	
	/**
	 * Review finish date
	 */
	public void setReviewDate(java.sql.Date value) {
		this.reviewDate = value;
	}
	
	/**
	 * Review finish date
	 */
	public java.sql.Date getReviewDate() {
		return reviewDate;
	}
	
	public void setModeratorSummary(String value) {
		this.moderatorSummary = value;
	}
	
	public String getModeratorSummary() {
		return moderatorSummary;
	}
	
	public void setReviewers(java.util.Set value) {
		this.reviewers = value;
	}
	
	public java.util.Set getReviewers() {
		return reviewers;
	}
	
	
	public void setModerator(org.msh.pharmadex.domain.User value) {
		this.moderator = value;
	}
	
	public org.msh.pharmadex.domain.User getModerator() {
		return moderator;
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
