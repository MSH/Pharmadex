package org.msh.pharmadex.mbean;

import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.Role;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class RoleMBean implements Serializable {
    private static final long serialVersionUID = -8564371266339354819L;

    @ManagedProperty(value = "#{roleDAO}")
    RoleDAO roleDAO;

    public List<Role> findAllRoles() {
        return (List<Role>) roleDAO.findAll();
    }

    public RoleDAO getRoleDAO() {
        return roleDAO;
    }

    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }
}
