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
@Table(name = "review")
public class Review implements Serializable {

    private static final long serialVersionUID = 4403558274641428489L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RecomendType recomendType;

    @Temporal(TemporalType.DATE)
    private Date assignDate;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    @OneToMany(mappedBy = "review", cascade = {CascadeType.ALL})
    private List<ReviewChecklist> reviewChecklists;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @Column(length = 1000)
    private String reviewerSummary;

    @Column(length = 1000)
    private String moderatorSummary;


    @Transient
    private boolean submitted;
    private String modComment;


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

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }

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

    public List<ReviewChecklist> getReviewChecklists() {
        return reviewChecklists;
    }

    public void setReviewChecklists(List<ReviewChecklist> reviewChecklists) {
        this.reviewChecklists = reviewChecklists;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public boolean isSubmitted() {
        if (reviewStatus.equals(ReviewStatus.SUBMITTED))
            submitted = true;
        return submitted;
    }


    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewerSummary() {
        return reviewerSummary;
    }

    public void setReviewerSummary(String reviewerSummary) {
        this.reviewerSummary = reviewerSummary;
    }

    public String getModeratorSummary() {
        return moderatorSummary;
    }

    public void setModeratorSummary(String moderatorSummary) {
        this.moderatorSummary = moderatorSummary;
    }

    public void setModComment(String modComment) {
        this.modComment = modComment;
    }

    public String getModComment() {
        return modComment;
    }
}
