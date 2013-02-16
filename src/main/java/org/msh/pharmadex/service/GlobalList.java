package org.msh.pharmadex.service;

import org.msh.pharmadex.domain.enums.ProdType;

import javax.annotation.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.util.Arrays;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class GlobalList {
    private ProdType[] prodType;

    public List<ProdType> getProdType() {
        return Arrays.asList(ProdType.values());
    }

//    public String getProdTypes() {
//        return (prodType == null) ? null : prodType.getKey();
//    }
//
//    public void setProdType(String prodType) {
//        this.prodType = ProdType.valueOf(prodType);
//    }
}
