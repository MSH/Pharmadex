package org.msh.pharmadex.domain.amendment;
import javax.persistence.*;
/**
 * Embedded payment description
 */
@Embeddable
public class Payment {
	@Column(name="`payment_form`", nullable=true, length=255)	
	private String form;
	
	public void setForm(String value) {
		this.form = value;
	}
	
	public String getForm() {
		return form;
	}
	
	@Column(name="`payment_bank`", nullable=true, length=255)	
	private String bank;
	
	public void setBank(String value) {
		this.bank = value;
	}
	
	public String getBank() {
		return bank;
	}
	
	@Column(name="`payment_receipt`", nullable=true, length=255)	
	private String receipt;
	
	public void setReceipt(String value) {
		this.receipt = value;
	}
	
	public String getReceipt() {
		return receipt;
	}
	
	@Column(name="`payment_payDate`", nullable=true, length=255)	
	private String payDate;
	
	public void setPayDate(String value) {
		this.payDate = value;
	}
	
	public String getPayDate() {
		return payDate;
	}
	
	@Column(name="`payment_received`", nullable=false, length=1)	
	private boolean received = false;
	
	public void setReceived(boolean value) {
		this.received = value;
	}
	
	public boolean getReceived() {
		return received;
	}
}
