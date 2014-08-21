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
@Table(name = "prod_company")
public class ProdCompany extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="prod_id", nullable = false)
    private Product product;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    public ProdCompany() {
    }

    public ProdCompany(Product product, Company company, CompanyType companyType) {
        this.product = product;
        this.company = company;
        this.companyType = companyType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }
}
