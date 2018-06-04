package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by usrivastava on 01/17/2015.
 */
@Entity
@Table(name = "piporder_lookup")
public class PIPOrderLookUp extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    private String no;

    private String name;

    private boolean pip;

    private boolean header;

    private boolean optional;

    @OneToOne(fetch = FetchType.LAZY)
    private ApplicantType applicantType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPip() {
        return pip;
    }

    public void setPip(boolean pip) {
        this.pip = pip;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public ApplicantType getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(ApplicantType applicantType) {
        this.applicantType = applicantType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
