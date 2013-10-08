package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "pharmacy_checklist")
public class PharmacySiteChecklist extends CreationDetail implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitechecklist_id")
    private SiteChecklist siteChecklist;

    @ManyToOne
    @JoinColumn(name = "pharmacysite_id", nullable = false)
    private PharmacySite pharmacySite;

    private boolean value;

    private boolean staffValue;

    @Column(length = 100)
    private String staffComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SiteChecklist getSiteChecklist() {
        return siteChecklist;
    }

    public void setSiteChecklist(SiteChecklist siteChecklist) {
        this.siteChecklist = siteChecklist;
    }

    public PharmacySite getPharmacySite() {
        return pharmacySite;
    }

    public void setPharmacySite(PharmacySite pharmacySite) {
        this.pharmacySite = pharmacySite;
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
}
