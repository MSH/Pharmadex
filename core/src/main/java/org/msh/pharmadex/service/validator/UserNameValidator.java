package org.msh.pharmadex.service.validator;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class UserNameValidator implements Validator, Serializable {

    @Autowired
    UserService userService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if(o==null){
            FacesMessage msg = new FacesMessage(resourceBundle.getString("valid_value_req"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        String username = o.toString();


        if(userService.isUsernameDuplicated(username)){
            FacesMessage msg = new FacesMessage(username+" "
                    + resourceBundle.getString("valid_user_exist"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
}

