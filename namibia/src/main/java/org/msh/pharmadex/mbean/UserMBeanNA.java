package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Address;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Letter;
import org.msh.pharmadex.domain.Mail;
import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.LetterService;
import org.msh.pharmadex.service.MailService;
import org.msh.pharmadex.service.UserService;
import org.primefaces.model.DualListModel;
import org.springframework.web.util.WebUtils;

/**
 * Created by dudchenko
 */
@ManagedBean
@ViewScoped
public class UserMBeanNA implements Serializable {
	
	@ManagedProperty(value = "#{userMBean}")
    public UserMBean userMBean;
	@ManagedProperty(value = "#{roleDAO}")
    RoleDAO roleDAO;
    @ManagedProperty(value = "#{letterService}")
    LetterService letterService;
    @ManagedProperty(value = "#{mailService}")
    MailService mailService;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    @ManagedProperty(value = "#{workspaceDAO}")
    WorkspaceDAO workspaceDAO;
    
    FacesContext facesContext = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    
    private UserType userType = UserType.COMPANY;
    private DualListModel<Role> roles;
    private List<Role> rolesList;
    private List<Role> allRoles;
    private List<Role> selectedRoles;
    private boolean edit = false;
    

    public String exception() throws Exception {
        throw new Exception();
    }

    @PostConstruct
    private void init() {
        allRoles = (List<Role>) roleDAO.findAll();
        selectedRoles = new ArrayList<Role>();
        roles = new DualListModel<Role>(allRoles, selectedRoles);
    }
    
	public UserMBean getUserMBean() {
		return userMBean;
	}

	public void setUserMBean(UserMBean userMBean) {
		this.userMBean = userMBean;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public String saveUser(User selectedUser) {
        facesContext = FacesContext.getCurrentInstance();
        if (selectedUser.getUserId() != null && selectedUser.getUserId() == 0)
            selectedUser.setUserId(null);
        selectedUser.setRoles(roles.getTarget());

        if (isEdit()) {
            updateUser(selectedUser);
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
            //allUsers = null;
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
                WebUtils.setSessionAttribute(request, "userMBeanNA", null);
                return "/admin/userslist_bk.faces";
            } catch (Exception e) {
                e.printStackTrace();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("email_error")));
                return "";
            }
        }

    }

	public String updateUser(User selectedUser) {
        selectedUser.setRoles(roles.getTarget());

        selectedUser.setUpdatedDate(new Date());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            selectedUser = userService.updateUser(selectedUser);
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
            WebUtils.setSessionAttribute(request, "userMBeanNA", null);
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("no_user")));
        }
        return "";
        //return backTo;
    }
	
	public void cancelUser(User selectedUser) {
        selectedUser = new User();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "userMBeanNA", null);

    }
	
	public String cleanAssignCompany(User selectedUser) {
		selectedUser.setCompanyName("");
		selectedUser.setApplicant(null);
    	return "";
    }
	
	public String resetPassword(User selectedUser) {
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
                String sender = workspaceDAO.findAll().get(0).getRegistraremail();
                if (sender==null)
                    sender = "info@msh.org";
                else if ("".equals(sender))
                    sender = "info@msh.org";
                mailService.sendMailFromSender(mail, false,sender);
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
	
	public DualListModel<Role> getRoles() {
		return roles;
	}

	public void setRoles(DualListModel<Role> roles) {
		this.roles = roles;
	}

	public List<Role> getRolesList() {
		return rolesList;
	}

	public void setRolesList(List<Role> rolesList) {
		this.rolesList = rolesList;
		roles.setTarget(rolesList);
	}

	public List<Role> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<Role> allRoles) {
		this.allRoles = allRoles;
	}

	public List<Role> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(List<Role> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public WorkspaceDAO getWorkspaceDAO() {
		return workspaceDAO;
	}

	public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	public boolean isNewUser(User selectedUser) {
		if(selectedUser != null && selectedUser.getUserId() != null && selectedUser.getUserId() > 0)
			return false;
		return true;
	}

	
}
