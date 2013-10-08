package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "dosform")
public class DosageForm implements Serializable {


    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long uid;

	@Column(name = "dosageform", length = 200, nullable = false)
	private String dosForm;

    @Column(name = "Discontinued")
    private boolean inactive;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDosForm() {
        return dosForm;
    }

    public void setDosForm(String dosForm) {
        this.dosForm = dosForm;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
}
