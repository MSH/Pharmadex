package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.PharmClassif;
import org.msh.pharmadex.mbean.GlobalEntityLists;
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
@FacesConverter(value = "ManufConverterConverter")
@Component
@Scope("request")
public class ManufConverter implements Converter, Serializable {


    @Autowired
    private GlobalEntityLists globalEntityLists;

    private List<Company> companies;

    public List<Company> getCompanies() {
        if (companies == null)
            companies = globalEntityLists.getManufacturers();
        return companies;
    }

    public Company findpClassifByName(String name) {
        for (Company c : getCompanies()) {
            if (c.getCompanyName().equalsIgnoreCase(name))
                return c;
        }
        return new Company(name);
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (Company p : getCompanies()) {
                    if (p.getId() == number)
                        return p;
                }
            } catch (NumberFormatException exception) {
                return findpClassifByName(submittedValue);
//                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid INN Code"));
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

