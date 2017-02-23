package org.msh.pharmadex.mbean;

import java.io.Serializable;

/**
 * Created by Одиссей on 13.01.2017.
 */
public class AppOverall implements Serializable{
    private String kpiName;
    private Double regNewMol= Double.valueOf(0);
    private Double regGBE = Double.valueOf(0);
    private Double regGWoBE = Double.valueOf(0);
    private Double suspCanc= Double.valueOf(0);
    private Double renew= Double.valueOf(0);
    private Double variation= Double.valueOf(0);
    private Double pip= Double.valueOf(0);
    private Double po= Double.valueOf(0);
    private Double total = Double.valueOf(0);

    public void sum(AppOverall add){
        regNewMol = regNewMol + add.regNewMol;
        regGWoBE = regGWoBE + add.regGWoBE;
        renew = renew + add.renew;
        regGBE = regGBE + add.regGBE;
        variation = variation + add.variation;
        suspCanc = suspCanc + add.suspCanc;
        pip = pip + add.pip;
        po = po + add.po;
        total = po + add.total;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public Double getRegNewMol() {
        return regNewMol;
    }

    public void setRegNewMol(Double regNewMol) {
        this.regNewMol = regNewMol;
    }

    public Double getRegGBE() {
        return regGBE;
    }

    public void setRegGBE(Double regGBE) {
        this.regGBE = regGBE;
    }

    public Double getRegGWoBE() {
        return regGWoBE;
    }

    public void setRegGWoBE(Double regGWoBE) {
        this.regGWoBE = regGWoBE;
    }

    public Double getSuspCanc() {
        return suspCanc;
    }

    public void setSuspCanc(Double suspCanc) {
        this.suspCanc = suspCanc;
    }

    public Double getRenew() {
        return renew;
    }

    public void setRenew(Double renew) {
        this.renew = renew;
    }

    public Double getVariation() {
        return variation;
    }

    public void setVariation(Double variation) {
        this.variation = variation;
    }

    public Double getPip() {
        return pip;
    }

    public void setPip(Double pip) {
        this.pip = pip;
    }

    public Double getPo() {
        return po;
    }

    public void setPo(Double po) {
        this.po = po;
    }

    public Double getTotal() {
        total = po+pip+suspCanc+regGBE+regGWoBE+regNewMol+renew+variation;
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
