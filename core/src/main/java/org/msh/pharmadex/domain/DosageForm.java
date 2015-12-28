package org.msh.pharmadex.domain;

import org.msh.pharmadex.util.RegistrationUtil;

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

	@Column(name = "dosageform", length = 255, nullable = false)
	private String dosForm;

    @Column(name = "Discontinued")
    private boolean inactive;

    @Column(name = "sampleSize")
    private Integer sampleSize;

    @Transient
    private String key;

    public String getKey() {
        String delimiter = "_";
        key = this.getClass().getSimpleName()+delimiter+ RegistrationUtil.formatString(getDosForm());
        return key;
    }

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

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    @Override
    public String toString() {
        return ""+getUid();
    }
}
