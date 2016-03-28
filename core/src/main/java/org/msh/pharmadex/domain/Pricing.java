package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "pricing")
public class Pricing extends CreationDetail implements Serializable {

    private static final long serialVersionUID = -7523541197529598604L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;


    @Column(length = 255)
    private String msrp;

    @Column(length = 255)
    private String pricePerDose;

    @Column(length = 255)
    private String pricePerDay;

    @Column(length = 255)
    private String treatCost;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "PROD_ID")
    private Product product;

    @OneToMany(mappedBy = "pricing", cascade = CascadeType.ALL)
    private List<DrugPrice> drugPrices;

    public Pricing() {
    }

    public Pricing(List<DrugPrice> drugPrices) {
        this.drugPrices = drugPrices;
    }

    public Pricing(List<DrugPrice> drugPrices, Product product) {
        this.drugPrices = drugPrices;
        this.product = product;
    }

    public List<DrugPrice> getDrugPrices() {
        return drugPrices;
    }

    public void setDrugPrices(List<DrugPrice> drugPrices) {
        this.drugPrices = drugPrices;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsrp() {
        return msrp;
    }

    public void setMsrp(String msrp) {
        this.msrp = msrp;
    }

    public String getPricePerDose() {
        return pricePerDose;
    }

    public void setPricePerDose(String pricePerDose) {
        this.pricePerDose = pricePerDose;
    }

    public String getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(String pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getTreatCost() {
        return treatCost;
    }

    public void setTreatCost(String treatCost) {
        this.treatCost = treatCost;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
