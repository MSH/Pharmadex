package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AmdmtType;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "amdmt_category")
public class AmdmtCategory extends CreationDetail implements Serializable {


    private static final long serialVersionUID = -8621482999656256928L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private int id;

    @Column(name = "cat_code")
    private int categoryCD;

    @Column(name = "short_desc")
    private String shortDesc;

    @Column(name = "full_desc")
    private String fullDesc;

    @Column(name = "amdmt_type")
    @Enumerated(EnumType.STRING)
    private AmdmtType amdmtType;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryCD() {
        return categoryCD;
    }

    public void setCategoryCD(int categoryCD) {
        this.categoryCD = categoryCD;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getFullDesc() {
        return fullDesc;
    }

    public void setFullDesc(String fullDesc) {
        this.fullDesc = fullDesc;
    }

    public AmdmtType getAmdmtType() {
        return amdmtType;
    }

    public void setAmdmtType(AmdmtType amdmtType) {
        this.amdmtType = amdmtType;
    }
}
