package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.service.AtcService;
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
@FacesConverter(value = "atcCodeConverter")
@Component
@Scope("singleton")
public class AtcCodeConverter implements Converter, Serializable {
    private static final long serialVersionUID = 1599505668323428406L;
    @Autowired
    private AtcService atcService;

    private List<Atc> atcList;

    public List<Atc> getAtcList() {
        if (atcList == null)
            atcList = atcService.getAtcList();
        return atcList;
    }

    public Atc findInnByCode(String name) {
        for (Atc c : getAtcList()) {
            if (c.getAtcCode().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return findInnByCode(submittedValue);
        } else {
            try {
                for (Atc p : getAtcList()) {
                    if (p.getAtcCode() == submittedValue) {
                        return p;
                    }
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
            return ((Atc) value).getAtcCode();
        }
    }
}

