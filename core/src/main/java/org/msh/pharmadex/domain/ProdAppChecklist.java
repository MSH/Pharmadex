package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class ProdAppChecklist extends CreationDetail implements Serializable {


    private static final long serialVersionUID = -5281719332174386609L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;

    @ManyToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    private boolean value;

    private boolean staffValue;

    @Column(length = 100)
    private String staffComment;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    private boolean fileUploaded;

    @OneToOne
    private User uploadedBy;

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

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isStaffValue() {
        return staffValue;
    }

    public void setStaffValue(boolean staffValue) {
        this.staffValue = staffValue;
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
}