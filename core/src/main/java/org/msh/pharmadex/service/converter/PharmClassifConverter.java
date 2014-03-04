package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.PharmClassif;
import org.msh.pharmadex.mbean.GlobalEntityLists;
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
@FacesConverter(value = "pharmClassifConverter")
@Component
@Scope("singleton")
public class PharmClassifConverter implements Converter, Serializable {


    @Autowired
    private GlobalEntityLists globalEntityLists;

    private List<PharmClassif> pharmClassifs;

    public List<PharmClassif> getPharmClassifs() {
        if (pharmClassifs == null)
            pharmClassifs = globalEntityLists.getPharmClassifs();
        return pharmClassifs;
    }

    public PharmClassif findpClassifByName(String name) {
        for (PharmClassif c : getPharmClassifs()) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return findpClassifByName(submittedValue);
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (PharmClassif p : getPharmClassifs()) {
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
            return String.valueOf(value);
        }
    }
}

