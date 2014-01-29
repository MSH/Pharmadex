package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.AmdmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class AmdmtProcessMBean implements Serializable {

    private static final long serialVersionUID = 175811820572316932L;
    @Autowired
    AmdmtService amdmtService;

    @Autowired
    UserSession userSession;

    private List<ProdApplications> prodApplicationses;
    private List<ProdApplications> filteredApps;
    private ProdAppAmdmt prodAppAmdmt = new ProdAppAmdmt();


    public String onRowSelect() {
        return "/internal/processreg.faces";
    }

    public void nextAmdmtStep() {
//        prodAppAmdmt = processProdBn.getProdAppAmdmt();
        this.prodAppAmdmt = prodAppAmdmt;
        AmdmtState currState = prodAppAmdmt.getAmdmtState();

        if (currState.equals(AmdmtState.NEW_APPLICATION))
            prodAppAmdmt.setAmdmtState(AmdmtState.REVIEW);
        else if (currState.equals(AmdmtState.REVIEW)) {
            prodAppAmdmt.setAmdmtState(AmdmtState.APPROVED);
            prodAppAmdmt.setApproved(true);
            prodAppAmdmt.setApprovedBy(userSession.getLoggedInUserObj());
        }

        String result = amdmtService.saveAmdmt(prodAppAmdmt);
        prodApplicationses = null;

        if (result.equalsIgnoreCase("persisted"))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved", "State successfully changed"));
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Unable to move it to next step. Make sure all the required fields are entered."));

    }

    public String saveAmdmt() {
        amdmtService.saveAmdmt(prodAppAmdmt);
        return "";
    }

    public List<ProdApplications> getProdApplicationses() {
        if (prodApplicationses == null) {
            prodApplicationses = amdmtService.findAmdmtsRecieved();
        }
        return prodApplicationses;
    }


    public void setProdApplicationses(ArrayList<ProdApplications> prodApplicationses) {
        this.prodApplicationses = prodApplicationses;
    }

    public List<ProdApplications> getFilteredApps() {
        return filteredApps;
    }

    public void setFilteredApps(List<ProdApplications> filteredApps) {
        this.filteredApps = filteredApps;
    }

    public ProdAppAmdmt getProdAppAmdmt() {
        return prodAppAmdmt;
    }

    public void setProdAppAmdmt(ProdAppAmdmt prodAppAmdmt) {
        this.prodAppAmdmt = prodAppAmdmt;
    }

}
