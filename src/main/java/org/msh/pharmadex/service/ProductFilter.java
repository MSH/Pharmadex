package org.msh.pharmadex.service;

import org.msh.pharmadex.domain.Applicant;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class ProductFilter {
    String prodName;
    String manufName;
    String regNumber;
    String atcName;
    String prodClassif;
    String innName;
    Applicant applicant;

    public HashMap<String, Object> getFilters() {
        HashMap<String, Object> filter = new HashMap<String, Object>();
        if (prodName != null && !prodName.equalsIgnoreCase(""))
            filter.put("prodName", prodName);
        if (manufName != null && !manufName.equalsIgnoreCase(""))
            filter.put("manufName", manufName);
        if (regNumber != null && !regNumber.equalsIgnoreCase(""))
            filter.put("regNumber", regNumber);
        if (atcName != null && !atcName.equalsIgnoreCase(""))
            filter.put("atcName", atcName);
        if (innName != null && !innName.equalsIgnoreCase(""))
            filter.put("innName", innName);
        if (applicant != null)
            filter.put("applicant", applicant);


        return filter;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getAtcName() {
        return atcName;
    }

    public void setAtcName(String atcName) {
        this.atcName = atcName;
    }

    public String getProdClassif() {
        return prodClassif;
    }

    public void setProdClassif(String prodClassif) {
        this.prodClassif = prodClassif;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }


    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
