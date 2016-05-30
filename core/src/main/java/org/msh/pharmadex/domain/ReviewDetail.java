/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.domain;

import javax.persistence.*;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "review_detail")
public class ReviewDetail extends CreationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewquest_id", nullable = false)
    private ReviewQuestion reviewQuestions;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "review_info_id", nullable = false)
    private ReviewInfo reviewInfo;

    @Column(name = "satisfactory")
    private boolean satifactory;

    @Column(name = "volume")
    private String volume;

    @Lob
    @Column(name = "no_reason")
    private String noReason;

    @Lob
    @Column(name = "other_comment")
    private String otherComment;

    @Lob
    @Column(name = "sec_comment")
    private String secComment;

    @Column(name = "answered")
    private boolean answered;

    @Lob
    @Column(nullable = true)
    private byte[] file;
    
    @Column(name = "filename")
    private String filename;

    public ReviewDetail() {
    }

    public ReviewDetail(ReviewQuestion reviewQuestions, ReviewInfo reviewInfo, boolean satifactory, String noReason, String otherComment, boolean answered) {
        this.reviewQuestions = reviewQuestions;
        this.reviewInfo = reviewInfo;
        this.satifactory = satifactory;
        this.noReason = noReason;
        this.otherComment = otherComment;
        this.answered = answered;
    }

    public ReviewDetail(ReviewQuestion reviewQuestions, ReviewInfo reviewInfo, boolean answered) {
        this.reviewQuestions = reviewQuestions;
        this.reviewInfo = reviewInfo;
        this.answered = answered;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public ReviewQuestion getReviewQuestions() {
        return reviewQuestions;
    }

    public void setReviewQuestions(ReviewQuestion reviewQuestions) {
        this.reviewQuestions = reviewQuestions;
    }

    public boolean isSatifactory() {
        return satifactory;
    }

    public void setSatifactory(boolean satifactory) {
        this.satifactory = satifactory;
    }

    public String getNoReason() {
        return noReason;
    }

    public void setNoReason(String noReason) {
        this.noReason = noReason;
    }

    public String getOtherComment() {
        return otherComment;
    }

    public void setOtherComment(String otherComment) {
        this.otherComment = otherComment;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getSecComment() {
        return secComment;
    }

    public void setSecComment(String secComment) {
        this.secComment = secComment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
