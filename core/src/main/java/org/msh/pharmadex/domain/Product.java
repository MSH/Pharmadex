package org.msh.pharmadex.domain;

import org.hibernate.envers.Audited;
import org.msh.pharmadex.domain.enums.AdminRoute;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.ProdType;
import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
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

    @Column(name = "prod_name", length = 100)
    private String prodName;

    @Column(name = "apprvd_name", length = 100)
    private String apprvdName;

    @Column(name = "prod_desc", length = 200)
    private String prodDesc;

    @Column(name = "gen_name", length = 150)
    private String genName;

    @OneToOne
    @JoinColumn(name = "DOSFORM_ID")
    private DosageForm dosForm;

    @Column(name = "dosage_strength")
    private Double dosStrength;

    @OneToOne
    @JoinColumn(name = "DOSUNIT_ID")
    private DosUom dosUnit;

    @Column(name = "lic_no", length = 50)
    private String licNo;

    @Column(name = "prod_type")
    @Enumerated(EnumType.STRING)
    private ProdType prodType;

    @Column(name = "prod_cat")
    @Enumerated(EnumType.STRING)
    private ProdCategory prodCategory;

    @Column(name = "reg_no", length = 100)
    private String regNo;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<ProdInn> inns;

    @ManyToMany(targetEntity = Atc.class, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "prod_atc", joinColumns = @JoinColumn(name = "prod_id"), inverseJoinColumns = @JoinColumn(name = "atc_id"))
    private List<Atc> atcs;

    private boolean noAtc;

    @Enumerated
    private AdminRoute adminRoute;

    @OneToOne
    @JoinColumn(name = "PHARM_CLASSIF_ID")
    private PharmClassif pharmClassif;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "APP_ID", nullable = false)
    private Applicant applicant;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<Company> companies;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_APP_ID")
    private ProdApplications prodApplications;

    @OneToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @Enumerated
    private RegState regState;

    public Product() {
    }

    public Product(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
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

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }

    public Double getDosStrength() {
        return dosStrength;
    }

    public void setDosStrength(Double dosStrength) {
        this.dosStrength = dosStrength;
    }

    public String getLicNo() {
        return licNo;
    }

    public void setLicNo(String licNo) {
        this.licNo = licNo;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public List<ProdInn> getInns() {
        return inns;
    }

    public void setInns(List<ProdInn> inns) {
        this.inns = inns;
    }

    public boolean isNoAtc() {
        return noAtc;
    }

    public void setNoAtc(boolean noAtc) {
        this.noAtc = noAtc;
    }

    public List<Atc> getAtcs() {
        return atcs;
    }

    public void setAtcs(List<Atc> atcs) {
        this.atcs = atcs;
    }

    public PharmClassif getPharmClassif() {
        return pharmClassif;
    }

    public void setPharmClassif(PharmClassif pharmClassif) {
        this.pharmClassif = pharmClassif;
    }

    public DosageForm getDosForm() {
        return dosForm;
    }

    public void setDosForm(DosageForm dosForm) {
        this.dosForm = dosForm;
    }

    public DosUom getDosUnit() {
        return dosUnit;
    }

    public void setDosUnit(DosUom dosUnit) {
        this.dosUnit = dosUnit;
    }

    public String getApprvdName() {
        return apprvdName;
    }

    public void setApprvdName(String apprvdName) {
        this.apprvdName = apprvdName;
    }

    public AdminRoute getAdminRoute() {
        return adminRoute;
    }

    public void setAdminRoute(AdminRoute adminRoute) {
        this.adminRoute = adminRoute;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public ProdType getProdType() {
        return prodType;
    }

    public void setProdType(ProdType prodType) {
        this.prodType = prodType;
    }

    public ProdCategory getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(ProdCategory prodCategory) {
        this.prodCategory = prodCategory;
    }
}
