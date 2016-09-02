package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

@ManagedBean(name="prodAppMBeanMZ")
@ViewScoped
public class ProdAppMBeanMZ implements Serializable {

	private static final long serialVersionUID = -2169563442954064204L;

	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
	ProdApplicationsServiceMZ prodApplicationsServiceMZ;

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;

	protected List<ProdApplications> submmittedAppList;
	protected List<ProdApplications> processProdAppList;
	private List<ProdApplications> filteredApps;

	protected FacesContext facesContext = getCurrentInstance();
	protected java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

	//public LazyDataModel<ProdApplications> lazyModel;

	/*@PostConstruct
    public void init() {
        lazyModel = new ProdAppDataModel(prodApplicationsServiceMZ.getProcessProdAppList(userSession), prodApplicationsServiceMZ.getProdApplicationsService());
    }

	public LazyDataModel<ProdApplications> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<ProdApplications> lazyModel) {
		this.lazyModel = lazyModel;
	}
*/

	public List<ProdApplications> getProcessProdAppList() {
		if(processProdAppList == null)
			processProdAppList = prodApplicationsServiceMZ.getProcessProdAppList(userSession);
		return processProdAppList;
	}

	public void setProcessProdAppList(List<ProdApplications> processProdAppList) {
		this.processProdAppList = processProdAppList;
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

	public List<ProdApplications> getFilteredApps() {
		return filteredApps;
	}

	public void setFilteredApps(List<ProdApplications> filteredApps) {
		this.filteredApps = filteredApps;
	}

	public List<ProdApplications> getSubmmittedAppList() {
		if(submmittedAppList == null){
			submmittedAppList = prodApplicationsServiceMZ.getSubmittedApplications(userSession);
		}
		return submmittedAppList;
	}

	public void setSubmmittedAppList(List<ProdApplications> submmittedAppList) {
		this.submmittedAppList = submmittedAppList;
	}

	public String buildColReviewStatus(ProdApplications prodApp){
		if(prodApp != null && prodApp.getReviewStatus() != null){
			return resourceBundle.getString(prodApp.getReviewStatus().getKey());
		}
		return "";
	}
}
