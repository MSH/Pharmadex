package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "drug_price")
public class DrugPrice extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -5062448315278401357L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(nullable = false)
    private String drugName;

    @Column(nullable = false)
    private String msrp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_id", nullable = false)
    private Pricing pricing;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String durgName) {
        this.drugName = durgName;
    }

    public String getMsrp() {
        return msrp;
    }

    public void setMsrp(String msrp) {
        this.msrp = msrp;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }
}
