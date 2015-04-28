package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.PharmacySiteService;
import org.msh.pharmadex.service.UserService;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class PharmacySiteMbean implements Serializable {


    @ManagedProperty(value = "#{pharmacySiteService}")
    PharmacySiteService pharmacySiteService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    @ManagedProperty(value = "#{userService}")
    UserService userService;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    private List<PharmacySiteChecklist> siteChecklists;

    private PharmacySite selectedSite;

    private User user;

    private List<PharmacySite> allSites;

    private List<PharmacySite> filteredSites;

    private List<PharmacySite> submittedSites;

    @PostConstruct
    private void init() {
        selectedSite = new PharmacySite();
        selectedSite.setSiteAddress(new Address());
        selectedSite.getSiteAddress().setCountry(new Country());
        siteChecklists = new ArrayList<PharmacySiteChecklist>();
        user = userService.findUser(userSession.getLoggedINUserID());

        List<SiteChecklist> allChecklist = pharmacySiteService.findAllCheckList();
        PharmacySiteChecklist eachPharmacySiteChecklist;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachPharmacySiteChecklist = new PharmacySiteChecklist();
            eachPharmacySiteChecklist.setSiteChecklist(allChecklist.get(i));
            eachPharmacySiteChecklist.setPharmacySite(selectedSite);
            siteChecklists.add(eachPharmacySiteChecklist);
        }

    }

    public String saveApp() {
        selectedSite.setSubmitDate(new Date());
        ArrayList<User> users = new ArrayList<User>();
        users.add(user);
        selectedSite.setUsers(users);
        selectedSite.setState(ApplicantState.NEW_APPLICATION);
        selectedSite.setApplicantName(user.getName());
        selectedSite.setEmail(user.getEmail());
        selectedSite.setFaxNo(user.getFaxNo());
        selectedSite.setPhoneNo(user.getPhoneNo());
        selectedSite.setPharmacySiteChecklists(siteChecklists);
        if (pharmacySiteService.saveSite(selectedSite).equalsIgnoreCase("persisted")) {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "applicantMBean", null);
            globalEntityLists.setPharmacySites(null);
            return "/public/rxsitelist.faces";
        } else {
            return null;
        }
    }

    public List<PharmacySite> getAllSites() {
        return allSites;
    }

    public void setAllSites(List<PharmacySite> allSites) {
        this.allSites = allSites;
    }

    public List<PharmacySite> getRegSites() {
        allSites = globalEntityLists.getPharmacySites();
        return allSites;
    }

    public PharmacySite getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(PharmacySite selectedSite) {
        this.selectedSite = selectedSite;
    }

    public List<PharmacySiteChecklist> getSiteChecklists() {
        return siteChecklists;
    }

    public void setSiteChecklists(List<PharmacySiteChecklist> siteChecklists) {
        this.siteChecklists = siteChecklists;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PharmacySite> getFilteredSites() {
        return filteredSites;
    }

    public void setFilteredSites(List<PharmacySite> filteredSites) {
        this.filteredSites = filteredSites;
    }

    public List<PharmacySite> getSubmittedSites() {
        if (submittedSites == null) {
            List<User> users = new ArrayList<User>();
            users.add(user);
            submittedSites = pharmacySiteService.findPharmacySiteByStateUser(users, ApplicantState.NEW_APPLICATION);
        }
        return submittedSites;
    }

    public void setSubmittedSites(List<PharmacySite> submittedSites) {
        this.submittedSites = submittedSites;
    }

    public PharmacySiteService getPharmacySiteService() {
        return pharmacySiteService;
    }

    public void setPharmacySiteService(PharmacySiteService pharmacySiteService) {
        this.pharmacySiteService = pharmacySiteService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }
}
