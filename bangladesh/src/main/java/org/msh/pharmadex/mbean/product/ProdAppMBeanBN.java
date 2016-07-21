package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceBN;

@ManagedBean
@ViewScoped
public class ProdAppMBeanBN implements Serializable {

	private static final long serialVersionUID = -2392902050214575039L;

	@ManagedProperty(value = "#{prodApplicationsServiceBN}")
    ProdApplicationsServiceBN prodApplicationsServiceBN;

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;
	
	protected List<ProdApplications> submmittedAppList;
    protected List<ProdApplications> processProdAppList;
    private List<ProdApplications> filteredApps;

    public List<ProdApplications> getProcessProdAppList() {
    	if(processProdAppList == null)
    		processProdAppList = prodApplicationsServiceBN.getProcessProdAppList(userSession);
		return processProdAppList;
	}

	public void setProcessProdAppList(List<ProdApplications> processProdAppList) {
		this.processProdAppList = processProdAppList;
	}

	public ProdApplicationsServiceBN getProdApplicationsServiceBN() {
        return prodApplicationsServiceBN;
    }

    public void setProdApplicationsServiceBN(ProdApplicationsServiceBN prodApplicationsServiceBN) {
        this.prodApplicationsServiceBN = prodApplicationsServiceBN;
    }
    
    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
    
    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }

	public List<ProdApplications> getSubmmittedAppList() {
		if(submmittedAppList == null)
			submmittedAppList = prodApplicationsServiceBN.getSubmittedApplications(userSession);
		return submmittedAppList;
	}

	public void setSubmmittedAppList(List<ProdApplications> submmittedAppList) {
		this.submmittedAppList = submmittedAppList;
	}
}
