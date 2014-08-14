package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ApplicantType;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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

    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @PostConstruct
    private void init() {
        selectedApplicant = new Applicant();
        if (userSession.isCompany()) {
            user = userSession.getLoggedInUserObj();
            selectedApplicant.getAddress().setCountry(countryService.findCountryById(user.getAddress().getCountry().getId()));
            selectedApplicant.setContactName(user != null ? user.getName() : null);
            selectedApplicant.setEmail(user != null ? user.getEmail() : null);
        } else {
            if (selectedApplicant.getAddress() != null)
                selectedApplicant.getAddress().setCountry(new Country());
            if (user == null) {
                user = new User();
                user.getAddress().setCountry(new Country());
            }
        }
    }

    public void onRowSelect() {
        setShowAdd(true);
        facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), "Selected " + selectedApplicant.getAppName()));
    }

    public void initNewUser() {
        user = new User();
        user.setType(UserType.COMPANY);
        user.setEnabled(false);
    }

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            selectedApplicant.setSubmitDate(new Date());
            if(selectedApplicant.getUsers()==null && user.getEmail() == null) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            selectedApplicant = applicantService.saveApp(selectedApplicant, user);
            if (selectedApplicant != null) {
                user.setApplicant(selectedApplicant);
                if (userSession.isCompany()) {
                    userSession.getLoggedInUserObj().setApplicant(selectedApplicant);
                    userSession.setDisplayAppReg(false);
                }
                selectedApplicant = new Applicant();
                setShowAdd(false);
                HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
                WebUtils.setSessionAttribute(request, "applicantMBean", null);
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_submit_success")));
                return "/public/applicantlist.faces";
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    public List<ApplicantType> completeApplicantTypeList(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getApplicantTypes());
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
        facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "applicantMBean", null);
        return "/public/registrationhome.faces";
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

    @Transactional
    public void newUser() {
        user.setEnabled(false);
        user.setType(UserType.COMPANY);
        String username = user.getName().replaceAll("\\s", "");
        user.setUsername(username);
        user.setPassword(username);
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
