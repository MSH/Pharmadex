package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.service.SuspendService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
@ViewScoped
public class SuspAppMBean implements Serializable {
    @ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    protected List<SuspDetail> submmittedAppList;
    @ManagedProperty(value = "#{suspendService}")
    SuspendService suspendService;
    private List<ProdApplications> filteredApps;

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<SuspDetail> getSubmmittedAppList() {
        submmittedAppList = suspendService.findAll(userSession);
        return submmittedAppList;
    }

    public void setSubmmittedAppList(List<SuspDetail> submmittedAppList) {
        this.submmittedAppList = submmittedAppList;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }

    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }
}
