package org.msh.pharmadex.mbean.applicant;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessAppBn implements Serializable {

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private Applicant applicant;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    private User user;
    private List<User> availableUsers;
    private List<User> userList;

    @PostConstruct
    private void init() {
        if (user == null) {
            user = new User();
            user.getAddress().setCountry(new Country());
        }
    }

    @Transactional
    public void addUserToApplicant() {
        if (userList == null)
            userList = new ArrayList<User>();
        userList.add(user);
        applicant.setUsers(userList);
        user = new User();

    }

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            applicant.setUpdatedDate(new Date());
            if (applicant.getUsers() == null || applicant.getUsers().size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            applicant = applicantService.updateApp(applicant, null);

            if (applicant == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
                return null;
            }

            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "applicantMBean", null);
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
//            globalEntityLists.setRegApplicants(null);
            return "/public/applicantlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }


    public void initNewUser() {
        user = new User();
        user.setType(UserType.COMPANY);
        user.setEnabled(false);
    }

    @Transactional
    public void newUser() {
        user.setEnabled(false);
        user.setType(UserType.COMPANY);
        String username = user.getName().replaceAll("\\s", "");
        user.setUsername(username);
        user.setPassword(username);
        user = userService.passwordGenerator(user);
        
        if (userList == null)
            userList = new ArrayList<User>();
        userList.add(user);
        applicant.setUsers(userList);
//        applicantService.updateApp(selectedApplicant, user);
        user = new User();

    }


    public String registerApplicant() {
        applicant.setState(ApplicantState.REGISTERED);
        applicantService.updateApp(applicant, null);
//        globalEntityLists.setRegApplicants(null);
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
        if(userSession.isCompany()){
            return "/home.faces";
        }else {
            return "/internal/processapplist.faces";
        }
    }


    public List<User> completeUserList(String query) {
        return JsfUtils.completeSuggestions(query, getAvailableUsers());
    }

    public String cancelAddUser() {
        user = new User();
        return "";
    }

    public Applicant getApplicant() {
        if (applicant == null) {
            if (userSession.getApplcantID() != null) {
                applicant = applicantService.findApplicant(userSession.getApplcantID());
                if (applicant.getAddress() == null)
                    applicant.setAddress(new Address());
                if (applicant.getAddress().getCountry() == null)
                    applicant.getAddress().setCountry(new Country());
            } else {
                applicant = null;
            }
        }
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
        if (userList == null) {
            if (applicant != null)
                userList = applicant.getUsers();
        }
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
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

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
