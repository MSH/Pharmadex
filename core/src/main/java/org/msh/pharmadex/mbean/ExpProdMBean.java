package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ExpProdMBean implements Serializable {

    @Autowired
    ProcessProdBn processProdBn;

    @Autowired
    ProdApplicationsService prodApplicationsService;

    private List<ProdApplications> prodApplicationses;
    private List<ProdApplications> filteredApps;


    public String onRowSelect() {
        processProdBn = null;
        return "/internal/processreg.faces";
    }

    public List<ProdApplications> getProdApplicationses() {
        if (prodApplicationses == null) {
            prodApplicationses = prodApplicationsService.findExpiringProd();
        }
        return prodApplicationses;
    }

    public void setProdApplicationses(ArrayList<ProdApplications> prodApplicationses) {
        this.prodApplicationses = prodApplicationses;
    }

    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }
}
