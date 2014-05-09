package org.msh.pharmadex.mbean;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.domain.Mail;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class RegisterUserMbean implements Serializable {
    private static final long serialVersionUID = -5045721468877947576L;
    private User user;
    private String newpwd1;
    private String newpwd2;
    private String oldpwd;

    private static final Logger logger = LoggerFactory.getLogger(RegisterUserMbean.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserSettingBean userSettingBean;


    @PostConstruct
    private void init() {
        if (userSession.getLoggedInUserObj() != null)
            user = userSession.getLoggedInUserObj();
        else
            user = new User();
    }

    public String cancel() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "registerUserMbean", null);
        return "/public/registrationhome.faces";
    }

    public String save() {
        user.setType(org.msh.pharmadex.domain.enums.UserType.COMPANY);
        String password = PassPhrase.getNext();
        logger.info("======================================== ");
        logger.info("\"password ============== \"+password");
        logger.info("======================================== ");
        user.setPassword(password);
        Mail mail = new Mail();
        mail.setMailto(user.getEmail());
        mail.setSubject("Pharmadex User Registration");
        mail.setUser(user);
        mail.setDate(new Date());
        mail.setMessage("Thank you for registering yourself for Pharmadex. In order to access the system please use the username '" + user.getUsername() + "' and password '" + password + "' ");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String retvalue;
        try {
            retvalue = userService.createUser(user);
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Email already exists"));
            return "/page/registeruser.faces";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getMessage()));
            e.printStackTrace();
            return "/page/registeruser.faces";
        }
        if (!retvalue.equalsIgnoreCase("persisted")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", retvalue));
            return "/page/registeruser.faces";
        } else {
            try {
                mailService.sendMail(mail, false);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Your password has been mailed to the email address provided at the time of registration. Please use the password to log into the system and change your password"));
                return "/public/registrationhome.faces";
            } catch (Exception e) {
                e.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Error sending email"));
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String retvalue;
        try {
            retvalue = userService.updateUser(user);
        } catch (ConstraintViolationException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Email already exists"));
            return "/page/registeruser.faces";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getMessage()));
            e.printStackTrace();
            return "/page/registeruser.faces";
        }
        if (!retvalue.equalsIgnoreCase("persisted")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", retvalue));
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
        FacesContext facesContext = FacesContext.getCurrentInstance();

        String result = userService.changePwd(user, oldpwd, newpwd1);
        if (result.equalsIgnoreCase("PWDERROR")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password incorrect!!!"));
            return null;
        }

        if (result.equalsIgnoreCase("persisted")) {
            userSettingBean.setPreference(true);
            userSettingBean.setSelection("preference");
            userSettingBean.active();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password successfully changed!!!"));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
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
}
