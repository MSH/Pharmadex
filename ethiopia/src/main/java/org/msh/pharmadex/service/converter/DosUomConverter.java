package org.msh.pharmadex.service.converter;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.service.DosageFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Author: dudchenko
 */
@FacesConverter(value = "dosUomConverter")
@Component
@Scope("singleton")
public class DosUomConverter implements Converter, Serializable {
    @Autowired
    private DosageFormService dosageFormService;

    private List<DosUom> dosUoms;

    public List<DosUom> getDosUoms() {
        if (dosUoms == null)
        	dosUoms = dosageFormService.findAllDosUom();
        return dosUoms;
    }

    public DosUom findDosUomByName(String name) {
        for (DosUom c : getDosUoms()) {
            if (c.getUom().equalsIgnoreCase(name))
                return c;
        }
        return new DosUom(name);
    }


    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return findDosUomByName(submittedValue);
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                for (DosUom p : getDosUoms()) {
                    if (p.getId() == number)
                        return p;
                }
            } catch (NumberFormatException exception) {
                return findDosUomByName(submittedValue);
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
        	if(value instanceof DosUom)
        		return String.valueOf(((DosUom)value).getId());
        	else if(value instanceof Integer){
        		if(((Integer) value).intValue() == 0)
        			return "";
        		else
        			return String.valueOf(value);
        	}else
        		return "";
        }
    }
}

