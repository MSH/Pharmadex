package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.AmdmtCategory;
import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.AmdmtType;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.AmdmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class AmendmentMbn implements Serializable {

    @Autowired
    ProcessProdBn processProdBn;

    @Autowired
    AmdmtService amdmtService;

    @Autowired
    UserSession userSession;

    private ProdApplications selProApp;

    private Product selProd;

    private AmdmtType amdmtType;

    private List selAmdmtS;

    private List<AmdmtCategory> selAmdmtCategories;

    private List<AmdmtCategory> amdmtCategories;

    private List<ProdAppAmdmt> prodAppAmdmts;

    public void handleAmdmtChange() {
        amdmtCategories = amdmtService.findAmdmtCategoryByType(amdmtType);
    }

    public String submitAmdments() {
        amdmtService.submitAmdmt(prodAppAmdmts);
        return "/internal/processreg.faces";
    }

    public String nextAmdmtStep() {
        System.out.println("----nextAmdmtStep---");
        List<Integer> selAmdmtInt = new ArrayList<Integer>();
        for (Object s : selAmdmtS) selAmdmtInt.add(Integer.valueOf((String) s));
        selAmdmtCategories = amdmtService.findAmdmtCatByIDs(selAmdmtInt);

        prodAppAmdmts = new ArrayList<ProdAppAmdmt>(selAmdmtCategories.size());
        ProdAppAmdmt prodAppAmdmt;
        for (AmdmtCategory amdmtCategory : selAmdmtCategories) {
            prodAppAmdmt = new ProdAppAmdmt();
            prodAppAmdmt.setAmdmtCategory(amdmtCategory);
            prodAppAmdmt.setAmdmtState(AmdmtState.NEW_APPLICATION);
            prodAppAmdmt.setApproved(false);
            prodAppAmdmt.setProdApplications(processProdBn.getProdApplications());
            prodAppAmdmt.setCreatedDate(new Date());
            prodAppAmdmt.setSubmittedBy(userSession.getLoggedInUserObj());
            prodAppAmdmts.add(prodAppAmdmt);
        }
        return "/secure/amdmtdetails.faces";
    }

    public AmdmtType getAmdmtType() {
        return amdmtType;
    }

    public void setAmdmtType(AmdmtType amdmtType) {
        this.amdmtType = amdmtType;
    }

    public Product getSelProd() {
        return selProApp.getProd();
    }

    public void setSelProd(Product selProd) {
        this.selProd = selProd;
    }

    public ProdApplications getSelProductApp() {
        if (selProApp == null) {
            selProApp = processProdBn.getProdApplications();
        }
        return selProApp;
    }

    public void setSelProApp(ProdApplications selProApp) {
        this.selProApp = selProApp;
    }

    public List getSelAmdmtS() {
        return selAmdmtS;
    }

    public void setSelAmdmtS(List selAmdmtS) {
        this.selAmdmtS = selAmdmtS;
    }

    public List<AmdmtCategory> getAmdmtCategories() {
        return amdmtCategories;
    }

    public void setAmdmtCategories(List<AmdmtCategory> amdmtCategories) {
        this.amdmtCategories = amdmtCategories;
    }

    public List<AmdmtCategory> getSelAmdmtCategories() {
        return selAmdmtCategories;
    }

    public void setSelAmdmtCategories(List<AmdmtCategory> selAmdmtCategories) {
        this.selAmdmtCategories = selAmdmtCategories;
    }

    public List<ProdAppAmdmt> getProdAppAmdmts() {
        return prodAppAmdmts;
    }

    public void setProdAppAmdmts(List<ProdAppAmdmt> prodAppAmdmts) {
        this.prodAppAmdmts = prodAppAmdmts;
    }
}
