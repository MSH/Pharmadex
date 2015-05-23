package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.CTDModule;
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
@Table(name = "review_info")
public class ReviewInfo implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @OneToMany(mappedBy = "reviewInfo", cascade = {CascadeType.ALL})
    private List<ReviewDetail> reviewDetails;

    @OneToMany(mappedBy = "reviewInfo", cascade = {CascadeType.ALL})
    private List<ReviewComment> reviewComments;

    @OneToMany(mappedBy = "reviewInfo", cascade = {CascadeType.ALL})
    private List<RevDeficiency> revDeficiencies;

    @Enumerated(EnumType.STRING)
    private RecomendType recomendType;

    @Temporal(TemporalType.DATE)
    private Date assignDate;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    @Lob
    @Column(name = "comment")
    private String comment;

    @Lob
    @Column(name = "exec_summary")
    private String execSummary;

    @Lob
    @Column(name = "modcomment")
    private String modcomment;

    @Enumerated(EnumType.STRING)
    private CTDModule ctdModule;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    private boolean submitted;


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

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

//    public RecomendType getRecomendType() {
//        return recomendType;
//    }
//
//    public void setRecomendType(RecomendType recomendType) {
//        this.recomendType = recomendType;
//    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExecSummary() {
        return execSummary;
    }

    public void setExecSummary(String execSummary) {
        this.execSummary = execSummary;
    }

    public CTDModule getCtdModule() {
        return ctdModule;
    }

    public void setCtdModule(CTDModule ctdModule) {
        this.ctdModule = ctdModule;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public List<ReviewDetail> getReviewDetails() {
        return reviewDetails;
    }

    public void setReviewDetails(List<ReviewDetail> reviewDetails) {
        this.reviewDetails = reviewDetails;
    }

    public String getModcomment() {
        return modcomment;
    }

    public void setModcomment(String modcomment) {
        this.modcomment = modcomment;
    }

    public boolean isSubmitted() {
        if (reviewStatus.equals(ReviewStatus.SUBMITTED))
            submitted = true;
        else
            submitted = false;
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<ReviewComment> getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(List<ReviewComment> reviewComments) {
        this.reviewComments = reviewComments;
    }

    public List<RevDeficiency> getRevDeficiencies() {
        return revDeficiencies;
    }

    public void setRevDeficiencies(List<RevDeficiency> revDeficiencies) {
        this.revDeficiencies = revDeficiencies;
    }

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }
}
