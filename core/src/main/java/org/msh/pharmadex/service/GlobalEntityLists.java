package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.CustomFeeSchDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("singleton")
public class GlobalEntityLists implements Serializable {

    private static final long serialVersionUID = 5852117386686135477L;
    private List<DosageForm> dosageForms;
    private List<DosUom> dosUoms;
    private List<Country> countries;
    private List<ProdTable> regProducts;
    private List<Applicant> regApplicants;
    private List<PharmacySite> pharmacySites;
    private List<AmdmtCategory> amdmtCategories;
    private List<ApplicantType> applicantTypes;
    private List<Company> manufacturers;
    private List<PharmClassif> pharmClassifs;
    private List<Inn> inns;
    private List<Atc> atcs;
    private List<AdminRoute> adminRoutes;
    private List<Excipient> excipients;
    private List<DisplayReviewQ> displayReviewQ;
    private List<FeeSchedule> feeSchedules;
    private Workspace workspace;
    private List<SRA> sras;


    @Autowired
    private UserAccessService userAccessService;

    @Autowired
    private CustomFeeSchDAO customFeeSchDAO;

    @Autowired
    private DosageFormService dosageFormService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PharmacySiteService pharmacySiteService;

    @Autowired
    private AmdmtService amdmtService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PharmClassifService pharmClassifService;

    @Autowired
    private InnService innService;

    @Autowired
    private AtcService atcService;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private AdminRouteService adminRouteService;

    @Autowired
    private UserService userService;

    @Autowired
    private SraService sraService;

    public List<Atc> getAtcs() {
        if (atcs == null)
            atcs = atcService.getAtcList();
        return atcs;
    }

    public List<Inn> getInns() {
        if (inns == null)
            inns = innService.getInnList();
        return inns;
    }

    public List<Company> getManufacturers() {
        if (manufacturers == null)
            manufacturers = companyService.findAllManufacturers();
        return manufacturers;
    }

    public void setManufacturers(List<Company> manufacturers) {
        this.manufacturers = manufacturers;
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

    public List<Country> completeCountryList(String query) {
        return JsfUtils.completeSuggestions(query, getCountries());
    }

    public List<SRA> getSras() {
        if (sras == null)
            sras = sraService.findAllSRAs();
        return sras;
    }

    public void setSras(List<SRA> sras) {
        this.sras = sras;
    }

    public List<DosUom> getDosUoms() {
        if (dosUoms == null)
            dosUoms = dosageFormService.findAllDosUom();
        return dosUoms;
    }

    public List<ProdTable> getRegProducts() {
        if (regProducts == null)
            regProducts = productService.findAllRegisteredProduct();
        return regProducts;
    }

    public void setRegProducts(List<ProdTable> regProducts) {
        this.regProducts = regProducts;
    }

    public List<Applicant> getRegApplicants() {
        if (regApplicants == null)
            regApplicants = applicantService.getRegApplicants();
        return regApplicants;
    }

    public void setRegApplicants(List<Applicant> regApplicants) {
        this.regApplicants = regApplicants;
    }

    public List<PharmacySite> getPharmacySites() {
        if (pharmacySites == null)
            pharmacySites = pharmacySiteService.findAllPharmacySite(ApplicantState.REGISTERED);
        return pharmacySites;
    }

    public void setPharmacySites(List<PharmacySite> pharmacySites) {
        this.pharmacySites = pharmacySites;
    }

    public List<ApplicantType> getApplicantTypes() {
        if (applicantTypes == null)
            applicantTypes = applicantService.findAllApplicantTypes();
        return applicantTypes;
    }

    public List<PharmClassif> getPharmClassifs() {
        if (pharmClassifs == null)
            pharmClassifs = pharmClassifService.getPharmClassifList();
        return pharmClassifs;
    }

    public List<PharmClassif> completePharmClassif(String query) {
        return JsfUtils.completeSuggestions(query, getPharmClassifs());
    }


    public List<AdminRoute> getAdminRoutes() {
        if (adminRoutes == null)
            adminRoutes = adminRouteService.getAdminRoutes();
        return adminRoutes;
    }

    public List<Excipient> getExcipients() {
        if (excipients == null)
            excipients = innService.getExcipients();
        return excipients;
    }

    public List<FeeSchedule> getFeeSchedules() {
        if (feeSchedules == null) {
            feeSchedules = customFeeSchDAO.findByStartAndEndDate(new Date());
        }
        return feeSchedules;
    }

    public List<Atc> completeAtcNames(String query) {
        return JsfUtils.completeSuggestions(query, getAtcs());
    }

    public List<Inn> completeInnCodes(String query) {
        return JsfUtils.completeSuggestions(query, getInns());
    }

    public List<Excipient> completeExcipients(String query) {
        return JsfUtils.completeSuggestions(query, getExcipients());
    }

    public List<User> completeProcessorList(String query) {
        return JsfUtils.completeSuggestions(query, userService.findProcessors());
    }

    public List<User> completeModeratorList(String query) {
        return JsfUtils.completeSuggestions(query, userService.findModerators());
    }

    public Workspace getWorkspace() {
        if(workspace==null)
            workspace = userAccessService.getWorkspace();
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
