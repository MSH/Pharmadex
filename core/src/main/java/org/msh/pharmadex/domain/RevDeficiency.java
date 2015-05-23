package org.msh.pharmadex.domain;


import org.msh.pharmadex.domain.enums.RecomendType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
public class RevDeficiency implements Serializable
{
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
    private ReviewComment sentComment;

    @ManyToOne(cascade = CascadeType.ALL)
    private ReviewComment ackComment;

    @ManyToOne(cascade = CascadeType.ALL)
    private ProdAppLetter prodAppLetter;

	@ManyToOne
	@JoinColumn(name="reviewinfo_ID", nullable = true)
    private ReviewInfo reviewInfo;

    @ManyToOne (fetch = FetchType.LAZY)
   	@JoinColumn(name="userId", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
   	@Column(name="created_date")
   	private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ack_date")
    private Date ackDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="due_date")
    private Date dueDate;

    private boolean resolved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReviewComment getSentComment() {
        return sentComment;
    }

    public void setSentComment(ReviewComment sentComment) {
        this.sentComment = sentComment;
    }

    public ReviewComment getAckComment() {
        return ackComment;
    }

    public void setAckComment(ReviewComment ackComment) {
        this.ackComment = ackComment;
    }

    public ProdAppLetter getProdAppLetter() {
        return prodAppLetter;
    }

    public void setProdAppLetter(ProdAppLetter prodAppLetter) {
        this.prodAppLetter = prodAppLetter;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getAckDate() {
        return ackDate;
    }

    public void setAckDate(Date ackDate) {
        this.ackDate = ackDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
