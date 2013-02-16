package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "roleConverter", forClass = Role.class)
@Component
public class RoleConverter implements Converter,Serializable {

    private static final long serialVersionUID = -7425826087579166730L;

    @Autowired
    RoleDAO roleDAO;

    private Iterable<Role> roles;

    public Iterable<Role> getRoles() {
        if(roles==null)
            roles = roleDAO.findAll();
        return roles;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);

                for (Role p : getRoles()) {
                    if (p.getRoleId() == number) {
                        return p;
                    }
                }

            } catch(NumberFormatException exception) {
                exception.printStackTrace();
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid role"));
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Role) value).getRoleId());
        }
    }


}
