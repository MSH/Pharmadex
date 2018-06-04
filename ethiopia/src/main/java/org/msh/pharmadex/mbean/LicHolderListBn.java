package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.UserService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class LicHolderListBn implements Serializable {

    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    private List<LicenseHolder> licenseHolders;
    private List<LicenseHolder> filteredlicHolders;

    public List<LicenseHolder> getLicenseHolders() {
        if(licenseHolders==null){
            licenseHolders = licenseHolderService.findAllLicenseHolder();
        }
        return licenseHolders;
    }

    public void setLicenseHolders(List<LicenseHolder> licenseHolders) {
        this.licenseHolders = licenseHolders;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<LicenseHolder> getFilteredlicHolders() {
        return filteredlicHolders;
    }

    public void setFilteredlicHolders(List<LicenseHolder> filteredlicHolders) {
        this.filteredlicHolders = filteredlicHolders;
    }
}
