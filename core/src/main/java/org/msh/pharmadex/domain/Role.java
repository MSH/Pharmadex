package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/14/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = {"rolename"}))
public class Role extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -5806096133693830945L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleId;

    @Column(length = 255, nullable = false)
    private String rolename;

    @Column(length = 255, nullable = false)
    private String displayname;

    @Column(length = 255, nullable = true)
    private String description;


    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}
    
    
}