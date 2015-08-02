package org.msh.pharmadex.domain.lab;

import org.msh.pharmadex.domain.CreationDetail;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.SampleTestStatus;

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

    @Enumerated(EnumType.STRING)
    private SampleTestStatus sampleTestStatus;

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

    public SampleTest() {
    }

    public SampleTest(List<SampleComment> sampleComments) {
        this.sampleComments = sampleComments;
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
}
