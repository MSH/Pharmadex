package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;

/**
 * Author: dudchenko
 */
@ManagedBean
@RequestScoped
public class ExpProdMBeanNA implements Serializable {

    @ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    ProdApplicationsServiceMZ prodApplicationsServiceMZ;

    private List<ProdApplications> expiringProdApplicationses;

    public List<ProdApplications> getExpiringProdApplicationses() {
        if (expiringProdApplicationses == null) {
        	expiringProdApplicationses = prodApplicationsServiceMZ.findExpiringProd();
        }
        return expiringProdApplicationses;
    }

    public void setExpiringProdApplicationses(ArrayList<ProdApplications> prodApplicationses) {
        this.expiringProdApplicationses = prodApplicationses;
    }


    public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
        return prodApplicationsServiceMZ;
    }

    public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsService) {
        this.prodApplicationsServiceMZ = prodApplicationsService;
    }
}
