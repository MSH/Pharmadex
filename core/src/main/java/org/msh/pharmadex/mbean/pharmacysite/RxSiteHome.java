package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.PharmacySite;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class RxSiteHome implements Serializable {

    private PharmacySite site;

    public PharmacySite getSite() {
        return site;
    }

    public void setSite(PharmacySite site) {
        this.site = site;
    }
}
