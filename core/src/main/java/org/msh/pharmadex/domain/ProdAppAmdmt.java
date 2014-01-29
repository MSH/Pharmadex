package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AmdmtState;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "prodapp_amdmt")
public class ProdAppAmdmt extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "amdmt_category")
    private AmdmtCategory amdmtCategory;

    @ManyToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @Column(length = 500, name = "amdmtDesc")
    private String amdmtDesc;

    @Column(name = "approved")
    private boolean approved;

    @Column(name = "amdmt_state")
    @Enumerated(EnumType.STRING)
    private AmdmtState amdmtState;

    @Column(length = 500, name = "staff_comment")
    private String staffComment;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    private boolean fileUploaded;

    @OneToOne
    private User uploadedBy;

    @OneToOne
    private User approvedBy;

    @OneToOne
    private User submittedBy;

    @Column(length = 100, nullable = true)
    private String contentType;

    @Column(length = 100, nullable = true)
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AmdmtCategory getAmdmtCategory() {
        return amdmtCategory;
    }

    public void setAmdmtCategory(AmdmtCategory amdmtCategory) {
        this.amdmtCategory = amdmtCategory;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public String getAmdmtDesc() {
        return amdmtDesc;
    }

    public void setAmdmtDesc(String desc) {
        this.amdmtDesc = desc;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getStaffComment() {
        return staffComment;
    }

    public void setStaffComment(String staffComment) {
        this.staffComment = staffComment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    public AmdmtState getAmdmtState() {
        return amdmtState;
    }

    public void setAmdmtState(AmdmtState amdmtState) {
        this.amdmtState = amdmtState;
    }
}
