package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdAppMBeanMZ implements Serializable {

	private static final long serialVersionUID = -2169563442954064204L;
	
	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    ProdApplicationsServiceMZ prodApplicationsServiceMZ;

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;
	 
	protected List<ProdApplications> prodApplicationsList;
    protected List<ProdApplications> submmittedAppList;
    
    public List<ProdApplications> getProdApplicationsList() {
        if (prodApplicationsList == null)
            prodApplicationsList = prodApplicationsServiceMZ.getSubmittedApplications(userSession);
        return prodApplicationsList;
    }

    public List<ProdApplications> getSubmmittedAppList() {
        if (submmittedAppList == null)
            submmittedAppList = prodApplicationsServiceMZ.getSubmittedApplications(userSession);
        return submmittedAppList;
    }


    public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
        return prodApplicationsServiceMZ;
    }

    public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
        this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
    }
    
    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
