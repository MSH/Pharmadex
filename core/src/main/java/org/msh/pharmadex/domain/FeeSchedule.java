package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "fee_sch")
public class FeeSchedule extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String AppType;

    @Column(length = 255, nullable = false)
    private String fee;

    @Column(length = 255, nullable = false)
    private String preScreenFee;

    @Column(length = 45, nullable = false)
    private String labFee;

    @Column(length = 255, nullable = false)
    private String totalFee;

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

    public String getAppType() {
        return AppType;
    }

    public void setAppType(String appType) {
        AppType = appType;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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

    public String getPreScreenFee() {
        return preScreenFee;
    }

    public void setPreScreenFee(String preScreenFee) {
        this.preScreenFee = preScreenFee;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getLabFee() {
        return labFee;
    }

    public void setLabFee(String labFee) {
        this.labFee = labFee;
    }
}
