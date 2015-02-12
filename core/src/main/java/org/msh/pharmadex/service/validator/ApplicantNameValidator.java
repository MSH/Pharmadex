package org.msh.pharmadex.service.validator;

import org.msh.pharmadex.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@Component
public class ApplicantNameValidator implements Validator, Serializable {

    @Autowired
    ApplicantService applicantService;

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if(o==null){
            FacesMessage msg = new FacesMessage(resourceBundle.getString("valid_value_req"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        String applicantName = o.toString();


        if(applicantService.isApplicantDuplicated(applicantName)){
            FacesMessage msg = new FacesMessage(applicantName
                    +" "+ resourceBundle.getString("valid_applicant_exist"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
}

