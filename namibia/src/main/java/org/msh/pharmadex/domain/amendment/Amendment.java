package org.msh.pharmadex.domain.amendment;

import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.CreationDetail;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.User;


import java.io.Serializable;
import javax.persistence.*;
/**
 * General amendment - comments only
 * Suits for common amendments and works as common ancestor for specific amendments
 * @author Alex Kurasoff
 *
 */
@Entity
@Table(name="Amendment")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Amendment")
public class Amendment extends CreationDetail implements Serializable {

	private static final long serialVersionUID = 4906750596828631027L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;
	
	@OneToOne(targetEntity=User.class, fetch=FetchType.LAZY)		
	@JoinColumn(name = "USEREXEC_ID", nullable = true)		
	private User executor;
	
	@OneToOne(targetEntity=ProdApplications.class, fetch=FetchType.LAZY)		
	@JoinColumn(name = "PA_ID", nullable = true)	
	private ProdApplications prodApplications;
	
	@Column(name="`AppComment`", nullable=true, length=4096)	
	private String appComment;
	
	@Column(name="`StaffComment`", nullable=true, length=4096)	
	private String staffComment;
	
	@Column(name="`RegistrarComment`", nullable=true, length=4096)	
	private String registrarComment;
	
	@Column(name="`ApprDate`", nullable=true)	
	private java.util.Date apprDate;
	
	@Column(name="`RejectDate`", nullable=true)	
	private java.util.Date rejectDate;
	
	@Column(name="`EffectiveDate`", nullable=true)	
	private java.util.Date effectiveDate;
	
	@Column(name="`State`", nullable=true, length=50)
	@Enumerated(EnumType.STRING)
	private AmdState state;
	
	public void setAppComment(String value) {
		this.appComment = value;
	}
	
	public String getAppComment() {
		return appComment;
	}
	
	public void setStaffComment(String value) {
		this.staffComment = value;
	}
	
	public String getStaffComment() {
		return staffComment;
	}
	
	public void setRegistrarComment(String value) {
		this.registrarComment = value;
	}
	
	public String getRegistrarComment() {
		return registrarComment;
	}
	
	public void setApprDate(java.util.Date value) {
		this.apprDate = value;
	}
	
	public java.util.Date getApprDate() {
		return apprDate;
	}
	
	public void setRejectDate(java.util.Date value) {
		this.rejectDate = value;
	}
	
	public java.util.Date getRejectDate() {
		return rejectDate;
	}
	
	public void setEffectiveDate(java.util.Date value) {
		this.effectiveDate = value;
	}
	
	public java.util.Date getEffectiveDate() {
		return effectiveDate;
	}
	
	public void setState(AmdState value) {
		this.state = value;
	}
	
	public AmdState getState() {
		return state;
	}
	
	
	public void setProdApplications(ProdApplications value) {
		this.prodApplications = value;
	}
	
	public ProdApplications getProdApplications() {
		return prodApplications;
	}
	
	public void setExecutor(User value) {
		this.executor = value;
	}
	
	public User getExecutor() {
		return executor;
	}
	
	
	@Embedded	
	private Address address;
	
	public Address getAddress()  {
		return this.address;
	}
	
	public void setAddress(Address value)  {
		this.address = value;
	}
	
	@Embedded	
	private Telecom telecom;
	
	public Telecom getTelecom()  {
		return this.telecom;
	}
	
	public void setTelecom(Telecom value)  {
		this.telecom = value;
	}
	
	@Embedded	
	private Payment payment;
	
	public Payment getPayment()  {
		return this.payment;
	}
	
	public void setPayment(Payment value)  {
		this.payment = value;
	}
	
	
	
}
