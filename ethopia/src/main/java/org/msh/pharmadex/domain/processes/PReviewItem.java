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
@Table(name="PReviewItem")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PReviewItem extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PReviewItem() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.processes.PReviewFile.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="PFileID") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.processes.PReviewFile attachment;
	
	@Column(name="VolumePages", nullable=false, length=11)	
	private int volumePages;
	
	@Column(name="Answer", nullable=false, length=11)	
	private int answer;
	
	@Column(name="Module", nullable=true, length=11)	
	private int module;
	
	@Column(name="Question", nullable=true, length=255)	
	private String question;
	
	public void setVolumePages(int value) {
		this.volumePages = value;
	}
	
	public int getVolumePages() {
		return volumePages;
	}
	
	/**
	 * Must be ordinal value for enum Yes, No, Not applicable or something like this
	 */
	public void setAnswer(int value) {
		this.answer = value;
	}
	
	/**
	 * Must be ordinal value for enum Yes, No, Not applicable or something like this
	 */
	public int getAnswer() {
		return answer;
	}
	
	/**
	 * Module belong to. Must be enum with module numbers and ALL
	 */
	public void setModule(int value) {
		this.module = value;
	}
	
	/**
	 * Module belong to. Must be enum with module numbers and ALL
	 */
	public int getModule() {
		return module;
	}
	
	public void setQuestion(String value) {
		this.question = value;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setAttachment(org.msh.pharmadex.domain.processes.PReviewFile value) {
		this.attachment = value;
	}
	
	public org.msh.pharmadex.domain.processes.PReviewFile getAttachment() {
		return attachment;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
