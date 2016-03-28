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
@Table(name = "pip_prod")
public class PIPProd extends PProdBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piporder_id")
    private PIPOrder pipOrder;

    public PIPProd(DosageForm dosForm, DosUom dosUnit, PIPOrder pipOrder, String curr) {
        super(dosForm, dosUnit);
        this.pipOrder = pipOrder;
        this.setCurrency(curr);
    }

    public PIPProd() {
    }

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

}
