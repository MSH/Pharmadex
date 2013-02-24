package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Inn;
import org.msh.pharmadex.service.InnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "innConverter", forClass = Atc.class)
@Component
@Scope("singleton")
public class InnConverter implements Converter, Serializable {
    private static final long serialVersionUID = 5821077613663099246L;
    @Autowired
    private InnService innService;

    private List<Inn> innList;

    public List<Inn> getInnList() {
        if (innList == null)
            innList = innService.getInnList();
        return innList;
    }

    public Inn findInnByName(String name) {
        for (Inn c : getInnList()) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return findInnByName(submittedValue);
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (Inn p : getInnList()) {
                    if (p.getId() == number)
                        return p;
                }
            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid INN Code"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Inn) value).getId());
        }
    }
}

