package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.enums.ProdCategory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by utkarsh on 4/6/15.
 */
public class ProdTable implements Serializable {

    private Long id;
    private String regNo;
    private String prodName;
    private String genName;
    private ProdCategory prodCategory;
    private String appName;
    private Date regDate;
    private Date regExpiryDate;
    private String manufName;
    private String prodDesc;
    private Long prodAppID;
    private String fnm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }

    public ProdCategory getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(ProdCategory prodCategory) {
        this.prodCategory = prodCategory;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getRegExpiryDate() {
        return regExpiryDate;
    }

    public void setRegExpiryDate(Date regExpiryDate) {
        this.regExpiryDate = regExpiryDate;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public Long getProdAppID() {
        return prodAppID;
    }

    public void setProdAppID(Long prodAppID) {
        this.prodAppID = prodAppID;
    }

	public String getFnm() {
		return fnm;
	}

	public void setFnm(String fnm) {
		this.fnm = fnm;
	}
    
}
