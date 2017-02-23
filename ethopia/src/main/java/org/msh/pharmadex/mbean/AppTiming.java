package org.msh.pharmadex.mbean;

import java.io.Serializable;

/**
 * Created by Одиссей on 06.01.2017.
 * User: Odissey
 * Date: 06.01.2017
 */
public class AppTiming implements Serializable {
    String appNo;
    Double queued = 0.0;
    Double underReview = 0.0;
    Double fir = 0.0;
    Double waiting = 0.0;
    Double approved = 0.0;
    Double rejected = 0.0;
    Double pre_screening = 0.0;
    Double total1 = 0.0;
    Double total2 = 0.0;
    Double total = 0.0;

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public Double getQueued() {
        return queued;
    }

    public void setQueued(Double queued) {
        queued = Math.round(queued * 100.d) / 100.0d;
        this.queued = queued;
    }

    public Double getUnderReview() {
        return underReview;
    }

    public void setUnderReview(Double underReview) {
        underReview = Math.round(underReview * 100.d) / 100.0d;
        this.underReview = underReview;
    }

    public Double getWaiting() {
        return waiting;
    }

    public void setWaiting(Double waiting) {
        waiting = Math.round(waiting * 100.d) / 100.0d;
        this.waiting = waiting;
    }

    public Double getApproved() {
        return approved;
    }

    public void setApproved(Double approved) {
        approved = Math.round(approved * 100.d) / 100.0d;
        this.approved = approved;
    }

    public Double getRejected() {
        return rejected;
    }

    public void setRejected(Double rejected) {
        rejected = Math.round(rejected * 100.d) / 100.0d;
        this.rejected = rejected;
    }

    public Double getPre_screening() {
        return pre_screening;
    }

    public void setPre_screening(Double pre_screening) {
        pre_screening = Math.round(pre_screening * 100.d) / 100.0d;
        this.pre_screening = pre_screening;
    }

    public Double getTotal1() {
        return total1;
    }

    public void setTotal1(Double total1) {
        this.total1 = total1;
    }

    public Double getTotal2() {
        return total2;
    }

    public void setTotal2(Double total2) {
        this.total2 = total2;
    }

    public Double getTotal()
    {
        total = queued + underReview + waiting + approved + rejected + pre_screening + fir;
        total = Math.round(total * 100.d) / 100.d;
        return total;
    }

    public Double getFir() {
        return fir;
    }

    public void setFir(Double fir) {
        this.fir = fir;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
