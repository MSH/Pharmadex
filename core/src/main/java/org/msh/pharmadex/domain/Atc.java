package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "atc")
public class Atc extends CreationDetail implements Serializable
{
    @Id
    @Column(nullable = false, unique = true)
   	private String atcCode;

    @Column(length = 500, nullable = false)
    private String atcName;

   	@ManyToOne
   	@JoinColumn(name="PARENT_ID")
   	private Atc parent;

   	@OneToMany(mappedBy="parent",fetch=FetchType.LAZY)
   	private List<Atc> units = new ArrayList<Atc>();

    @Column(nullable=false)
   	private int level;

   	@Column(length=50)
   	private String legacyId;

    @ManyToMany(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinTable(name = "prod_atc")
    private List<Product> products;




   	// properties to help dealing with trees
//   	private int unitsCount;

   	/**
   	 * Return the parent list including the own object
   	 * @return List of {@link Atc} instance
   	 */
   	public List<Atc> getParents() {
   		return getParentsTreeList(true);
   	}


   	/**
   	 * Return the display name of the Atc concatenated with its parent
   	 * @return
   	 */
   	public String getFullDisplayName() {
   		String s = getAtcName().toString();

   		for (Atc adm: getParentsTreeList(false)) {
   			s += ", " + adm.getAtcName().toString();
   		}

   		return s;
   	}

   	/**
   	 * Return a list with parents atc, where the first is the upper level ATC and
   	 * the last the lowest level
   	 * @return {@link List} of {@link Atc} instances
   	 */
   	public List<Atc> getParentsTreeList(boolean includeThis) {
   		ArrayList<Atc> lst = new ArrayList<Atc>();

   		Atc aux;
   		if (includeThis)
   			 aux = this;
   		else aux = getParent();

   		while (aux != null) {
   			lst.add(0, aux);
   			aux = aux.getParent();
   		}
   		return lst;
   	}


   	/**
   	 * Static method that return the parent code of a given code
   	 * @param code
   	 * @return
   	 */
   	public static String getParentCode(String code) {
   		if ((code == null) || (code.length() <= 3))
   			return null;

   		if (code.length() <= 6)
   			return code.substring(0, 3);
   		if (code.length() <= 9)
   			return code.substring(0, 6);
   		if (code.length() <= 12)
   			return code.substring(0, 9);
   		return code.substring(0, 12);
   	}

   	/**
   	 * Check if an atc code (passed as the code parameter) is a child of the current atc
   	 * @param code of the unit
   	 * @return true if code is of a child unit, otherwise return false
   	 */
//   	public boolean isSameOrChildCode(String code) {
//   		return isSameOrChildCode(this.level, code);
   /*		int len = this.code.length();
   		if (len > code.length())
   			return false;
   		return (this.code.equals(code.substring(0, this.code.length())));
    	}
   */


   	/**
   	 * Static method to check if a code is equals of a child of the code given by the parentCode param
   	 * @param parentCode
   	 * @param code
   	 * @return
   	 */
   	public static boolean isSameOrChildCode(String parentCode, String code) {
   		int len = parentCode.length();
   		if (len > code.length())
   			return false;
   		return (parentCode.equals(code.substring(0, parentCode.length())));
   	}


   	/**
   	 * Return the parent atc based on its level. If level is the same of this unit, it returns itself.
   	 * If level is bigger than the level of this unit, it returns null
   	 * @param level
   	 * @return {@link Atc} instance, which is itself or a parent admin unit
   	 */
   	public Atc getAdminUnitByLevel(int level) {
   		List<Atc> lst = getParents();
   		for (Atc adm: lst) {
   			if (adm.getLevel()== level)
   				return adm;
   		}
   		return null;
   	}


   	/**
   	 * Return parent administrative unit of level 1
   	 * @return {@link Atc} instance
   	 */
   	public Atc getParentLevel1() {
   		return getAdminUnitByLevel(1);
   	}


   	/**
   	 * Return parent administrative unit of level 2
   	 * @return {@link Atc} instance
   	 */
   	public Atc getParentLevel2() {
   		return getAdminUnitByLevel(2);
   	}


   	/**
   	 * Return parent administrative unit of level 3
   	 * @return {@link Atc} instance
   	 */
   	public Atc getParentLevel3() {
   		return getAdminUnitByLevel(3);
   	}


   	/**
   	 * Return parent administrative unit of level 4
   	 * @return {@link Atc} instance
   	 */
   	public Atc getParentLevel4() {
   		return getAdminUnitByLevel(4);
   	}


   	/**
   	 * Return parent administrative unit of level 5
   	 * @return {@link Atc} instance
   	 */
   	public Atc getParentLevel5() {
   		return getAdminUnitByLevel(5);
   	}

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public String getAtcName() {
        return atcName;
    }

    public void setAtcName(String atcName) {
        this.atcName = atcName;
    }

    public Atc getParent() {
        return parent;
    }

    public void setParent(Atc parent) {
        this.parent = parent;
    }

    public List<Atc> getUnits() {
        return units;
    }

    public void setUnits(List<Atc> units) {
        this.units = units;
    }

    /**
   	 * @return the legacyId
   	 */
   	public String getLegacyId() {
   		return legacyId;
   	}

   	/**
   	 * @param legacyCode the legacyId to set
   	 */
   	public void setLegacyId(String legacyCode) {
   		this.legacyId = legacyCode;
   	}

   	/* (non-Javadoc)
   	 * @see java.lang.Object#toString()
   	 */
   	@Override
   	public String toString() {
   		return getFullDisplayName();
   	}

   	/**
   	 * @return the unitsCount
   	 */
//   	public int getUnitsCount() {
//   		return unitsCount;
//   	}
//
//   	/**
//   	 * @param unitsCount the unitsCount to set
//   	 */
//   	public void setUnitsCount(int unitsCount) {
//   		this.unitsCount = unitsCount;
//   	}

   	/* (non-Javadoc)
   	 * @see java.lang.Object#equals(java.lang.Object)
   	 */
   	@Override
   	public boolean equals(Object obj) {
   		if (obj == this)
   			return true;

   		if (!(obj instanceof Atc))
   			return false;

   		return ((Atc)obj).getAtcCode().equals(getAtcCode());
   	}

//   	/**
//   	 * @param code the code to set
//   	 */
//   	public void setLevel(String code) {
//   		this.level = code;
//   	}
//
//   	/**
//   	 * @return the code
//   	 */
//   	public String getLevel() {
//   		return level;
//   	}

//   	public int getLevel() {
//   		String s = getLevel();
//   		if (s == null)
//   			return 0;
//
//   		return s.length()/3;
//   	}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
