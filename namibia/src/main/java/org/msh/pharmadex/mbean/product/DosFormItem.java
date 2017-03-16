package org.msh.pharmadex.mbean.product;

import java.io.Serializable;

/**
 * 
 * @author dudchenko
 *
 */
public class DosFormItem implements Serializable{

	private static final long serialVersionUID = 3597095165870259975L;

	private String name = "";
	private String fullName = "";
	private boolean main = false;
	
	public DosFormItem() {
		super();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isMain() {
		main = name.equals(fullName);
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}
}
