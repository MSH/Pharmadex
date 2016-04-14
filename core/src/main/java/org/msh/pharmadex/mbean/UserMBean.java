package org.msh.pharmadex.mbean;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.LetterService;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.primefaces.model.DualListModel;
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

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UserMBean implements Serializable {
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    @ManagedProperty(value = "#{applicantService}")
    ApplicantService applicantService;
    @ManagedProperty(value = "#{mailService}")
    MailService mailService;
    @ManagedProperty(value = "#{roleDAO}")
    RoleDAO roleDAO;
    @ManagedProperty(value = "#{letterService}")
    LetterService letterService;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private User selectedUser;
    private List<User> allUsers;
    private DualListModel<Role> roles;
    private List<Role> allRoles;
    private List<Role> selectedRoles;
    private boolean edit;
    private Long prevApplicantId;
    private Applicant userApp;
    private String email;

    public String exception() throws Exception {
        throw new Exception();
    }

    @PostConstruct
    private void init() {
        allRoles = (List<Role>) roleDAO.findAll();
        selectedRoles = new ArrayList<Role>();
        roles = new DualListModel<Role>(allRoles, selectedRoles);
        selectedUser = new User();
        selectedUser.setAddress(new Address());
        selectedUser.setApplicant(new Applicant());
        userApp = new Applicant();
    }


    public void onRowSelect() {
        setEdit(true);
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedUser.getName()));
    }

    public String goToResetPwd() {
        return "/public/resetpwd.faces";
    }

    public String startReset() {
        selectedUser = new User();
        selectedUser.setEmail(email);
        selectedUser = userService.findByUsernameOrEmail(selectedUser);
        resetPassword();
        return "/home.faces";

    }

    public String saveUser() {
        facesContext = FacesContext.getCurrentInstance();
        if (selectedUser.getUserId() == 0)
            selectedUser.setUserId(null);
        selectedUser.setRoles(roles.getTarget());
        if (selectedUser != null && selectedUser.getApplicant() != null && selectedUser.getApplicant().getApplcntId() != null)
            selectedUser.setApplicant(applicantService.findApplicant(selectedUser.getApplicant().getApplcntId()));
        else
            selectedUser.setApplicant(null);
        if (isEdit()) {
            updateuser();
            return "";
        }

        if (userService.isUsernameDuplicated(selectedUser.getUsername())) {
            FacesMessage msg = new FacesMessage(selectedUser.getUsername() + " "
                    + bundle.getString("valid_user_exist"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesContext.addMessage(null, msg);
            facesContext.validationFailed();
            return "";
        }

        if (userService.isEmailDuplicated(selectedUser.getEmail())) {
            FacesMessage msg = new FacesMessage(selectedUser.getEmail() + " "
                    + bundle.getString("valid_email_exist"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesContext.addMessage(null, msg);
            facesContext.validationFailed();
            return "";
        }

        String password = PassPhrase.getNext();
        selectedUser.setPassword(password);
        selectedUser.setRegistrationDate(new Date());
        Letter letter = letterService.findByLetterType(LetterType.NEW_USER_REGISTRATION);
        Mail mail = new Mail();
        mail.setMailto(selectedUser.getEmail());
        mail.setSubject(letter.getSubject());
        mail.setMailto(selectedUser.getEmail());
        mail.setUser(selectedUser);
        mail.setDate(new Date());
        mail.setMessage(bundle.getString("email_user_reg1") + selectedUser.getUsername() + bundle.getString("email_user_reg2") + password + bundle.getString("email_user_reg3"));
        String retvalue;
        try {
            retvalue = userService.createUser(selectedUser);
            allUsers = null;
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_exists")));
            return "";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
            e.printStackTrace();
            return "";
        }
        if (!retvalue.equalsIgnoreCase("persisted")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), retvalue));
            return "";
        } else {
            try {
                mailService.sendMail(mail, false);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("send_password_success")));
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                WebUtils.setSessionAttribute(request, "userMBean", null);
                return "/admin/userslist_bk.faces";
            } catch (Exception e) {
                e.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_error")));
                return "";
            }
        }

    }

    public void updateuser() {
        selectedUser.setRoles(roles.getTarget());
        selectedUser.setApplicant(getUserApp());
//        if(!prevApplicantId.equals(userApp.getApplcntId())){
//            selectedUser.setApplicant(applicantService.findApplicant(userApp.getApplcntId()));
//        }
        selectedUser.setUpdatedDate(new Date());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            selectedUser = userService.updateUser(selectedUser);
            allUsers = null;
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_exists")));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
            e.printStackTrace();
        }
        if (selectedUser != null) {
            setEdit(false);
            selectedUser = new User();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), selectedUser.getName() + bundle.getString("global.success")));
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "userMBean", null);
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("no_user")));
        }
//        return "/admin/userslist_bk.faces";
    }

    public void addUser() {
        selectedRoles = new ArrayList<Role>();
        allRoles = roleDAO.findAll();
        roles = new DualListModel<Role>(allRoles, selectedRoles);
        selectedUser = new User();
        selectedUser.setAddress(new Address());
        selectedUser.setApplicant(new Applicant());
        setEdit(false);
    }

    public String resetPassword() {
        String password = PassPhrase.getNext();
        selectedUser.setPassword(password);
        System.out.println("Password == " + password);
        selectedUser.setUpdatedDate(new Date());
        Mail mail = new Mail();
        mail.setMailto(selectedUser.getEmail());
        mail.setSubject("Password Reset");
//        mail.setSubject(bundle.getString("reset_pwd_sub"));
        mail.setUser(selectedUser);
        mail.setDate(new Date());
        mail.setMessage("Your password has been successfully reset In order to access the system please use the username '" + selectedUser.getUsername() + "' and password '" + password + "' ");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            selectedUser = userService.updateUser(userService.passwordGenerator(selectedUser));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
            e.printStackTrace();
            return "";
        }
        if (selectedUser == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("save_error")));
            return "";
        } else {
            try {
                mailService.sendMail(mail, false);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("send_password_success")));
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                WebUtils.setSessionAttribute(request, "userMBean", null);
                return "/admin/userslist_bk.faces";
            } catch (Exception e) {
                e.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_error")));
                return "";
            }
        }

    }

    public void cancelUser() {
        selectedUser = new User();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "userMBean", null);

    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = userService.findUser(selectedUser.getUserId());
        this.selectedRoles = this.selectedUser.getRoles();
        roles.setTarget(selectedRoles);
        if (selectedUser.getApplicant() != null)
            userApp = applicantService.findApplicant(selectedUser.getApplicant().getApplcntId());
        this.prevApplicantId = userApp.getApplcntId();
    }

    public List<User> getAllUsers() {
        if(allUsers==null)
            allUsers = userService.findAllUsers();
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public DualListModel<Role> getRoles() {
        if (roles != null) {

        }
        return roles;
    }

    public void setRoles(DualListModel<Role> roles) {
        this.roles = roles;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public Applicant getUserApp() {
        return userApp;
    }

    public void setUserApp(Applicant userApp) {
        this.userApp = userApp;
        
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public RoleDAO getRoleDAO() {
        return roleDAO;
    }

    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public LetterService getLetterService() {
        return letterService;
    }

    public void setLetterService(LetterService letterService) {
        this.letterService = letterService;
    }
}
