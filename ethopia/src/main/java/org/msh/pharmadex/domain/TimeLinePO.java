package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@DiscriminatorValue(value="PO")
public class TimeLinePO extends TimeLineBase implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_APP_ID", nullable = false)
    private PurOrder prodApplications;

    public PurOrder getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(PurOrder prodApplications) {
        this.prodApplications = prodApplications;
    }
}
