package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceET;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
public class ProdAppMBeanET extends ProdAppMBean {

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
