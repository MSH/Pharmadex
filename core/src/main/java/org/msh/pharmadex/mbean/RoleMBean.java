package org.msh.pharmadex.mbean;

import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class RoleMBean implements Serializable{
    private static final long serialVersionUID = -8564371266339354819L;

    @Autowired
    RoleDAO roleDAO;

    public List<Role> findAllRoles(){
        return (List<Role>) roleDAO.findAll();
    }

}
