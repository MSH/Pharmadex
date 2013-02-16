package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "inn")
public class Inn extends CreationDetail implements Serializable
{
    private static final long serialVersionUID = -4404602565576338011L;
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true)
	private Long id;

    @Column(nullable = false)
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(unique = true)
//	@NotNull
//	private Long uid;
//
//	@Column(name = "PROLINO")
//	@NotNull
//	private int prolino;
//
//	@Column(name = "RECOLINO")
//	private String recolino;
//	
//	@Column(name = "dbo_INN_SERIAL")
//	@NotNull
//	private int dboInnSerial;
//	
//	@Column(name = "INN")
//	private String inn;
//
//	@Column(name = "INNE")
//	private String inne;
//
//	@Column(name = "INNCH")
//	private String innch;
//
//	@Column(name = "INNF")
//	private String innf;
//
//	@Column(name = "INNR")
//	private String innr;
//
//	@Column(name = "INNS")
//	private String inns;
//
//	@Column(name = "INNA")
//	private String inna;
//
//	@Column(name = "CAS")
//	private String cas;
//
//	@Column(name = "MOLFORM")
//	private String molform;
//
//	@Column(name = "ATC_CODE")
//	private String atcCode;
//
//	public Long getUid() {
//		return uid;
//	}
//
//	public void setUid(Long uid) {
//		this.uid = uid;
//	}
//
//	public int getProlino() {
//		return prolino;
//	}
//
//	public void setProlino(int prolino) {
//		this.prolino = prolino;
//	}
//
//	public String getRecolino() {
//		return recolino;
//	}
//
//	public void setRecolino(String recolino) {
//		this.recolino = recolino;
//	}
//
//	public int getDboInnSerial() {
//		return dboInnSerial;
//	}
//
//	public void setDboInnSerial(int dboInnSerial) {
//		this.dboInnSerial = dboInnSerial;
//	}
//
//	public String getInn() {
//		return inn;
//	}
//
//	public void setInn(String inn) {
//		this.inn = inn;
//	}
//
//	public String getInne() {
//		return inne;
//	}
//
//	public void setInne(String inne) {
//		this.inne = inne;
//	}
//
//	public String getInnch() {
//		return innch;
//	}
//
//	public void setInnch(String innch) {
//		this.innch = innch;
//	}
//
//	public String getInnf() {
//		return innf;
//	}
//
//	public void setInnf(String innf) {
//		this.innf = innf;
//	}
//
//	public String getInnr() {
//		return innr;
//	}
//
//	public void setInnr(String innr) {
//		this.innr = innr;
//	}
//
//	public String getInns() {
//		return inns;
//	}
//
//	public void setInns(String inns) {
//		this.inns = inns;
//	}
//
//	public String getInna() {
//		return inna;
//	}
//
//	public void setInna(String inna) {
//		this.inna = inna;
//	}
//
//	public String getCas() {
//		return cas;
//	}
//
//	public void setCas(String cas) {
//		this.cas = cas;
//	}
//
//	public String getMolform() {
//		return molform;
//	}
//
//	public void setMolform(String molform) {
//		this.molform = molform;
//	}
//
//	public String getAtcCode() {
//		return atcCode;
//	}
//
//	public void setAtcCode(String atcCode) {
//		this.atcCode = atcCode;
//	}
//
//	@Override
//	public String toString(){
//		return inne;
//		
//	}
//
//	
}
