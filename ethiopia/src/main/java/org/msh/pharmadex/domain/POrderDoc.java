package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.util.StrTools;

import javax.persistence.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "porder_doc")
public class POrderDoc extends CreationDetail {

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

    @ManyToOne
    private PIPOrder pipOrder;

    @ManyToOne
    private PurOrder purOrder;

    @OneToOne
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    private AmdmtState regState;

    @Lob
    @Column(nullable = false)
    private byte[] file;

    private String fileNameOnly;

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

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
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

    public AmdmtState getRegState() {
        return regState;
    }

    public void setRegState(AmdmtState regState) {
        this.regState = regState;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }

    public String getFileNameOnly() {
        if (!StrTools.isEmptyString(fileName)){
            Path path = Paths.get(fileName);
            fileNameOnly = path.getFileName().toString();
        }else{
            fileNameOnly = fileName;
        }
        return fileNameOnly;
    }

    public void setFileNameOnly(String fileNameOnly) {
        this.fileNameOnly = fileNameOnly;
    }
}
