package org.msh.pharmadex.domain.amendment;

import javax.persistence.*;
/**
 * Specific Amendment to change product proprietary name
 * @author Alex Kurasoff
 *
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("PropNameAmd")
public class ProdNameAmd extends Amendment {
	private static final long serialVersionUID = -111863732620146796L;
	@Column(name="`PropName`", nullable=true, length=255)	
	private String propName;
	
	public void setPropName(String value) {
		this.propName = value;
	}
	
	public String getPropName() {
		return propName;
	}
	
	public String toString() {
		return super.toString();
	}
}
