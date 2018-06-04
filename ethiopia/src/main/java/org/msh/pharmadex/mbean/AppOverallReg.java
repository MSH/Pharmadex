package org.msh.pharmadex.mbean;

/**
 * Created by Одиссей on 19.01.2017.
 * Date: 19.01.2017
 */
public class AppOverallReg {
    private String kpiName;
    private Double regNewMol= 0.0;
    private Double regNewMolSRA= 0.0;
    private Double regNewMolFast= 0.0;

    private Double regGBE = 0.0;
    private Double regGBESRA = 0.0;
    private Double regGBEFast = 0.0;

    private Double regGWoBE = 0.0;
    private Double regGWoBESRA = 0.0;
    private Double regGWoBEFast = 0.0;

    private Double suspCanc= 0.0;
    private Double renew= 0.0;
    private Double variation= 0.0;

    private Double total = 0.0;

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

    public Double getRegNewMolSRA() {
        return regNewMolSRA;
    }

    public void setRegNewMolSRA(Double regNewMolSRA) {
        this.regNewMolSRA = regNewMolSRA;
    }

    public Double getRegNewMolFast() {
        return regNewMolFast;
    }

    public void setRegNewMolFast(Double regNewMolFast) {
        this.regNewMolFast = regNewMolFast;
    }

    public Double getRegGBE() {
        return regGBE;
    }

    public void setRegGBE(Double regGBE) {
        this.regGBE = regGBE;
    }

    public Double getRegGBESRA() {
        return regGBESRA;
    }

    public void setRegGBESRA(Double regGBESRA) {
        this.regGBESRA = regGBESRA;
    }

    public Double getRegGBEFast() {
        return regGBEFast;
    }

    public void setRegGBEFast(Double regGBEFast) {
        this.regGBEFast = regGBEFast;
    }

    public Double getRegGWoBE() {
        return regGWoBE;
    }

    public void setRegGWoBE(Double regGWoBE) {
        this.regGWoBE = regGWoBE;
    }

    public Double getRegGWoBESRA() {
        return regGWoBESRA;
    }

    public void setRegGWoBESRA(Double regGWoBESRA) {
        this.regGWoBESRA = regGWoBESRA;
    }

    public Double getRegGWoBEFast() {
        return regGWoBEFast;
    }

    public void setRegGWoBEFast(Double regGWoBEFast) {
        this.regGWoBEFast = regGWoBEFast;
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

    public Double getTotal() {
        total = variation + renew +
                regGBE + regGBEFast + regGBESRA +
                regGWoBE + regGWoBEFast + regGWoBESRA +
                regNewMol + regNewMolFast + regNewMolSRA +
                suspCanc;
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
