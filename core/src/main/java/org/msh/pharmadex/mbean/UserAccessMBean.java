package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.service.UserAccessService;
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
@Scope("session")
public class UserAccessMBean implements Serializable{
    private static final long serialVersionUID = -759094158707798197L;
    private List<UserAccess> allUserAccess;

    @Autowired
    UserAccessService userAccessService;


//    public void onRowSelect(){
//        setShowAdd(true);
//        System.out.println("inside onrowselect");
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedUser.getName()));
//    }


    public List<UserAccess> getAllUserAccess() {
        if(allUserAccess==null)
            allUserAccess = userAccessService.getUserAccessList();
        return allUserAccess;
    }

    public void setAllUserAccess(List<UserAccess> allUserAccess) {
        this.allUserAccess = allUserAccess;
    }
}
