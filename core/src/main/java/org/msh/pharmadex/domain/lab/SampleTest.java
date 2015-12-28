package org.msh.pharmadex.domain.lab;

import org.msh.pharmadex.domain.CreationDetail;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
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
@Table(name = "sample_test")
public class SampleTest extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    private ProdApplications prodApplications;

    @OneToMany(mappedBy = "sampleTest", cascade = {CascadeType.ALL})
    private List<ProdAppLetter> prodAppLetters;

    @OneToMany(mappedBy = "sampleTest", cascade = {CascadeType.ALL})
    private List<SampleComment> sampleComments;

    @OneToMany(mappedBy = "sampleTest", cascade = {CascadeType.ALL})
    private List<SampleMed> sampleMeds;

    @OneToMany(mappedBy = "sampleTest", cascade = {CascadeType.ALL})
    private List<SampleStd> sampleStds;

    @Enumerated(EnumType.STRING)
    private SampleTestStatus sampleTestStatus;

    @ElementCollection(targetClass = SampleType.class)
    @JoinTable(name = "tblsampletypes", joinColumns = @JoinColumn(name = "sampleTestID"))
    @Column(name = "sample_type")
    @Enumerated(EnumType.STRING)
    private List<SampleType> sampleTypes;

    @Column
    private boolean letterGenerated;

    @Temporal(TemporalType.DATE)
    private Date reqDt;

    @Column
    private boolean letterSent;

    @Column
    private boolean sampleRecieved;

    @Temporal(TemporalType.DATE)
    private Date recievedDt;

    @Temporal(TemporalType.DATE)
    private Date resultDt;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Column(length = 255)
    private String quantity;

    public SampleTest() {
    }

    public SampleTest(List<SampleComment> sampleComments) {
        this.sampleComments = sampleComments;
    }

    public SampleTest(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public List<SampleComment> getSampleComments() {
        return sampleComments;
    }

    public void setSampleComments(List<SampleComment> sampleComments) {
        this.sampleComments = sampleComments;
    }

    public SampleTestStatus getSampleTestStatus() {
        return sampleTestStatus;
    }

    public void setSampleTestStatus(SampleTestStatus sampleTestStatus) {
        this.sampleTestStatus = sampleTestStatus;
    }

    public boolean isLetterGenerated() {
        return letterGenerated;
    }

    public void setLetterGenerated(boolean letterGenerated) {
        this.letterGenerated = letterGenerated;
    }

    public Date getReqDt() {
        return reqDt;
    }

    public void setReqDt(Date reqDt) {
        this.reqDt = reqDt;
    }

    public boolean isLetterSent() {
        return letterSent;
    }

    public void setLetterSent(boolean letterSent) {
        this.letterSent = letterSent;
    }

    public boolean isSampleRecieved() {
        return sampleRecieved;
    }

    public void setSampleRecieved(boolean sampleRecieved) {
        this.sampleRecieved = sampleRecieved;
    }

    public Date getRecievedDt() {
        return recievedDt;
    }

    public void setRecievedDt(Date recievedDt) {
        this.recievedDt = recievedDt;
    }

    public Date getResultDt() {
        return resultDt;
    }

    public void setResultDt(Date resultDt) {
        this.resultDt = resultDt;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public List<ProdAppLetter> getProdAppLetters() {
        return prodAppLetters;
    }

    public void setProdAppLetters(List<ProdAppLetter> prodAppLetters) {
        this.prodAppLetters = prodAppLetters;
    }

    public List<SampleType> getSampleTypes() {
        return sampleTypes;
    }

    public void setSampleTypes(List<SampleType> sampleTypes) {
        this.sampleTypes = sampleTypes;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<SampleMed> getSampleMeds() {
        return sampleMeds;
    }

    public void setSampleMeds(List<SampleMed> sampleMeds) {
        this.sampleMeds = sampleMeds;
    }

    public List<SampleStd> getSampleStds() {
        return sampleStds;
    }

    public void setSampleStds(List<SampleStd> sampleStds) {
        this.sampleStds = sampleStds;
    }
}
