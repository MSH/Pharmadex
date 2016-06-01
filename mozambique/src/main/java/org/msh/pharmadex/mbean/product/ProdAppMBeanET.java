package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceET;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdAppMBeanET extends ProdAppMBean {

	private static final long serialVersionUID = -2169563442954064204L;
	
	@ManagedProperty(value = "#{prodApplicationsServiceET}")
    ProdApplicationsServiceET prodApplicationsServiceET;

    @Override
    public List<ProdApplications> getProdApplicationsList() {
        if (prodApplicationsList == null)
            prodApplicationsList = prodApplicationsServiceET.getSubmittedApplications(userSession);
        return prodApplicationsList;
    }

    @Override
    public List<ProdApplications> getSubmmittedAppList() {
        if (submmittedAppList == null)
            submmittedAppList = prodApplicationsServiceET.getSubmittedApplications(userSession);
        return submmittedAppList;
    }


    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }
}
