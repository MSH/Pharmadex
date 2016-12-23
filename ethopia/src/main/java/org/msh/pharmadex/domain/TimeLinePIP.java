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
        name="discriminator",
        discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue(value="PIP")
public class TimeLinePIP extends TimeLineBase implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_APP_ID", nullable = false)
    private PIPOrder prodApplications;

    public PIPOrder getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(PIPOrder prodApplications) {
        this.prodApplications = prodApplications;
    }
}
