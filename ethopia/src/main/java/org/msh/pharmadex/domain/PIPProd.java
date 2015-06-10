package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

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

    public PIPProd(DosageForm dosForm, DosUom dosUnit, PIPOrder pipOrder) {
        super(dosForm, dosUnit);
        this.pipOrder = pipOrder;
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
