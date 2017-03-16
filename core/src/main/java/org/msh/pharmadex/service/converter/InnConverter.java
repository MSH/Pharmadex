package org.msh.pharmadex.service.converter;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.msh.pharmadex.domain.Inn;
import org.msh.pharmadex.service.InnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "innConverter")
@Component
@Scope("singleton")
public class InnConverter implements Converter, Serializable {
    private static final long serialVersionUID = 5821077613663099246L;
    
    @Autowired
    private InnService innService;

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
    	Inn inn = null;
    	if (submittedValue.trim().equals("")) {
            return inn;
        } else {
                /*int number = Integer.parseInt(submittedValue);
                for (Inn p : getInnList()) {
                    if (p.getId() == number)
                        return p;
                }*/
            	try {
                    int number = Integer.parseInt(submittedValue);
                    inn = innService.findInnById(new Long(number));
                } catch (NumberFormatException exception) {
                	inn = innService.findInnByName(submittedValue);
                	if(inn == null){
                		inn = new Inn(submittedValue);
                	}
                }
        }

        return inn;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
    	if (value == null || "".equals(value) || ("null".equals(value))) {
            return null;
        } else {
        	if (value instanceof Long) {
                return String.valueOf(value);
            }else if (value instanceof String) {
                return String.valueOf(value);
            }else if (value instanceof Inn){
                Inn inn = (Inn) value;
                if (inn.getId() != null)
                    return String.valueOf(inn.getId());
                else
                    return inn.getName();
            }
        	/*if(value instanceof Inn){
        		return String.valueOf(((Inn) value).getId());
        	}
        	if(value instanceof Long)
        		return String.valueOf(value);*/
        }
        return null;
    }
}

