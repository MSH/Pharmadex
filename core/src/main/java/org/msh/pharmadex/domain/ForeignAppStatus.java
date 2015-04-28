package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.ForeignAppStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by utkarsh on 10/2/14.
 */
@Entity
@Table(name = "foreign_appl_status")
public class ForeignAppStatus extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CNTRY_ID")
    private Country country;

    @Column(name = "prodName", length = 255)
    private String prodName;

    @Column(name = "mktAuthHolder", length = 255)
    private String mktAuthHolder;

    @Column(name = "mktAuthCert", length = 255)
    private String mktAuthCert;

    @Temporal(TemporalType.DATE)
    private Date mktAuthDate;

    @Enumerated(EnumType.STRING)
    private ForeignAppStatusType foreignAppStatusType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_APP_ID", nullable = false)
    private ProdApplications prodApplications;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getMktAuthHolder() {
        return mktAuthHolder;
    }

    public void setMktAuthHolder(String mktAuthHolder) {
        this.mktAuthHolder = mktAuthHolder;
    }

    public Date getMktAuthDate() {
        return mktAuthDate;
    }

    public void setMktAuthDate(Date mktAuthDate) {
        this.mktAuthDate = mktAuthDate;
    }

    public ForeignAppStatusType getForeignAppStatusType() {
        return foreignAppStatusType;
    }

    public void setForeignAppStatusType(ForeignAppStatusType foreignAppStatusType) {
        this.foreignAppStatusType = foreignAppStatusType;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public String getMktAuthCert() {
        return mktAuthCert;
    }

    public void setMktAuthCert(String mktAuthCert) {
        this.mktAuthCert = mktAuthCert;
    }
}
