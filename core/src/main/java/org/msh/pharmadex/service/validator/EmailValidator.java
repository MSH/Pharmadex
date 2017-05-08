package org.msh.pharmadex.service.validator;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.mbean.UserMBean;
import org.springframework.stereotype.Component;

/**
 * Author: usrivastava
 */
@Component
@Deprecated
public class EmailValidator implements Validator, Serializable {

	private static final long serialVersionUID = -1927773446792527429L;
	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);

	}


	/**
	 * Rules:
	 * <ul>
	 * <li>Empty eMail allows only for Company user
	 * <li>If email not empty, then structure of address should be checked unconditionally
	 * </ul>
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		UserMBean userMBean
		= (UserMBean) FacesContext.getCurrentInstance().getApplication()
		.getELResolver().getValue(elContext, null, "userMBean");
		ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		User selectedUser = null;
		if(userMBean != null){
			selectedUser = userMBean.getSelectedUser();
		}

		if(selectedUser != null){
			if(o==null && selectedUser.isCompany()){
				return;
			}
		}
		if(o==null){
			FacesMessage msg = new FacesMessage(resourceBundle.getString("email_is_empty"));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		String email = o.toString().trim();
		matcher = pattern.matcher(email);
		if(!matcher.matches()){
			FacesMessage msg = new FacesMessage(resourceBundle.getString("valid_email"));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		//        if(userService.isEmailDuplicated(email)){
		//            FacesMessage msg = new FacesMessage(email
		//                    + resourceBundle.getString("valid_user_exist"));
		//            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		//            throw new ValidatorException(msg);
		//        }

	}
}

