package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.PharmacySiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class PharmacySiteMbean implements Serializable {


    @Autowired
    PharmacySiteService pharmacySiteService;

    @Autowired
    UserSession userSession;

    @Autowired
    GlobalEntityLists globalEntityLists;

    private List<PharmacySiteChecklist> siteChecklists;

    private PharmacySite selectedSite;

    private User user;


    @PostConstruct
    private void init(){
        selectedSite = new PharmacySite();
        selectedSite.setSiteAddress(new Address());
        selectedSite.getSiteAddress().setCountry(new Country());
        siteChecklists = new ArrayList<PharmacySiteChecklist>();
        user = userSession.getLoggedInUserObj();

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
        if (pharmacySiteService.saveSite(selectedSite).equalsIgnoreCase("persisted")){
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "applicantMBean", null);
            globalEntityLists.setPharmacySites(null);
            return "/public/applicantlist.faces";
        } else {
            return null;
        }
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
}
