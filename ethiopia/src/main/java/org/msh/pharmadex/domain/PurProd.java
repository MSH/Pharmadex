package org.msh.pharmadex.domain;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pur_prod")
public class PurProd extends PProdBase {

    @OneToOne
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purorder_id")
    private PurOrder purOrder;

    public PurProd() {
    }

    public PurProd(DosageForm dosForm, DosUom dosUnit, PurOrder purOrder, String currCD) {
        super(dosForm, dosUnit);
        this.purOrder = purOrder;
        this.setCurrency(currCD);
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
