package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.service.SuspendService;
import org.msh.pharmadex.service.SuspendServiceMZ;
import org.msh.pharmadex.util.JsfUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.Flash;
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
public class SuspAppMBeanMZ implements Serializable {

	private static final long serialVersionUID = 4949609035304539253L;

	@ManagedProperty(value = "#{userSession}")
    protected UserSession userSession;
    
    @ManagedProperty(value = "#{suspendServiceMZ}")
    SuspendServiceMZ suspendServiceMZ;
    
    private List<SuspDetail> suspList;
    private List<SuspDetail> filteredList;

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<SuspDetail> getSuspList() {
    	suspList = suspendServiceMZ.findAll(userSession);
		return suspList;
	}

	public void setSuspList(List<SuspDetail> prodAppList) {
		this.suspList = prodAppList;
	}

    public SuspendServiceMZ getSuspendServiceMZ() {
        return suspendServiceMZ;
    }

    public void setSuspendServiceMZ(SuspendServiceMZ suspendService) {
        this.suspendServiceMZ = suspendService;
    }

    public List<SuspDetail> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(List<SuspDetail> filteredApps) {
        this.filteredList = filteredApps;
    }
    
    public String openSuspForm(Long suspID){
    	if(suspID != null && suspID > 0){
    		SuspDetail det = suspendServiceMZ.findSuspDetail(suspID);
    		if(det != null && det.getDecision() != null){
    			if(det.getDecision().equals(RecomendType.SUSPEND))
    				return "/internal/suspenddetail.faces";
    			if(det.getDecision().equals(RecomendType.CANCEL))
    				return "/internal/canceleddetail.faces";
    		}
    	}
    	return "";
    }
}
