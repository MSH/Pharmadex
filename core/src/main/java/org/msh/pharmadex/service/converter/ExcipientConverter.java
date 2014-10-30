package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Excipient;
import org.msh.pharmadex.service.InnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "excipientConverter")
@Component
@Scope("singleton")
public class ExcipientConverter implements Converter, Serializable {
    @Autowired
    private InnService innService;

    private List<Excipient> excipients;

    public List<Excipient> getExcipients() {
        if (excipients == null)
            excipients = innService.getExcipients();
        return excipients;
    }

    public Excipient findInnByName(String name) {
        for (Excipient c : getExcipients()) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return new Excipient(name);
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return findInnByName(submittedValue);
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (Excipient p : getExcipients()) {
                    if (p.getId() == number)
                        return p;
                }
            } catch (NumberFormatException exception) {
                return findInnByName(submittedValue);
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Excipient) value).getId());
        }
    }
}

