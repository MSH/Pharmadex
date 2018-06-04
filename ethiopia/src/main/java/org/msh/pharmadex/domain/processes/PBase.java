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
@MappedSuperclass
public class PBase implements Serializable {
	public PBase() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="V0A26FA1B1555D4EBDB10D177")	
	@org.hibernate.annotations.GenericGenerator(name="V0A26FA1B1555D4EBDB10D177", strategy="native")	
	private long ID;
	
	@Column(name="Created", nullable=true)	
	private java.util.Date created;
	
	@Column(name="Updated", nullable=true)	
	private java.util.Date updated;
	
	@Column(name="DueDate", nullable=true)	
	private java.sql.Date dueDate;
	
	@Version	
	@Column(name="Version", nullable=false, length=11)	
	private int version;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setCreated(java.util.Date value) {
		this.created = value;
	}
	
	public java.util.Date getCreated() {
		return created;
	}
	
	public void setUpdated(java.util.Date value) {
		this.updated = value;
	}
	
	public java.util.Date getUpdated() {
		return updated;
	}
	
	/**
	 * Very often it is necessary to have projected due date
	 */
	public void setDueDate(java.sql.Date value) {
		this.dueDate = value;
	}
	
	/**
	 * Very often it is necessary to have projected due date
	 */
	public java.sql.Date getDueDate() {
		return dueDate;
	}
	
	private void setVersion(int value) {
		this.version = value;
	}
	
	public int getVersion() {
		return version;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
