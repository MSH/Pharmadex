package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pur_order")
public class PurOrder extends POrderBase{

    @OneToMany(mappedBy = "purOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PurProd> purProds;

    @OneToMany(mappedBy = "purOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<POrderComment> pOrderComments;

    @OneToMany(mappedBy = "purOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<POrderChecklist> pOrderChecklists;

    public PurOrder(Currency currency) {
        this.setCurrency(currency);
    }

    public PurOrder() {
    }

    public List<POrderChecklist> getpOrderChecklists() {
        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }

    public List<PurProd> getPurProds() {
        return purProds;
    }

    public void setPurProds(List<PurProd> purProds) {
        this.purProds = purProds;
    }

    public List<POrderComment> getpOrderComments() {
        return pOrderComments;
    }

    public void setpOrderComments(List<POrderComment> pOrderComments) {
        this.pOrderComments = pOrderComments;
    }
}
