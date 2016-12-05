package org.msh.pharmadex.service.converter;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.springframework.stereotype.Component;
/**
 * Removes all lead and trail spaces AND convert all spaces inside the string to one space like fullTrim
 * @author Alex Kurasoff
 *
 */
@FacesConverter(forClass = String.class, value = "stringTrimConverter")
@Component
public class StringTrimConverter implements Converter {
	 @Override
	    public Object getAsObject(FacesContext context, UIComponent cmp, String value) {

	        if (value != null && cmp instanceof HtmlInputText) {
	            // trim the entered value in a HtmlInputText before doing validation/updating the model
	            return value.trim().replaceAll(" +", " ");
	        }

	        return value;
	    }

	    @Override
	    public String getAsString(FacesContext context, UIComponent cmp, Object value) {

	        if (value != null) {
	            // return the value as is for presentation
	            return value.toString();
	        }
	        return null;
	    }
}
