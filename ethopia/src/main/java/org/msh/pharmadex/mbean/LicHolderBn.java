package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.LicenseHolderService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class LicHolderBn implements Serializable {

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private LicenseHolder licenseHolder;

    private List<Applicant> applicants;

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public LicenseHolder getLicenseHolder() {
        if (licenseHolder == null) {
            if(userSession.getLicHolderID()!=null)
            licenseHolder = licenseHolderService.findLicHolder(userSession.getLicHolderID());
        }
        return licenseHolder;
    }

    public void setLicenseHolder(LicenseHolder licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<Applicant> applicants) {
        this.applicants = applicants;
    }

}
