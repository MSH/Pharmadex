package org.msh.pharmadex.mbean.applicant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.UserRole;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.mbean.UserMBeanNA;

/**
 * Created by dudchenko
 */
@ManagedBean
@ViewScoped
public class ApplicantMBeanNA implements Serializable {

	private static final long serialVersionUID = -816576878651980032L;
	
	@ManagedProperty(value = "#{applicantMBean}")
	public ApplicantMBean applicantMBean;
	
	@ManagedProperty(value = "#{userMBeanNA}")
    public UserMBeanNA userMBeanNA;

	public void newUser() {
		User user = applicantMBean.getUser();
		user.setEnabled(false);
		user.setType(UserType.COMPANY);
		
		String username = user.getUsername().replaceAll("\\s", "");
		user.setUsername(username);
		user.setPassword(username);

		user = applicantMBean.getUserService().passwordGenerator(user);
		List<Role> roles = new ArrayList<Role>();
		Role role = applicantMBean.findRole(UserRole.ROLE_COMPANY);
		if(role != null)
			roles.add(role);
		user.setRoles(roles);
		
		User verifUser = applicantMBean.getUserService().findByUsernameOrEmail(user);
		if(verifUser != null){// dublicate
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.validationFailed();
			ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
			facesContext.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("valid_user_exist"), resourceBundle.getString("valid_user_exist")));
			return ;
		}

		if (applicantMBean.getUsersByApplicant() == null)
			applicantMBean.setUsersByApplicant(new ArrayList<User>());
		applicantMBean.getUsersByApplicant().add(user);
		applicantMBean.getSelectedApplicant().setUsers(applicantMBean.getUsersByApplicant());
		
		applicantMBean.setUser(new User());
	}
	
	public List<User> buildUsersByApplicant(){
		if(applicantMBean.getUsersByApplicant() != null && applicantMBean.getUsersByApplicant().size() > 0){
			List<User> list = new ArrayList<User>();
			list.addAll(applicantMBean.getUsersByApplicant());
			applicantMBean.setUsersByApplicant(new ArrayList<User>());
			for(User u:list){
				applicantMBean.getUsersByApplicant().add(applicantMBean.getUserService().findUser(u.getUserId()));
			}
		}
		return applicantMBean.getUsersByApplicant();
	}

	public ApplicantMBean getApplicantMBean() {
		return applicantMBean;
	}
	public void setApplicantMBean(ApplicantMBean applicantMBean) {
		this.applicantMBean = applicantMBean;
	}

	public UserMBeanNA getUserMBeanNA() {
		return userMBeanNA;
	}

	public void setUserMBeanNA(UserMBeanNA userMBeanNA) {
		this.userMBeanNA = userMBeanNA;
	}
	
	
}
