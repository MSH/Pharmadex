package org.msh.pharmadex.service.validator;

import org.msh.pharmadex.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@FacesValidator("futureDateValidator")
public class FutureDateValidator implements Validator {


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if(o==null){
            FacesMessage msg = new FacesMessage(resourceBundle.getString("valid_value_req"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        Date dateObj = (Date) o;


        if(dateObj.before(new Date())){
            FacesMessage msg = new FacesMessage(dateObj
                    +" "+ resourceBundle.getString("past_date"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
}

