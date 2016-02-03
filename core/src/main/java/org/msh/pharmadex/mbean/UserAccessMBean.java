package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.UserAccess;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.service.UserAccessService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
@SessionScoped
public class UserAccessMBean implements Serializable {
    private static final long serialVersionUID = -759094158707798197L;
    @ManagedProperty(value = "#{userAccessService}")
    UserAccessService userAccessService;
    private List<UserAccess> allUserAccess;
    private List<UserAccess> filteredAllUserAccess;
    private Workspace workspace;
    private boolean detailReview;


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

    public UserAccessService getUserAccessService() {
        return userAccessService;
    }

    public void setUserAccessService(UserAccessService userAccessService) {
        this.userAccessService = userAccessService;
    }

    public Workspace getWorkspace() {
        if(workspace==null){
            workspace = userAccessService.getWorkspace();
        }
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public boolean isDetailReview() {
        detailReview = getWorkspace().isDetailReview();
        return detailReview;
    }

    public void setDetailReview(boolean detailReview) {
        this.detailReview = detailReview;
    }

    public List<UserAccess> getFilteredAllUserAccess() {
        return filteredAllUserAccess;
    }

    public void setFilteredAllUserAccess(List<UserAccess> filteredAllUserAccess) {
        this.filteredAllUserAccess = filteredAllUserAccess;
    }
}
