package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdAppMBeanMZ extends ProdAppMBean {

	private static final long serialVersionUID = -2169563442954064204L;
	
	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    ProdApplicationsServiceMZ prodApplicationsServiceMZ;

    @Override
    public List<ProdApplications> getProdApplicationsList() {
        if (prodApplicationsList == null)
            prodApplicationsList = prodApplicationsServiceMZ.getSubmittedApplications(userSession);
        return prodApplicationsList;
    }

    @Override
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
}
