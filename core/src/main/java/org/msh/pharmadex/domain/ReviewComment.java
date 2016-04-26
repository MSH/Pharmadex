package org.msh.pharmadex.domain;


import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.SuspendDecisionType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
public class ReviewComment implements Serializable
{
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	private String comment;

	@ManyToOne
	@JoinColumn(name="reviewinfo_ID", nullable = true)
    private ReviewInfo reviewInfo;

    @ManyToOne (fetch = FetchType.LAZY)
   	@JoinColumn(name="userId", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
   	@Column(name="comment_date")
   	private Date date;
   @Enumerated(EnumType.STRING)
    private RecomendType recomendType;

    private boolean finalSummary;

    public ReviewComment() {
    }

    public ReviewComment(ReviewInfo reviewInfo, User user, RecomendType recomendType) {
        this.reviewInfo = reviewInfo;
        this.user = user;
        this.recomendType = recomendType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFinalSummary() {
        return finalSummary;
    }

    public void setFinalSummary(boolean finalSummary) {
        this.finalSummary = finalSummary;
    }

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }


}
