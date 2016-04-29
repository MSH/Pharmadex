package org.msh.pharmadex.service.validator;

import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Author: dudchenko
 * проверка введенной даты
 * введенная дата позже текущей - ошибка
 */
@FacesValidator("pastDateValidator")
public class PastDateValidator implements Validator {


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if(o==null){
            FacesMessage msg = new FacesMessage(resourceBundle.getString("valid_value_req"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        Date dateObj = (Date) o;


        if(dateObj.after(new Date())){
            FacesMessage msg = new FacesMessage(dateObj
                    +" "+ resourceBundle.getString("past_date"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
}

