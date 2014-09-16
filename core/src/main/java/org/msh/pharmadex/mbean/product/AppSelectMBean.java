package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@Component
@Scope("view")
public class AppSelectMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AppSelectMBean.class);

    @Autowired
    RegHomeMbean regHomeMbean;

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    CountryService countryService;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    UserService userService;

    private Applicant selectedApplicant;
    private User applicantUser;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private boolean showApp;
    private boolean showUser;
    private boolean showUserSelect;

    @Transactional
    public void gmpChangeListener() {
//        if (selectedCompany.isGmpInsp())
//            showGMP = true;
//        else
//            showGMP = false;
        logger.error("inside gmpChangeListener");
        if (selectedApplicant != null && selectedApplicant.getApplcntId() != null) {
            selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
            List<User> users = userService.findUserByApplicant(selectedApplicant.getApplcntId());
            selectedApplicant.setUsers(users);
            showApp = true;
            if (users.size() > 1) {
                setShowUserSelect(true);
            } else {
                if (users.size() == 1) {
                    applicantUser = users.get(0);
                    showUser = true;
                }
            }
        }

    }

    public void appChangeListenener(SelectEvent event) {
        logger.error("inside appChangeListenener");
        logger.error("Selected company is " + selectedApplicant.getAppName());
        logger.error("event " + event.getObject());
        gmpChangeListener();


    }

    public void appChangeListenener(AjaxBehaviorEvent event) {
        logger.error("inside appChangeListenener");
        logger.error("Selected company is " + selectedApplicant.getAppName());
        logger.error("event " + event.getSource());
        gmpChangeListener();


    }

    @Transactional
    public void addApptoRegistration() {
        selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
        applicantUser = userService.findUser(applicantUser.getUserId());
        regHomeMbean.setApplicant(selectedApplicant);
        regHomeMbean.setApplicantUser(applicantUser);
    }

    public void cancelAddApplicant() {
        selectedApplicant = new Applicant();
        applicantUser = new User();
    }


    public List<Applicant> completeApplicantList(String query) {
        List<Applicant> applicants = applicantService.findAllApplicants();
        return JsfUtils.completeSuggestions(query, applicants);
    }

    public boolean isShowApp() {
        return showApp;
    }

    public void setShowApp(boolean showApp) {
        this.showApp = showApp;
    }

    public boolean isShowUserSelect() {
        return showUserSelect;
    }

    public void setShowUserSelect(boolean showUserSelect) {
        this.showUserSelect = showUserSelect;
    }

    public Applicant getSelectedApplicant() {
        return selectedApplicant;
    }

    public void setSelectedApplicant(Applicant selectedApplicant) {
        this.selectedApplicant = selectedApplicant;
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public boolean isShowUser() {
        return showUser;
    }

    public void setShowUser(boolean showUser) {
        this.showUser = showUser;
    }
}
