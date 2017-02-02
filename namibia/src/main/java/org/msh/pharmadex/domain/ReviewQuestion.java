package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by utkarsh on 12/3/14.
 */

@Entity
@Cacheable
@Table(name = "review_question")
public class ReviewQuestion extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;
    
    private Integer ord;
    
    private String ctdModule;

    @Column(name = "header1", length = 255)
    private String header1;

    @Column(name = "header2", length = 255)
    private String header2;

    @Column(name = "new_med")
    private boolean newMed;

    @Column(name = "gen_med")
    private boolean genMed;

    @Column(name = "renewal")
    private boolean renewal;

    @Lob
    @Column(name = "question")
    private String question;
    

    private boolean sra;

    @Column(name = "variation")
    private Boolean variation;
	
    @Column(name = "majVariation")
	private Boolean majVariation;


    public Integer getOrd() {
		return ord;
	}

	public void setOrd(Integer ord) {
		this.ord = ord;
	}

	public Boolean isVariation() {
		return variation;
	}

	public void setVariation(Boolean variation) {
		this.variation = variation;
	}

	    public Boolean isMajVariation() {
			return majVariation;
		}

		public void setMajVariation(Boolean variation) {
			this.majVariation = variation;
		}
		
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCtdModule() {
        return ctdModule;
    }

    public void setCtdModule(String ctdModule) {
        this.ctdModule = ctdModule;
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isSra() {
        return sra;
    }

    public void setSra(boolean sra) {
        this.sra = sra;
    }

    public boolean isNewMed() {
        return newMed;
    }

    public void setNewMed(boolean newMed) {
        this.newMed = newMed;
    }

    public boolean isGenMed() {
        return genMed;
    }

    public void setGenMed(boolean genMed) {
        this.genMed = genMed;
    }

    public boolean isRenewal() {
        return renewal;
    }

    public void setRenewal(boolean renewal) {
        this.renewal = renewal;
    }
}
