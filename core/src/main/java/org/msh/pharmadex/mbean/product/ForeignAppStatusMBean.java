package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ForeignAppStatus;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.util.JsfUtils;
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

    @ManagedProperty(value = "#{regHomeMbean}")
    RegHomeMbean regHomeMbean;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{countryService}")
    CountryService countryService;

    @ManagedProperty(value = "#{companyService}")
    CompanyService companyService;

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

    @Transactional
    public void addForStatus() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            foreignAppStatuses = regHomeMbean.getForeignAppStatuses();
            if (foreignAppStatuses == null) {
                foreignAppStatuses = new ArrayList<ForeignAppStatus>();
            }
            selForeignAppStatus.setProdApplications(regHomeMbean.getProdApplications());
            selForeignAppStatus.setCountry(countryService.findCountryById(selForeignAppStatus.getCountry().getId()));
            foreignAppStatuses.add(selForeignAppStatus);
            regHomeMbean.setForeignAppStatuses(foreignAppStatuses);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("company_add_success")));
            selForeignAppStatus = new ForeignAppStatus();
            selForeignAppStatus.setCountry(new Country());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), e.getMessage()));
        }
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

    public RegHomeMbean getRegHomeMbean() {
        return regHomeMbean;
    }

    public void setRegHomeMbean(RegHomeMbean regHomeMbean) {
        this.regHomeMbean = regHomeMbean;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
        return foreignAppStatuses;
    }

    public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
        this.foreignAppStatuses = foreignAppStatuses;
    }


}
