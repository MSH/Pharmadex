package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.service.LicenseHolderService;
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
@FacesConverter(value = "licHolderConverter")
@Component
@Scope("singleton")
public class LicHolderConverter implements Converter, Serializable {
    @Autowired
    private LicenseHolderService licenseHolderService;

    private List<LicenseHolder> licenseHolders;

    public List<LicenseHolder> getLicenseHolders() {
        if (licenseHolders == null)
            licenseHolders = licenseHolderService.findAllLicenseHolder();
        return licenseHolders;
    }

    public void setLicenseHolders(List<LicenseHolder> licenseHolders) {
        this.licenseHolders = licenseHolders;
    }

    public LicenseHolder findLicHolderByID(String name) {
        for (LicenseHolder c : getLicenseHolders()) {
            if (String.valueOf(c.getId()).equalsIgnoreCase(name))
                return c;
        }
        return null;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
//                int number = Integer.parseInt(submittedValue);
                for (LicenseHolder p : getLicenseHolders()) {
                    if (String.valueOf(p.getId()).equalsIgnoreCase(submittedValue))
                        return p;
                }
            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Applicant"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            if (value instanceof Long)
                return String.valueOf(value);
            else if (value instanceof String){
                LicenseHolder lc = licenseHolderService.findByName(((String) value));
                if (lc!=null){
                    return String.valueOf(lc.getId());
                }
            }else if (value instanceof LicenseHolder){
                return String.valueOf(((LicenseHolder) value).getId());
            }
        }
        return "";
    }
}

