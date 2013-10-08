package org.msh.pharmadex.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="countrystructure")
public class CountryStructure extends CreationDetail implements Serializable{

    private static final long serialVersionUID = -8897228462959061506L;

    @Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
	@Embedded
	private LocalizedNameComp name = new LocalizedNameComp();
	
	@Column(name="STRUCTURE_LEVEL")
	private int level;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
	 * @return the name
	 */
	public LocalizedNameComp getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(LocalizedNameComp name) {
		this.name = name;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (name != null? name.toString() : super.toString());
	}
}
