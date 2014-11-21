package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.PharmacySiteChecklist;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.PharmacySiteService;
import org.msh.pharmadex.service.UserService;
import org.springframework.web.util.WebUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@SessionScoped
public class ProcessRxSiteBn {

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    private PharmacySite selectedSite;
    private List<PharmacySite> pendingRxSite;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{pharmacySiteService}")
    private PharmacySiteService pharmacySiteService;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;

    private User user;
    private List<User> availableUsers;
    private List<User> userList;
    private List<PharmacySiteChecklist> siteChecklists;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void addUserToRxSite() {
        if (userList == null)
            userList = new ArrayList<User>();
        userList.add(user);
        selectedSite.setUsers(userList);
        pharmacySiteService.updateApp(selectedSite, user);
        user = new User();

    }

    public String registerRxSite() {
        facesContext = FacesContext.getCurrentInstance();
        for (PharmacySiteChecklist psc : siteChecklists) {
            if (!psc.isStaffValue()) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", resourceBundle.getString("premises_particular_valid")));
                return "/internal/processrxsite.faces";
            }

        }
        selectedSite.setState(ApplicantState.REGISTERED);
        pharmacySiteService.updateApp(selectedSite, userSession.getLoggedInUserObj());
        globalEntityLists.setPharmacySites(null);
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processRxSiteBn", null);
        return "/internal/processrxsitelist.faces";

    }

    public String cancel() {
        facesContext = FacesContext.getCurrentInstance();
        selectedSite = new PharmacySite();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processRxSiteBn", null);

        if (userSession.isCompany())
            return "/secure/submittedrxsites.faces";
        else
            return "/internal/processrxsitelist.faces";
    }

    public List<User> completeUserList(String query) {
        List<User> suggestions = new ArrayList<User>();

        if (query == null || query.equalsIgnoreCase(""))
            return getAvailableUsers();

        for (User eachInn : getAvailableUsers()) {
            if (eachInn.getName().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachInn);
        }
        return suggestions;
    }

    public String cancelAddUser() {
        user = new User();
        return "";
    }

    public List<PharmacySite> getPendingRxSite() {
        return pharmacySiteService.findAllPharmacySite(ApplicantState.NEW_APPLICATION);
    }

    public void setPendingApps(List<PharmacySite> pendingRxSite) {
        this.pendingRxSite = pendingRxSite;
    }

    public List<User> getAvailableUsers() {
        return userService.findUnregisteredUsers();
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }

    public User getUser() {
        return getUserList().get(0);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userService.findUsersBySite(selectedSite.getId());
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public PharmacySite getSelectedRxSite() {
        return selectedSite;
    }

    public void setSelectedRxSite(PharmacySite pharmacySite) {
        this.selectedSite = pharmacySite;
        siteChecklists = pharmacySiteService.findChecklistBySite(selectedSite.getId());
    }

    public List<PharmacySiteChecklist> getSiteChecklists() {
        return siteChecklists;
    }

    public void setSiteChecklists(List<PharmacySiteChecklist> siteChecklists) {
        this.siteChecklists = siteChecklists;
    }
}
