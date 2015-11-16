package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public class PProdBase extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = true)
    private String productNo;

    @Column(length = 255, nullable = false)
    private String productName;

    @Column(length = 255, nullable = true)
    private String productDesc;

    @Column(length = 500, nullable = true)
    private String manufName;

    @Column(length = 500, nullable = true)
    private String manufSite;

    @ManyToOne
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOSFORM_ID")
    private DosageForm dosForm;

    @Column(name = "dosage_strength")
    private String dosStrength;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOSUNIT_ID")
    private DosUom dosUnit;

    @Column(length = 500)
    private String shelfLife;

    private Long quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double freight;

    @Column(length = 255)
    private Double totalPrice;

    public PProdBase(DosageForm dosForm, DosUom dosUnit) {
        this.dosForm = dosForm;
        this.dosUnit = dosUnit;
    }

    public PProdBase() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
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

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public String getManufSite() {
        return manufSite;
    }

    public void setManufSite(String manufSite) {
        this.manufSite = manufSite;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
