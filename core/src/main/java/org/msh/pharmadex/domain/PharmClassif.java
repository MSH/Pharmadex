package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class PharmClassif extends CreationDetail implements Serializable
{


    private static final long serialVersionUID = -8621482999656256928L;
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true)
    private int id;
    
	@Column(name = "CatSTY")
	private int pharmaCat;
	
	@Column(name = "No")
    private String no;
    
	@Column(name = "PharmaClass")
    private String name;
	
	@Column(name = "Discontinued")
    private String discontinued;
	
	@Transient
	private String displayName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPharmaCat() {
		return pharmaCat;
	}

	public void setPharmaCat(int pharmaCat) {
		this.pharmaCat = pharmaCat;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(String discontinued) {
		this.discontinued = discontinued;
	}

	public String getDisplayName() {
		
		return no+" "+name;
	}

    @Override
    public String toString() {
        return getDisplayName();
    }

	

}
