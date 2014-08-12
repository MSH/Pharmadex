package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.UserAccessDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.domain.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class UserAccessService implements Serializable{

    private static final long serialVersionUID = 1652556697442962034L;
    @Resource
    UserAccessDAO userAccessDAO;

    @Autowired
    WorkspaceDAO workspaceDAO;

    public Workspace getWorkspace(){
        return workspaceDAO.findOne((long) 1);
    }

    public List<UserAccess> getUserAccessList(){
        return userAccessDAO.allUserAccess();
    }

    public String saveUserAccess(UserAccess userAccess){
        return userAccessDAO.saveUserAcess(userAccess);
    }

    public String update(UserAccess userAccess){
        return userAccessDAO.updateUserAccess(userAccess);
    }

}
