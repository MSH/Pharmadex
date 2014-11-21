package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ExpProdMBean implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    private List<ProdApplications> prodApplicationses;
    private List<ProdApplications> filteredApps;
    private List<ProdApplications> notifiedPayProd;
    private List<ProdApplications> expiredProds;


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

    public List<ProdApplications> getNotifiedPayProd() {
        if (notifiedPayProd == null) {
            notifiedPayProd = prodApplicationsService.findPayNotified();
        }
        return notifiedPayProd;
    }

    public void setNotifiedPayProd(List<ProdApplications> notifiedPayProd) {
        this.notifiedPayProd = notifiedPayProd;
    }

    public List<ProdApplications> getExpiredProds() {
        if (expiredProds == null) {
            expiredProds = prodApplicationsService.findExpiredProd();
        }
        return expiredProds;
    }

    public void setExpiredProds(List<ProdApplications> expiredProds) {
        this.expiredProds = expiredProds;
    }

    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }
}
