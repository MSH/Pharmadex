package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ForeignAppStatus;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ForeignAppStatusMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ForeignAppStatusMBean.class);

    @ManagedProperty(value = "#{prodRegAppMbean}")
    private ProdRegAppMbean prodRegAppMbean;

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    private ForeignAppStatus selForeignAppStatus;
    private List<ForeignAppStatus> foreignAppStatuses;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @PostConstruct
    public void init() {
        if (selForeignAppStatus == null)
            selForeignAppStatus = new ForeignAppStatus();
        selForeignAppStatus.setCountry(new Country());
    }

    public String addForStatus() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            foreignAppStatuses = prodRegAppMbean.getForeignAppStatuses();
            if (foreignAppStatuses == null) {
                foreignAppStatuses = new ArrayList<ForeignAppStatus>();
            }
            selForeignAppStatus.setProdApplications(prodRegAppMbean.getProdApplications());
            RetObject retObject = prodApplicationsService.saveForeignAppStatus(selForeignAppStatus);
            if(retObject.getMsg().equals("persist")) {
                selForeignAppStatus = (ForeignAppStatus) retObject.getObj();
                foreignAppStatuses.add(selForeignAppStatus);
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("company_add_success")));
                selForeignAppStatus = new ForeignAppStatus();
                selForeignAppStatus.setCountry(new Country());
            }else{
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), retObject.getMsg()));
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), e.getMessage()));
        }
        return "";
    }

    public void initAddCompany() {
        selForeignAppStatus = new ForeignAppStatus();
        selForeignAppStatus.setCountry(new Country());
    }

    public String cancelAdd() {
        selForeignAppStatus = null;
        return null;
    }

    public ForeignAppStatus getSelForeignAppStatus() {
        if (selForeignAppStatus == null)
            selForeignAppStatus = new ForeignAppStatus();
        return selForeignAppStatus;
    }

    public void setSelForeignAppStatus(ForeignAppStatus selForeignAppStatus) {
        this.selForeignAppStatus = selForeignAppStatus;
    }

    public List<Company> completeCompany(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getManufacturers());
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
        return foreignAppStatuses;
    }

    public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
        this.foreignAppStatuses = foreignAppStatuses;
    }

    public ProdRegAppMbean getProdRegAppMbean() {
        return prodRegAppMbean;
    }

    public void setProdRegAppMbean(ProdRegAppMbean prodRegAppMbean) {
        this.prodRegAppMbean = prodRegAppMbean;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }


}
