package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "attachment")
public class Attachment extends CreationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String contentType;

    @Column(length = 500, nullable = false)
    private String fileName;

    @Column(length = 500)
    private String comment;

    @OneToOne
    private ProdApplications prodApplications;

    @OneToOne
    private ReviewInfo reviewInfo;

    @OneToOne
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    private RegState regState;

    @Lob
    @Column(nullable = false)
    private byte[] file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }
}
