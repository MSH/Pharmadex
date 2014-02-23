package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("singleton")
public class GlobalEntityLists {

    private List<DosageForm> dosageForms;
    private List<DosUom> dosUoms;
    private List<Country> countries;
    private List<Product> regProducts;
    private List<Applicant> regApplicants;
    private List<PharmacySite> pharmacySites;
    private List<AmdmtCategory> amdmtCategories;
    private List<ApplicantType> applicantTypes;
    private List<Company> manufacturers;

    @Autowired
    DosageFormService dosageFormService;

    @Autowired
    CountryService countryService;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    ProductService productService;

    @Autowired
    PharmacySiteService pharmacySiteService;

    @Autowired
    AmdmtService amdmtService;

    @Autowired
    CompanyService companyService;

    public List<Company> getManufacturers() {
        if (manufacturers == null)
            manufacturers = companyService.findAllManufacturers();
        return manufacturers;
    }

    public List<AmdmtCategory> getAmdmtCategories() {
        if (amdmtCategories == null)
            amdmtCategories = amdmtService.findAllAmdmtCategory();
        return amdmtCategories;
    }

    public List<DosageForm> getDosageForms() {
        if (dosageForms == null)
            dosageForms = dosageFormService.findAllDosForm();
        return dosageForms;
    }

    public List<Country> getCountries() {
        if (countries == null)
            countries = countryService.getCountries();
        return countries;
    }


    public List<DosUom> getDosUoms() {
        if (dosUoms == null)
            dosUoms = dosageFormService.findAllDosUom();
        return dosUoms;
    }

    public List<Product> getRegProducts() {
        if (regProducts == null)
            regProducts = productService.findAllRegisteredProduct();
        return regProducts;
    }

    public List<Applicant> getRegApplicants() {
        if (regApplicants == null)
            regApplicants = applicantService.getRegApplicants();
        return regApplicants;
    }

    public List<PharmacySite> getPharmacySites() {
        if (pharmacySites == null)
            pharmacySites = pharmacySiteService.findAllPharmacySite(ApplicantState.REGISTERED);
        return pharmacySites;
    }

    public void setPharmacySites(List<PharmacySite> pharmacySites) {
        this.pharmacySites = pharmacySites;
    }

    public void setRegProducts(List<Product> regProducts) {
        this.regProducts = regProducts;
    }

    public void setRegApplicants(List<Applicant> regApplicants) {
        this.regApplicants = regApplicants;
    }

    public List<ApplicantType> getApplicantTypes() {
        if (applicantTypes == null)
            applicantTypes = applicantService.findAllApplicantTypes();
        return applicantTypes;
    }

}
