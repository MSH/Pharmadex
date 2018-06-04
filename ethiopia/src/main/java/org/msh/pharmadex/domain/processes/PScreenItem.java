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
@Table(name="PScreenItem")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PScreenItem extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PScreenItem() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User screener;
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.processes.PScreenItemFile.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="PFileID") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.processes.PScreenItemFile attachment;
	
	@Column(name="ModuleNo", nullable=true, length=255)	
	private String moduleNo;
	
	@Column(name="ModuleName", nullable=true, length=255)	
	private String moduleName;
	
	@Column(name="ItemName", nullable=true, length=255)	
	private String itemName;
	
	@Column(name="StaffComment", nullable=true)	
	private String staffComment;
	
	@Column(name="ApplicantComment", nullable=true)	
	private String applicantComment;
	
	@Column(name="Answer", nullable=false, length=11)	
	private int answer;
	
	public void setModuleNo(String value) {
		this.moduleNo = value;
	}
	
	public String getModuleNo() {
		return moduleNo;
	}
	
	public void setModuleName(String value) {
		this.moduleName = value;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public void setItemName(String value) {
		this.itemName = value;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setStaffComment(String value) {
		this.staffComment = value;
	}
	
	public String getStaffComment() {
		return staffComment;
	}
	
	public void setApplicantComment(String value) {
		this.applicantComment = value;
	}
	
	public String getApplicantComment() {
		return applicantComment;
	}
	
	/**
	 * Must be ordinal for some enum - Yes, No, Not applicable
	 */
	public void setAnswer(int value) {
		this.answer = value;
	}
	
	/**
	 * Must be ordinal for some enum - Yes, No, Not applicable
	 */
	public int getAnswer() {
		return answer;
	}
	
	public void setScreener(org.msh.pharmadex.domain.User value) {
		this.screener = value;
	}
	
	public org.msh.pharmadex.domain.User getScreener() {
		return screener;
	}
	
	public void setAttachment(org.msh.pharmadex.domain.processes.PScreenItemFile value) {
		this.attachment = value;
	}
	
	public org.msh.pharmadex.domain.processes.PScreenItemFile getAttachment() {
		return attachment;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
