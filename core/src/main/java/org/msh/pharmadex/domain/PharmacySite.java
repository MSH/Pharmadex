package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.ApplicantState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pharmacy_site")
public class PharmacySite extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -8795715499459500103L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String pharmacyName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1", column = @Column(name = "site_address1")),
            @AttributeOverride(name = "address2", column = @Column(name = "site_address2")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "site_zipcode")),
            @AttributeOverride(name = "country", column = @Column(name = "site_country"))
    })
    private Address siteAddress = new Address();

    @Column(length = 255, nullable = false)
    private String applicantName;

    @Column(length = 255, nullable = false)
    private String applicantQualif;

    @Column(length = 255, nullable = false)
    private String estPopulation;

    @Column(length = 255)
    private String targetArea;

    @Column(length = 255)
    private String phoneNo;

    @Column(length = 255)
    private String faxNo;

    private String email;

    @Column(length = 255)
    private String website;

    @Enumerated(EnumType.STRING)
    private ApplicantState state;

    @Column(length = 255)
    private String fileNumber;

    @Column(length = 500)
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @OneToMany(mappedBy = "pharmacySite", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PharmacySiteChecklist> pharmacySiteChecklists;

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinTable(name = "pharmacy_site_user", joinColumns = @JoinColumn(name = "site_id"), inverseJoinColumns = @JoinColumn(name = "userId"))
    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public ApplicantState getState() {
        return state;
    }

    public void setState(ApplicantState state) {
        this.state = state;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Address getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(Address applicantAddress) {
        this.siteAddress = applicantAddress;
    }

    public String getApplicantQualif() {
        return applicantQualif;
    }

    public void setApplicantQualif(String applicantQualif) {
        this.applicantQualif = applicantQualif;
    }

    public String getEstPopulation() {
        return estPopulation;
    }

    public void setEstPopulation(String estPopulation) {
        this.estPopulation = estPopulation;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public List<PharmacySiteChecklist> getPharmacySiteChecklists() {
        return pharmacySiteChecklists;
    }

    public void setPharmacySiteChecklists(List<PharmacySiteChecklist> pharmacySiteChecklists) {
        this.pharmacySiteChecklists = pharmacySiteChecklists;
    }
}
