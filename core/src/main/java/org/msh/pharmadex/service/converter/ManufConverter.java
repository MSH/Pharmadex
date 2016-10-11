package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProductService;
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
@FacesConverter(value = "manufConverter")
@Component
public class ManufConverter implements Converter, Serializable {

   // @Autowired
   // private GlobalEntityLists globalEntityLists;
    
    @Autowired
    private CompanyService companyService;

    //private List<Company> companies;

    /*public List<Company> getCompanies() {
        if (companies == null)
            companies = globalEntityLists.getManufacturers();
        return companies;
    }*/

    /*public Company findpClassifByName(String name) {
        for (Company c : getCompanies()) {
            if (c.getCompanyName().equalsIgnoreCase(name))
                return c;
        }
        return new Company(name);
    }*/


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
    	Company c = null;
        if (submittedValue.trim().equals("")) {
            return c;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                
                c = companyService.findCompanyById(new Long(number));
                /*for (Company p : getCompanies()) {
                    if (p.getId() == number)
                        return p;
                }*/
            } catch (NumberFormatException exception) {
            	c = companyService.findCompanyByName(submittedValue);
            	if(c == null){
            		c = new Company(submittedValue);
            	}
//                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid INN Code"));
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

