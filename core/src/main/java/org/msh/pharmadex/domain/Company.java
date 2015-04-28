package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.CompanyType;

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
@Table(name = "company", uniqueConstraints = @UniqueConstraint(columnNames = {"companyName",
        "siteNumber"}))
public class Company extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -3707427898846181650L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String companyName;

    @Embedded
    private Address address = new Address();

    @Column(length = 255)
    private String contactName;

    @Column(length = 255)
    private String phoneNo;

    @Column(length = 255)
    private String faxNo;

    @Column(length = 255, unique = true)
    private String siteNumber;

    private boolean gmpInsp;

    @Column(name = "gmp_insp_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmpInspDate;

    @Column(name ="gmp_cert_no", length = 500)
    private String gmpCertNo;

    public Company() {
    }

    public Company(String companyName) {
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String manufName) {
        this.companyName = manufName;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isGmpInsp() {
        return gmpInsp;
    }

    public void setGmpInsp(boolean gmpInsp) {
        this.gmpInsp = gmpInsp;
    }

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    public Date getGmpInspDate() {
        return gmpInspDate;
    }

    public void setGmpInspDate(Date gmpInspDate) {
        this.gmpInspDate = gmpInspDate;
    }

    public String getGmpCertNo() {
        return gmpCertNo;
    }

    public void setGmpCertNo(String gmpCertNo) {
        this.gmpCertNo = gmpCertNo;
    }

    @Override
    public String toString(){
        return getCompanyName();
    }
}
