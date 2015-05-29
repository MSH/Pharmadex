package org.msh.pharmadex.domain.lab;

import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RecomendType;
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
public class SampleTest implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    private ProdApplications prodApplications;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private ProdAppLetter prodAppLetter;

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

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ProdAppLetter getProdAppLetter() {
        return prodAppLetter;
    }

    public void setProdAppLetter(ProdAppLetter prodAppLetter) {
        this.prodAppLetter = prodAppLetter;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
