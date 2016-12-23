package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "timeline")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue(value="PA")
public class TimeLine extends TimeLineBase implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_APP_ID", nullable = false)
    private ProdApplications prodApplications;

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

}
