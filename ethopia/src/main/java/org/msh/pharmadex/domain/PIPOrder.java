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
@Table(name = "pip_order")
public class PIPOrder extends POrderBase {
    @OneToMany(mappedBy = "pipOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PIPProd> pipProds;
    @OneToMany(mappedBy = "pipOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<POrderComment> pOrderComments;
    @OneToMany(mappedBy = "pipOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<POrderChecklist> pOrderChecklists;

    public PIPOrder(Currency currency) {
        this.setCurrency(currency);
    }

    public PIPOrder() {
    }

    public List<POrderChecklist> getpOrderChecklists() {
        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }

    public List<PIPProd> getPipProds() {
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

    public List<POrderComment> getpOrderComments() {
        return pOrderComments;
    }

    public void setpOrderComments(List<POrderComment> pOrderComments) {
        this.pOrderComments = pOrderComments;
    }
}
