package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ApplicantMBean implements Serializable {
    private static final long serialVersionUID = -7233445025890580011L;
    private Applicant selectedApplicant;
    private List<Applicant> allApplicant;
    private List<Applicant> filteredApplicant;
    private boolean showAdd = false;
    private User user;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    UserService userService;

    @Autowired
    CountryService countryService;

    @Autowired
    private UserSession userSession;

    @Autowired
    GlobalEntityLists globalEntityLists;

    private ArrayList<User> userList;

    @PostConstruct
    private void init() {
        selectedApplicant = new Applicant();
        selectedApplicant.getAddress().setCountry(new Country());
        if (userSession.isCompany())
            user = userSession.getLoggedInUserObj();
        else
            user = null;
        selectedApplicant.setContactName(user != null ? user.getName() : null);
        selectedApplicant.setEmail(user != null ? user.getEmail() : null);
    }

    public void onRowSelect() {
        System.out.println("inside onrowselect");
        setShowAdd(true);
        System.out.println("inside onrowselect");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedApplicant.getAppName()));
    }

    public String saveApp() {
        selectedApplicant.setSubmitDate(new Date());
        selectedApplicant.setAppType(null);
        if (applicantService.saveApp(selectedApplicant, userSession.getLoggedInUserObj())) {
            selectedApplicant = new Applicant();
            setShowAdd(false);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "applicantMBean", null);
            globalEntityLists.setRegApplicants(null);
            return "/public/applicantlist.faces";
        } else {
            return null;
        }
    }

//    @PostConstruct
//    private void init(){
//        if(userSession!=null&&userSession.getLoggedInUserObj()!=null&&selectedApplicant==null){
//            selectedApplicant = new applicant();
//            System.out.print("insisde initialization ");
//            System.out.print("insisde initialization ");
//            List<User> users = new ArrayList<User>();
//            User user =userSession.getLoggedInUserObj();
//            users.add(user);
//            selectedApplicant.setUsers(users);
//            selectedApplicant.setEmail(user.getEmail());
//            selectedApplicant.setContactName(user.getName());
//        }
//    }

    public void editApp() {
        System.out.println("inside editUser");
    }

    public String cancelApp() {
        setShowAdd(false);
        selectedApplicant = new Applicant();
        return "/public/registrationhome.faces?redirect=true";
    }

    @Transactional
    public void addUserToApplicant() {
        if (userList == null)
            userList = new ArrayList<User>();
        userList.add(user);
        selectedApplicant.setUsers(userList);
//        applicantService.updateApp(selectedApplicant, user);
        user = new User();

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


    public List<User> getAvailableUsers() {
        return userService.findUnregisteredUsers();
    }


    public Applicant getSelectedApplicant() {
//        init();
        return selectedApplicant;
    }

    public List<Applicant> getAllApplicant() {
        return globalEntityLists.getRegApplicants();
    }

    public void setAllApplicant(List<Applicant> allApplicant) {
        this.allApplicant = allApplicant;
    }

    public void setSelectedApplicant(Applicant selectedApplicant) {
        this.selectedApplicant = selectedApplicant;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Applicant> getFilteredApplicant() {
        return filteredApplicant;
    }

    public void setFilteredApplicant(List<Applicant> filteredApplicant) {
        this.filteredApplicant = filteredApplicant;
    }

    public ArrayList<User> getUserList() {
        if (userList == null) {
            if (selectedApplicant != null)
                userList = (ArrayList<User>) userService.findUserByApplicant(selectedApplicant.getApplcntId());
        }
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }
}
