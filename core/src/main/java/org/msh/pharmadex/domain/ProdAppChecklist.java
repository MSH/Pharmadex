package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.YesNoNA;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class ProdAppChecklist extends CreationDetail implements Serializable {


    private static final long serialVersionUID = -5281719332174386609L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @Enumerated(EnumType.STRING)
    private YesNoNA value;

    @Enumerated(EnumType.STRING)
    private YesNoNA staffValue;

    @Column(length = 500)
    private String staffComment;

    private boolean sendToApp;

    @Column(length = 500)
    private String appRemark;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    private boolean fileUploaded;

    @OneToOne
    private User uploadedBy;

    @Column(length = 255, nullable = true)
    private String contentType;

    @Column(length = 255, nullable = true)
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

    public String getAppRemark() {
        return appRemark;
    }

    public void setAppRemark(String appRemark) {
        this.appRemark = appRemark;
    }

    public boolean isSendToApp() {

        return sendToApp;
    }

    public void setSendToApp(boolean sendToApp) {
        this.sendToApp = sendToApp;
    }

    public YesNoNA getValue() {
        return value;
    }

    public void setValue(YesNoNA value) {
        this.value = value;
    }

    public YesNoNA getStaffValue() {
        return staffValue;
    }

    public void setStaffValue(YesNoNA staffValue) {
        this.staffValue = staffValue;
    }
}
