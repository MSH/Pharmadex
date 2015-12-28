package org.msh.pharmadex.domain.lab;

import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.CreationDetail;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Author: usrivastava
 */
@Entity
@Table(name = "sample_std")
public class SampleStd extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    private SampleTest sampleTest;


    @Column(length = 255)
    private String batchNo;

    @Column(name = "manuf_name", length = 255)
    private String manufName;

    @Column(name="std_name", length = 255)
    private String stdName;

    @Column(name="std_type", length = 255)
    private String stdType;

    @Column(name="potency", length = 255)
    private String potency;

    @Column(name="quantity", length = 255)
    private String quantity;

    @OneToOne
    private Country country;

    @Column(name = "manuf_date")
    @Temporal(TemporalType.DATE)
    private Date manufDate;

    @Column(name = "exp_date")
    @Temporal(TemporalType.DATE)
    private Date expDate;

    @Column(name="comment", length = 255)
    private String comment;

    public SampleStd(SampleTest sampleTest, User loggedInUser, Country country) {
        this.sampleTest = sampleTest;
        this.setCreatedBy(loggedInUser);
        this.country = country;
    }

    public SampleStd() {
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

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getStdType() {
        return stdType;
    }

    public void setStdType(String stdType) {
        this.stdType = stdType;
    }

    public String getPotency() {
        return potency;
    }

    public void setPotency(String potency) {
        this.potency = potency;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
