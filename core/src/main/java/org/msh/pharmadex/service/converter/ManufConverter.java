package org.msh.pharmadex.service.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "manufConverter")
@Component
public class ManufConverter implements Converter, Serializable {

    @Autowired
    private CompanyService companyService;

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
    	Company c = null;
        if (submittedValue.trim().equals("")) {
            return c;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                c = companyService.findCompanyById(new Long(number));
            } catch (NumberFormatException exception) {
            	c = companyService.findCompanyByName(submittedValue);
            	if(c == null){
            		c = new Company(submittedValue);
            	}
            }
        }

        return c;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
    	if (value == null || "".equals(value) || ("null".equals(value))) {
            return null;
        } else {
            if (value instanceof Long) {
                return String.valueOf(value);
            }else if (value instanceof String) {
                return String.valueOf(value);
            }else if (value instanceof Company){
                Company company = (Company) value;
                if (company.getId() != null)
                    return String.valueOf(company.getId());
                else
                    return company.getCompanyName();
            }
        }
        return null;
    }
}

