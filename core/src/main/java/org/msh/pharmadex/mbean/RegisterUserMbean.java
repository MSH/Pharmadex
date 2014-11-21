package org.msh.pharmadex.mbean;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.Letter;
import org.msh.pharmadex.domain.Mail;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.service.LetterService;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@SessionScoped
public class RegisterUserMbean implements Serializable {
    private static final long serialVersionUID = -5045721468877947576L;
    private static final Logger logger = LoggerFactory.getLogger(RegisterUserMbean.class);
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private User user;
    private String newpwd1;
    private String newpwd2;
    private String oldpwd;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{mailService}")
    private MailService mailService;
    @ManagedProperty(value = "#{userSettingBean}")
    private UserSettingBean userSettingBean;
    @ManagedProperty(value = "#{letterService}")
    private LetterService letterService;

    @PostConstruct
    private void init() {
        if (userSession.getLoggedInUserObj() != null)
            user = userSession.getLoggedInUserObj();
        else {
            user = new User();
            user.getAddress().setCountry(new Country());
        }
    }

    public String cancel() {
        facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "registerUserMbean", null);
        return "/public/registrationhome.faces";
    }

    public String save() {
        facesContext = FacesContext.getCurrentInstance();
        user.setType(org.msh.pharmadex.domain.enums.UserType.COMPANY);
        String password = PassPhrase.getNext();
        logger.info("======================================== ");
        logger.info("\"password ============== \"+password");
        logger.info("======================================== ");
        user.setPassword(password);
        String retvalue;
        try {
            retvalue = userService.createPublicUser(user);
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("user_exists_valid")));
            return "";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("user_register_error")));
            e.printStackTrace();
            return "";
        }
        if (!retvalue.equalsIgnoreCase("persisted")) {
            facesContext.addMessage(null, new FacesMessage(retvalue));
            return "";
        } else {
            try {
                Letter letter = letterService.findByLetterType(LetterType.NEW_USER_REGISTRATION);
                Mail mail = new Mail();
                mail.setMailto(user.getEmail());
                mail.setSubject(letter.getSubject());
                mail.setUser(user);
                mail.setDate(new Date());
//                mail.setMessage(letter.getBody());
                mail.setMessage(bundle.getString("email_user_reg1") + user.getUsername() + bundle.getString("email_user_reg2") + password + bundle.getString("email_user_reg3"));
                mailService.sendMail(mail, false);
                facesContext.addMessage(null, new FacesMessage(bundle.getString("user_email_success")));
                return "/public/registrationhome.faces";
            } catch (Exception e) {
                e.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_error")));
                return null;
            }
        }
    }

    public String update() {
//        String password = PassPhrase.getNext();
//        user.setPassword(password);
//        Mail mail = new Mail();
//        mail.setMailto(user.getEmail());
//        mail.setSubject("Pharmadex User Registration");
//        mail.setUser(user);
//        mail.setDate(new Date());
//        mail.setMessage("Thank you for registering yourself for Pharmadex. In order to access the system please use the username '" + user.getUsername() + "' and password '" + password + "' ");
        String retvalue;
        facesContext = FacesContext.getCurrentInstance();
        try {
            user = userService.updateUser(user);
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_exists")));
            return "/page/registeruser.faces";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), e.getMessage()));
            e.printStackTrace();
            return "/page/registeruser.faces";
        }
        if (user == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("save_error")));
            return "/page/registeruser.faces";
        } else {
            return "/public/registrationhome.faces";
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNewpwd1() {
        return newpwd1;
    }

    public void setNewpwd1(String newpwd1) {
        this.newpwd1 = newpwd1;
    }

    public String getNewpwd2() {
        return newpwd2;
    }

    public void setNewpwd2(String newpwd2) {
        this.newpwd2 = newpwd2;
    }

    public String changePwd() throws NoSuchFieldException, IllegalAccessException {
        facesContext = FacesContext.getCurrentInstance();
        String result = userService.changePwd(user, oldpwd, newpwd1);
        if (result.equalsIgnoreCase("PWDERROR")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("password_error")));
            return null;
        }

        if (result.equalsIgnoreCase("persisted")) {
            userSettingBean.setPreference(true);
            userSettingBean.setSelection("preference");
            userSettingBean.active();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("password_success")));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return null;
    }

    public String cancelPwdChange() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        userSettingBean.setPreference(true);
        userSettingBean.setSelection("preference");
        userSettingBean.active();
        return "/secure/usersettings.faces";  //To change body of created methods use File | Settings | File Templates.
    }

    public String getOldpwd() {
        return oldpwd;
    }

    public void setOldpwd(String oldpwd) {
        this.oldpwd = oldpwd;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public UserSettingBean getUserSettingBean() {
        return userSettingBean;
    }

    public void setUserSettingBean(UserSettingBean userSettingBean) {
        this.userSettingBean = userSettingBean;
    }

    public LetterService getLetterService() {
        return letterService;
    }

    public void setLetterService(LetterService letterService) {
        this.letterService = letterService;
    }
}
