package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by utkarsh on 1/7/15.
 * User: usrivastava
 * Date: 1/7/15
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "fasttrack_med")
public class FastTrackMed extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255)
    private String genMed;

    @Column(length = 255)
    private String theraGroup;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenMed() {
        return genMed;
    }

    public void setGenMed(String genMed) {
        this.genMed = genMed;
    }

    public String getTheraGroup() {
        return theraGroup;
    }

    public void setTheraGroup(String theraGroup) {
        this.theraGroup = theraGroup;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
