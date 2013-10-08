package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ProcessAppBn {

    private Applicant applicant;

    private List<Applicant> pendingApps;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private UserService userService;

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    private MailService mailService;

    private User user;
    private List<User> availableUsers;
    private List<User> userList;

    @Transactional
    public void addUserToApplicant() {
        if (userList == null)
            userList = new ArrayList<User>();
        userList.add(user);
        applicant.setUsers(userList);
        applicantService.updateApp(applicant, user);
        user = new User();

    }

    public String registerApplicant() {
        applicant.setState(ApplicantState.REGISTERED);
        applicantService.updateApp(applicant, null);
        globalEntityLists.setRegApplicants(null);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processAppBn", null);
        return "/internal/processapplist.faces";

    }

    public String cancel() {
        applicant = new Applicant();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processAppBn", null);
        return "/internal/processapplist.faces";
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

    public List<Applicant> getPendingApps() {
        return applicantService.getPendingApplicants();
    }

    public void setPendingApps(List<Applicant> pendingApps) {
        this.pendingApps = pendingApps;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public List<User> getAvailableUsers() {
        return userService.findUnregisteredUsers();
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userService.findUserByApplicant(applicant.getApplcntId());
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
