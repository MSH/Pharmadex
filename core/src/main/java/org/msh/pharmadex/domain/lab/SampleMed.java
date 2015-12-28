package org.msh.pharmadex.domain.lab;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.SampleTestStatus;
import org.msh.pharmadex.domain.enums.SampleType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Author: usrivastava
 */
@Entity
@Table(name = "sample_med")
public class SampleMed extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    private SampleTest sampleTest;

    @OneToOne
    private Product product;

    @Column(length = 255)
    private String batchNo;

    @Column(name = "manuf_name", length = 255)
    private String manufName;

    @Column(name="quantity", length = 255)
    private String quantity;

    @OneToOne
    private org.msh.pharmadex.domain.Country country;

    @Column(name = "manuf_date")
    @Temporal(TemporalType.DATE)
    private Date manufDate;

    @Column(name = "exp_date")
    @Temporal(TemporalType.DATE)
    private Date expDate;

    @Column(name="comment", length = 255)
    private String comment;

    public SampleMed(SampleTest sampleTest, User loggedInUser, Country country) {
        this.sampleTest = sampleTest;
        this.setCreatedBy(loggedInUser);
        this.country = country;
    }

    public SampleMed() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Date getManufDate() {
        return manufDate;
    }

    public void setManufDate(Date manufDate) {
        this.manufDate = manufDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
