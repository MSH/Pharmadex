package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.lab.SampleTest;

import javax.persistence.*;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "prodapp_letter")
public class ProdAppLetter extends CreationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String fileName;

    @Column(length = 500, nullable = false)
    private String contentType;

    @Column(length = 500)
    private String comment;

    @OneToOne
    private ProdApplications prodApplications;

    @OneToOne
    private ReviewInfo reviewInfo;

    @ManyToOne
    private SampleTest sampleTest;

    @ManyToOne
    private SuspDetail suspDetail;

    @OneToOne
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    private RegState regState;

    @Enumerated(EnumType.STRING)
    private LetterType letterType;

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

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public LetterType getLetterType() {
        return letterType;
    }

    public void setLetterType(LetterType letterType) {
        this.letterType = letterType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public SuspDetail getSuspDetail() {
        return suspDetail;
    }

    public void setSuspDetail(SuspDetail suspDetail) {
        this.suspDetail = suspDetail;
    }


}
