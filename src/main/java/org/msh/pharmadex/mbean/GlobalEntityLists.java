package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.service.CountryService;
import org.msh.pharmadex.service.DosageFormService;
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

    @Autowired
    DosageFormService dosageFormService;

    @Autowired
    CountryService countryService;

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

}
