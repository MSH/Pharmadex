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
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("InvoiceFile")
public class InvoiceFile extends org.msh.pharmadex.domain.processes.PFile implements Serializable {
	public InvoiceFile() {
	}
	
	@Column(name="Invoice", nullable=true, length=1)	
	private boolean invoice;
	
	/**
	 * If true - invoice, otherwise receipt or something else
	 */
	public void setInvoice(boolean value) {
		this.invoice = value;
	}
	
	/**
	 * If true - invoice, otherwise receipt or something else
	 */
	public boolean getInvoice() {
		return invoice;
	}
	
	public String toString() {
		return super.toString();
	}
	
}
