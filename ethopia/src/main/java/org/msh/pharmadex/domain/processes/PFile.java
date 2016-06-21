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
@Table(name="PFile")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("PFile")
public class PFile extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PFile() {
	}
	
	@OneToOne(targetEntity=org.msh.pharmadex.domain.User.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="useruserId") })	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pharmadex.domain.User creator;
	
	@Column(name="FileName", nullable=true, length=100)	
	private String fileName;
	
	@Column(name="ContentType", nullable=true, length=50)	
	private String contentType;
	
	@Column(name="Title", nullable=true, length=255)	
	private String title;
	
	@Column(name="Description", nullable=true, length=255)	
	private String description;
	
	@Column(name="FileVersion", nullable=false, length=11)	
	private int fileVersion;
	
	public void setFileName(String value) {
		this.fileName = value;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setContentType(String value) {
		this.contentType = value;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setTitle(String value) {
		this.title = value;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * It will be nice to have
	 */
	public void setFileVersion(int value) {
		this.fileVersion = value;
	}
	
	/**
	 * It will be nice to have
	 */
	public int getFileVersion() {
		return fileVersion;
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
