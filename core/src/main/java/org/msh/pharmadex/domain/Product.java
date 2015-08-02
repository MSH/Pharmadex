package org.msh.pharmadex.domain;

import org.hibernate.envers.Audited;
import org.msh.pharmadex.domain.enums.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "product")
@Audited
public class Product extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -8204053633675277911L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(name = "prod_name", length = 500)
    @Size(max = 500, min = 3)
    private String prodName;

    @Column(name = "gen_name", length = 500)
    @Size(max = 500, min = 3)
    private String genName;

    @Column(name = "apprvd_name", length = 500)
    @Size(max = 500, min = 3)
    private String apprvdName;

    @Column(name = "prod_desc", length = 500)
    @Size(max = 500, min = 3)
    private String prodDesc;

    @Column(name = "new_chemical_entity")
    private boolean newChemicalEntity;

    @Column(name = "new_chem_name", length = 500)
    @Size(max = 500, min = 3)
    private String newChemicalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOSFORM_ID")
    private DosageForm dosForm;

    @Column(name = "dosage_strength")
    private String dosStrength;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOSUNIT_ID")
    private DosUom dosUnit;

    @Column(name = "prod_type")
    @Enumerated(EnumType.STRING)
    private ProdType prodType;

    @Column(name = "age_group")
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(name = "prod_cat")
    @Enumerated(EnumType.STRING)
    private ProdCategory prodCategory;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<ProdInn> inns;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<ProdExcipient> excipients;

    @ManyToMany(targetEntity = Atc.class, fetch = FetchType.LAZY)
    @JoinTable(name = "prod_atc", joinColumns = @JoinColumn(name = "prod_id"), inverseJoinColumns = @JoinColumn(name = "atc_id"))
    private List<Atc> atcs;

    private boolean noAtc = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ROUTE_ID")
    private AdminRoute adminRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHARM_CLASSIF_ID")
    private PharmClassif pharmClassif;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<ProdCompany> prodCompanies;

    @OneToMany(mappedBy = "product")
    private List<ProdApplications> prodApplicationses;

    @Transient
    private String manufName;

    @Column(length = 500)
    private String ingrdStatment;

    @OneToOne(cascade = CascadeType.ALL)
    private Pricing pricing;

    @Enumerated(EnumType.STRING)
    private ProdDrugType drugType;

    @ElementCollection(targetClass = UseCategory.class)
    @JoinTable(name = "tblusecategories", joinColumns = @JoinColumn(name = "prodID"))
    @Column(name = "useCategory")
    @Enumerated(EnumType.STRING)
    private List<UseCategory> useCategories;

    @Column(length = 500)
    private String packSize;

    @Column(length = 500)
    private String contType;

    @Column(length = 500)
    private String shelfLife;

    @Column(length = 500)
    private String storageCndtn;

    @Column(length = 1500)
    private String indications;

    @Column(length = 500)
    private String posology;

    @Column(length = 500)
    private String fnm;

    @Column(length = 500)
    private String pharmacopeiaStds;


    public String getManufName() {
        if (prodCompanies != null) {
            for (ProdCompany c : getProdCompanies()) {
                if (c.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)) {
                    return c.getCompany().getCompanyName();
                }
            }
        }
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }

    public String getApprvdName() {
        return apprvdName;
    }

    public void setApprvdName(String apprvdName) {
        this.apprvdName = apprvdName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public boolean isNewChemicalEntity() {
        return newChemicalEntity;
    }

    public void setNewChemicalEntity(boolean newChemicalEntity) {
        this.newChemicalEntity = newChemicalEntity;
    }

    public String getNewChemicalName() {
        return newChemicalName;
    }

    public void setNewChemicalName(String newChemicalName) {
        this.newChemicalName = newChemicalName;
    }

    public DosageForm getDosForm() {
        return dosForm;
    }

    public void setDosForm(DosageForm dosForm) {
        this.dosForm = dosForm;
    }

    public String getDosStrength() {
        return dosStrength;
    }

    public void setDosStrength(String dosStrength) {
        this.dosStrength = dosStrength;
    }

    public DosUom getDosUnit() {
        return dosUnit;
    }

    public void setDosUnit(DosUom dosUnit) {
        this.dosUnit = dosUnit;
    }

    public ProdType getProdType() {
        return prodType;
    }

    public void setProdType(ProdType prodType) {
        this.prodType = prodType;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public ProdCategory getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(ProdCategory prodCategory) {
        this.prodCategory = prodCategory;
    }

    public List<ProdInn> getInns() {
        return inns;
    }

    public void setInns(List<ProdInn> inns) {
        this.inns = inns;
    }

    public List<ProdExcipient> getExcipients() {
        return excipients;
    }

    public void setExcipients(List<ProdExcipient> excipients) {
        this.excipients = excipients;
    }

    public List<Atc> getAtcs() {
        return atcs;
    }

    public void setAtcs(List<Atc> atcs) {
        this.atcs = atcs;
    }

    public boolean isNoAtc() {
        return noAtc;
    }

    public void setNoAtc(boolean noAtc) {
        this.noAtc = noAtc;
    }

    public AdminRoute getAdminRoute() {
        return adminRoute;
    }

    public void setAdminRoute(AdminRoute adminRoute) {
        this.adminRoute = adminRoute;
    }

    public PharmClassif getPharmClassif() {
        return pharmClassif;
    }

    public void setPharmClassif(PharmClassif pharmClassif) {
        this.pharmClassif = pharmClassif;
    }

    public List<ProdCompany> getProdCompanies() {
        return prodCompanies;
    }

    public void setProdCompanies(List<ProdCompany> prodCompanies) {
        this.prodCompanies = prodCompanies;
    }

    public String getIngrdStatment() {
        return ingrdStatment;
    }

    public void setIngrdStatment(String ingrdStatment) {
        this.ingrdStatment = ingrdStatment;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public ProdDrugType getDrugType() {
        return drugType;
    }

    public void setDrugType(ProdDrugType drugType) {
        this.drugType = drugType;
    }

    public List<UseCategory> getUseCategories() {
        return useCategories;
    }

    public void setUseCategories(List<UseCategory> useCategories) {
        this.useCategories = useCategories;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public String getContType() {
        return contType;
    }

    public void setContType(String contType) {
        this.contType = contType;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getStorageCndtn() {
        return storageCndtn;
    }

    public void setStorageCndtn(String storageCndtn) {
        this.storageCndtn = storageCndtn;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getPosology() {
        return posology;
    }

    public void setPosology(String posology) {
        this.posology = posology;
    }

    public List<ProdApplications> getProdApplicationses() {
        return prodApplicationses;
    }

    public void setProdApplicationses(List<ProdApplications> prodApplicationses) {
        this.prodApplicationses = prodApplicationses;
    }

    public String getFnm() {
        return fnm;
    }

    public void setFnm(String fnm) {
        this.fnm = fnm;
    }

    public String getPharmacopeiaStds() {
        return pharmacopeiaStds;
    }

    public void setPharmacopeiaStds(String pharmacopeiaStds) {
        this.pharmacopeiaStds = pharmacopeiaStds;
    }
}

