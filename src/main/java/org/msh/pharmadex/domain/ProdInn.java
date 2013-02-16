package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class ProdInn extends CreationDetail implements Serializable {


    private static final long serialVersionUID = -5710089544366537006L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(length = 100, nullable = true)
    private String quantity;

    @Column(length = 20, nullable = true)
    private String uom;

    @Column(length = 100, nullable = true)
    private String RefStd;

    @OneToOne
    @JoinColumn(name="INN_ID")
    private Inn inn;

    @ManyToOne
    @JoinColumn(name="prod_id", nullable = false)
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getRefStd() {
        return RefStd;
    }

    public void setRefStd(String refStd) {
        RefStd = refStd;
    }

    public Inn getInn() {
        return inn;
    }

    public void setInn(Inn inn) {
        this.inn = inn;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
