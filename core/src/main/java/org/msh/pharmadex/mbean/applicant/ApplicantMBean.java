package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ApplicantType;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
@ManagedBean
@ViewScoped
public class ApplicantMBean implements Serializable {
    @ManagedProperty(value = "#{applicantService}")
    ApplicantService applicantService;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private Applicant selectedApplicant;
    private List<Applicant> allApplicant;
    private List<Applicant> filteredApplicant;
    private boolean showAdd = false;
    private User user;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    private ArrayList<User> userList;

    @PostConstruct
    private void init() {
        selectedApplicant = new Applicant();
        if (userSession.isGeneral()||userSession.isCompany()) {
            user = userSession.getLoggedInUserObj();
            selectedApplicant.getAddress().setCountry(user.getAddress().getCountry());
            selectedApplicant.setContactName(user != null ? user.getName() : null);
            selectedApplicant.setEmail(user != null ? user.getEmail() : null);
            user = userSession.getLoggedInUserObj();

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
            if (selectedApplicant.getUsers() == null && user.getEmail() == null) {
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
        } catch (Exception e) {
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
        return "/home.faces";
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
        return JsfUtils.completeSuggestions(query, getAvailableUsers());
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

    public void setSelectedApplicant(Applicant selectedApplicant) {
        this.selectedApplicant = selectedApplicant;
    }

    public List<Applicant> getAllApplicant() {
        return globalEntityLists.getRegApplicants();
    }

    public void setAllApplicant(List<Applicant> allApplicant) {
        this.allApplicant = allApplicant;
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

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
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
}
