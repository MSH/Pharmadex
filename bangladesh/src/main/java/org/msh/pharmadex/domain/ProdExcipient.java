package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class ProdExcipient extends CreationDetail implements Serializable {

	private static final long serialVersionUID = 6597026660123168100L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(name = "dosage_strength", length = 255)
    private String dosStrength;

    @OneToOne
    @JoinColumn(name = "DOSUNIT_ID")
    private DosUom dosUnit = new DosUom();

    @Column(length = 255, nullable = true)
    private String function;

    @Column(length = 255, nullable = true)
    private String RefStd;

    @OneToOne
    @JoinColumn(name = "EXPNT_ID")
    private Excipient excipient;
    
    @OneToOne
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id", nullable = false)
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDosStrength() {
        return dosStrength;
    }

    public void setDosStrength(String dosStrength) {
        this.dosStrength = dosStrength;
    }

    public DosUom getDosUnit() {
        return dosUnit;
    }

    public void setDosUnit(DosUom dosUnit) {
        this.dosUnit = dosUnit;
    }

    public String getRefStd() {
        return RefStd;
    }

    public void setRefStd(String refStd) {
        RefStd = refStd;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Excipient getExcipient() {
        return excipient;
    }

    public void setExcipient(Excipient excipient) {
        this.excipient = excipient;
    }

    public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
