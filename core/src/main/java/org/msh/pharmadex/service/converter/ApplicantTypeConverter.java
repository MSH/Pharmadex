package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.ApplicantType;
import org.msh.pharmadex.service.ApplicantService;
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
@FacesConverter(value = "applicantTypeConverter")
@Component
public class ApplicantTypeConverter implements Converter, Serializable {
    @Autowired
    private ApplicantService applicantService;

    private List<ApplicantType> applicantTypeList;

    public List<ApplicantType> getApplicantTypeList() {
        if (applicantTypeList == null)
            applicantTypeList = applicantService.findAllApplicantTypes();
        return applicantTypeList;
    }

    public ApplicantType findApplicantTypeByID(String name) {
        for (ApplicantType c : getApplicantTypeList()) {
            if (String.valueOf(c.getId()).equals(name))
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
                for (ApplicantType p : getApplicantTypeList()) {
                    if (p.getId().equals(Long.valueOf(submittedValue))) {
                        return p;
                    }
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
            return String.valueOf(value);
        }
    }
}

