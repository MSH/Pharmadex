package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.AmdmtCategory;
import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.AmdmtType;
import org.msh.pharmadex.service.AmdmtService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@SessionScoped
public class AmendmentMbn implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{amdmtService}")
    AmdmtService amdmtService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    private ProdApplications selProApp;

    private Product selProd;

    private AmdmtType amdmtType;

    private List selAmdmtS;

    private List<AmdmtCategory> selAmdmtCategories;

    private List<AmdmtCategory> amdmtCategories;

    private List<ProdAppAmdmt> prodAppAmdmts;

    private boolean newAmdmt;

    public void handleAmdmtChange() {
        amdmtCategories = amdmtService.findAmdmtCategoryByType(amdmtType);
        if (amdmtType.equals(AmdmtType.NEW))
            newAmdmt = true;
        else
            newAmdmt = false;

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

    public boolean isNewAmdmt() {
        return newAmdmt;
    }

    public void setNewAmdmt(boolean newAmdmt) {
        this.newAmdmt = newAmdmt;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public AmdmtService getAmdmtService() {
        return amdmtService;
    }

    public void setAmdmtService(AmdmtService amdmtService) {
        this.amdmtService = amdmtService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
