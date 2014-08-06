package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.CompanyType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "company")
public class Company extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -3707427898846181650L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500, nullable = false)
    private String companyName;

    @Embedded
    private Address address = new Address();

    @Column(length = 255)
    private String contactName;

    @Column(length = 255)
    private String phoneNo;

    @Column(length = 255)
    private String faxNo;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Column(length = 500)
    private String reference;

    @ManyToMany(targetEntity = Product.class, fetch = FetchType.LAZY)
    @JoinTable(name = "prod_company", joinColumns = @JoinColumn(name = "company_id"), inverseJoinColumns = @JoinColumn(name = "prod_id"))
    private List<Product> products;

    private boolean gmpInsp;

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

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isGmpInsp() {
        return gmpInsp;
    }

    public void setGmpInsp(boolean gmpInsp) {
        this.gmpInsp = gmpInsp;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
