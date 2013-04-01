package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ApplicantHome implements Serializable {

    private Applicant applicant;

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
