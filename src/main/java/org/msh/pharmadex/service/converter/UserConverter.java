package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
@FacesConverter(value = "userConverter", forClass = User.class)
@Component
public class UserConverter implements Converter,Serializable{
    private static final long serialVersionUID = -1633517224407687494L;
    @Autowired
    private UserService userService;

    private List<User> userList;

    public List<User> getUserList() {
        if(userList==null)
            userList = userService.findUnregisteredUsers();
        return userList;
    }

    public User findUserByID(String name){
       for (User c : getUserList()){
           if(String.valueOf(c.getUserId()).equalsIgnoreCase(name))
               return c;
       }
       return null;
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
            if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (User p : getUserList()) {
                    if(p.getUserId().equals(number))
                        return p;
                }
            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid INN Code"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return ""+value;
        }
    }
}
