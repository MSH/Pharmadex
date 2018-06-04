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
@Table(name="PComment")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("PComment")
public class PComment extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PComment() {
	}
	
	@Column(name="Comment", nullable=true)	
	private String comment;
	
	@Column(name="Internal", nullable=false, length=1)	
	private boolean internal;
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setInternal(boolean value) {
		this.internal = value;
	}
	
	public boolean getInternal() {
		return internal;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
