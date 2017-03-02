package org.msh.pharmadex.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = "agent_applicant")
public class AgentAgreement extends CreationDetail implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5899146104090309990L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="applicant_id", nullable = false)
    private Applicant applicant;
    
    @ManyToOne
    @JoinColumn(name="agent_id", nullable = false)
    private Applicant agent;
    
    private Date start;
    
    private Date finish;
    
    private Boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public Applicant getAgent() {
		return agent;
	}

	public void setAgent(Applicant agent) {
		this.agent = agent;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getFinish() {
		return finish;
	}

	public void setFinish(Date finish) {
		this.finish = finish;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
    

}
