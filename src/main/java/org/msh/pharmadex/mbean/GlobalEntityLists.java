package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.DosageFormService;
import org.msh.pharmadex.service.ProductService;
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

    @Autowired
    DosageFormService dosageFormService;

    @Autowired
    CountryService countryService;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    ProductService productService;

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

    public void setRegProducts(List<Product> regProducts) {
        this.regProducts = regProducts;
    }

    public void setRegApplicants(List<Applicant> regApplicants) {
        this.regApplicants = regApplicants;
    }
}
