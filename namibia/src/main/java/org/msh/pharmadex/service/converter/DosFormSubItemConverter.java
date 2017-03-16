package org.msh.pharmadex.service.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.msh.pharmadex.mbean.product.DosFormItem;
import org.msh.pharmadex.service.DosageFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@FacesConverter(value = "dosFormSubItemConverter")
@Component
@Scope("singleton")
public class DosFormSubItemConverter implements Converter, Serializable {

	private static final long serialVersionUID = 208340933306277271L;
	
	@Autowired
    private DosageFormService dosService;

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
    	DosFormItem form = null;
    	if (submittedValue.trim().equals("")) {
            return form;
        } else {
            	form = dosService.findDosFormItemByName(submittedValue, false);
                if(form == null){
                	form = new DosFormItem();
                	form.setName(submittedValue	);
                }
        }

        return form;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
    	if (value == null || "".equals(value) || ("null".equals(value))) {
            return null;
        } else {
        	if (value instanceof Long) {
                return null;
            }else if (value instanceof String) {
                return String.valueOf(value);
            }else if (value instanceof DosFormItem){
            	DosFormItem dos = (DosFormItem) value;
                return dos.getName();
            }
        }
        return null;
    }
}
