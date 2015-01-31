package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.mbean.product.UserDTO;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class POAppSelectBn extends PIPAppSelectBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(POAppSelectBn.class);

    @ManagedProperty(value = "#{purOrderBn}")
    PurOrderBn purOrderBn;

    @Transactional
    public void addApptoRegistration() {
        selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
        if(selectedUser!=null)
            applicantUser = userService.findUser(selectedUser.getUserId());
        purOrderBn.setApplicant(selectedApplicant);
        purOrderBn.setApplicantUser(applicantUser);
    }

    public PurOrderBn getPurOrderBn() {
        return purOrderBn;
    }

    public void setPurOrderBn(PurOrderBn purOrderBn) {
        this.purOrderBn = purOrderBn;
    }
}
