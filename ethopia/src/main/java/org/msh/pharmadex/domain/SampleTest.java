package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.ReviewStatus;

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
    private User user;

    @Column
    private boolean letterGenerated;

    @Column
    private boolean letterSent;

    @Column
    private boolean sampleRecieved;

    @Temporal(TemporalType.DATE)
    private Date sampleRecievedDt;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    @Transient
    private boolean submitted;


    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public boolean isSubmitted() {
        if (file!=null)
            submitted = true;
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLetterGenerated() {
        return letterGenerated;
    }

    public void setLetterGenerated(boolean letterGenerated) {
        this.letterGenerated = letterGenerated;
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

    public Date getSampleRecievedDt() {
        return sampleRecievedDt;
    }

    public void setSampleRecievedDt(Date sampleRecievedDt) {
        this.sampleRecievedDt = sampleRecievedDt;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }
}
