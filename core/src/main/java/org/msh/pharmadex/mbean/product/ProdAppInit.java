package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.enums.ProdAppType;

/**
 * Created by utkarsh on 1/20/15.
 */
public class ProdAppInit {

    private ProdAppType prodAppType;
    private String[] selSRA;
    private boolean eml = false;
    private String fee;
    private String prescreenfee;
    private String totalfee;
    private boolean SRA;
    private Long licHolderID;

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public String[] getSelSRA() {
        return selSRA;
    }

    public void setSelSRA(String[] selSRA) {
        this.selSRA = selSRA;
    }

    public boolean isEml() {
        return eml;
    }

    public void setEml(boolean eml) {
        this.eml = eml;
    }

    public String getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(String totalfee) {
        this.totalfee = totalfee;
    }

    public boolean isSRA() {
        return SRA;
    }

    public void setSRA(boolean SRA) {
        this.SRA = SRA;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrescreenfee() {
        return prescreenfee;
    }

    public void setPrescreenfee(String prescreenfee) {
        this.prescreenfee = prescreenfee;
    }

    public Long getLicHolderID() {
        return licHolderID;
    }

    public void setLicHolderID(Long licHolderID) {
        this.licHolderID = licHolderID;
    }
}
