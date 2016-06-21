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
@Table(name="Process")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Process")
public class Process extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public Process() {
	}
	
	@ManyToOne(targetEntity=org.msh.pharmadex.domain.ProdApplications.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="prodapplicationsid", referencedColumnName="id") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.ProdApplications prodApplications;
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId2") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User lastModifiedBy;
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User creator;
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.processes.PReview.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="PReviewID") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.processes.PReview reviews;
	
	@Column(name="SubmitDate", nullable=true)	
	private java.sql.Date submitDate;
	
	@Column(name="ResultDate", nullable=true)	
	private java.sql.Date resultDate;
	
	@Column(name="DosRecDate", nullable=true)	
	private java.sql.Date dosRecDate;
	
	@Column(name="DossLoc", nullable=true, length=255)	
	private String dossLoc;
	
	@Column(name="ExecSummary", nullable=true)	
	private String execSummary;
	
	@Column(name="ApplicantVerified", nullable=false, length=1)	
	private boolean applicantVerified;
	
	@Column(name="ProductVerified", nullable=false, length=1)	
	private boolean productVerified;
	
	@Column(name="ClinicalVerified", nullable=false, length=1)	
	private boolean clinicalVerified;
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PInvoice.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set invoices = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PSample.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set samples = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.ProcessFile.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set files = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PScreenItem.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set screening = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PTimeLine.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set timeline = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.PLetter.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set letters = new java.util.HashSet();
	
	@OneToMany(targetEntity=org.msh.pharmadex.domain.processes.ProcessComment.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ProcessID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set comments = new java.util.HashSet();
	
	public void setSubmitDate(java.sql.Date value) {
		this.submitDate = value;
	}
	
	public java.sql.Date getSubmitDate() {
		return submitDate;
	}
	
	public void setResultDate(java.sql.Date value) {
		this.resultDate = value;
	}
	
	public java.sql.Date getResultDate() {
		return resultDate;
	}
	
	public void setDosRecDate(java.sql.Date value) {
		this.dosRecDate = value;
	}
	
	public java.sql.Date getDosRecDate() {
		return dosRecDate;
	}
	
	/**
	 * Dossier location
	 */
	public void setDossLoc(String value) {
		this.dossLoc = value;
	}
	
	/**
	 * Dossier location
	 */
	public String getDossLoc() {
		return dossLoc;
	}
	
	/**
	 * Executive summary
	 */
	public void setExecSummary(String value) {
		this.execSummary = value;
	}
	
	/**
	 * Executive summary
	 */
	public String getExecSummary() {
		return execSummary;
	}
	
	public void setApplicantVerified(boolean value) {
		this.applicantVerified = value;
	}
	
	public boolean getApplicantVerified() {
		return applicantVerified;
	}
	
	public void setProductVerified(boolean value) {
		this.productVerified = value;
	}
	
	public boolean getProductVerified() {
		return productVerified;
	}
	
	public void setClinicalVerified(boolean value) {
		this.clinicalVerified = value;
	}
	
	public boolean getClinicalVerified() {
		return clinicalVerified;
	}
	
	public void setInvoices(java.util.Set value) {
		this.invoices = value;
	}
	
	public java.util.Set getInvoices() {
		return invoices;
	}
	
	
	public void setReviews(org.msh.pharmadex.domain.processes.PReview value) {
		this.reviews = value;
	}
	
	public org.msh.pharmadex.domain.processes.PReview getReviews() {
		return reviews;
	}
	
	public void setSamples(java.util.Set value) {
		this.samples = value;
	}
	
	public java.util.Set getSamples() {
		return samples;
	}
	
	
	public void setCreator(org.msh.pharmadex.domain.User value) {
		this.creator = value;
	}
	
	public org.msh.pharmadex.domain.User getCreator() {
		return creator;
	}
	
	public void setLastModifiedBy(org.msh.pharmadex.domain.User value) {
		this.lastModifiedBy = value;
	}
	
	public org.msh.pharmadex.domain.User getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setFiles(java.util.Set value) {
		this.files = value;
	}
	
	public java.util.Set getFiles() {
		return files;
	}
	
	
	public void setScreening(java.util.Set value) {
		this.screening = value;
	}
	
	public java.util.Set getScreening() {
		return screening;
	}
	
	
	public void setTimeline(java.util.Set value) {
		this.timeline = value;
	}
	
	public java.util.Set getTimeline() {
		return timeline;
	}
	
	
	public void setLetters(java.util.Set value) {
		this.letters = value;
	}
	
	public java.util.Set getLetters() {
		return letters;
	}
	
	
	public void setComments(java.util.Set value) {
		this.comments = value;
	}
	
	public java.util.Set getComments() {
		return comments;
	}
	
	
	public void setProdApplications(org.msh.pharmadex.domain.ProdApplications value) {
		this.prodApplications = value;
	}
	
	public org.msh.pharmadex.domain.ProdApplications getProdApplications() {
		return prodApplications;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
