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
/**
 * Sample may be consist of several items
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="PSampleItems")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PSampleItems extends org.msh.pharmadex.domain.processes.PBase implements Serializable {
	public PSampleItems() {
	}
	
	@Column(name="Item", nullable=false, length=11)	
	private int item;
	
	/**
	 * Ordinal of enum
	 */
	public void setItem(int value) {
		this.item = value;
	}
	
	/**
	 * Ordinal of enum
	 */
	public int getItem() {
		return item;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
