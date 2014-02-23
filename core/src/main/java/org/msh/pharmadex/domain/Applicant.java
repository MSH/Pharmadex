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
@Table(name = "applicant")
public class Applicant extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -9020561842927501066L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long applcntId;

    @Column(length = 200, nullable = false)
    private String appName;

    @Embedded
    private Address address = new Address();

    @OneToMany
    private List<ApplicantType> applicantTypes;


    @Column(length = 50, nullable = false)
    private String contactName;

    @Column(length = 30)
    private String phoneNo;

    @Column(length = 30)
    private String faxNo;

    private String email;

    @Column(length = 50)
    private String website;

    @Enumerated
    private ApplicantState state;

    @Column(length = 50)
    private String fileNumber;

    @Column(length = 50)
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;


    @OneToMany(mappedBy = "applicant", cascade = {CascadeType.ALL})
    private List<User> users;

    @OneToMany(mappedBy = "applicant", cascade = {CascadeType.ALL})
    private List<Product> products;

    public Long getApplcntId() {
        return applcntId;
    }

    public void setApplcntId(Long applcntId) {
        this.applcntId = applcntId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public List<ApplicantType> getApplicantTypes() {
        return applicantTypes;
    }

    public void setApplicantTypes(List<ApplicantType> applicantTypes) {
        this.applicantTypes = applicantTypes;
    }
}
