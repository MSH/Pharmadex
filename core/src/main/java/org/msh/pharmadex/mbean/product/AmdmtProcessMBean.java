package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdAppAmdmt;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.service.AmdmtService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class AmdmtProcessMBean implements Serializable {

    private static final long serialVersionUID = 175811820572316932L;

    @ManagedProperty(value = "#{amdmtService}")
    AmdmtService amdmtService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    private List<ProdApplications> prodApplicationses;
    private List<ProdApplications> filteredApps;
    private ProdAppAmdmt prodAppAmdmt = new ProdAppAmdmt();

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");


    public void nextAmdmtStep() {
//        prodAppAmdmt = processProdBn.getProdAppAmdmt();
        this.prodAppAmdmt = prodAppAmdmt;
        AmdmtState currState = prodAppAmdmt.getAmdmtState();

        if (currState.equals(AmdmtState.NEW_APPLICATION))
            prodAppAmdmt.setAmdmtState(AmdmtState.REVIEW);
        else if (currState.equals(AmdmtState.REVIEW)) {
            prodAppAmdmt.setAmdmtState(AmdmtState.APPROVED);
            prodAppAmdmt.setApproved(true);
//            prodAppAmdmt.setApprovedBy(userSession.getLoggedINUserID());
        }

        String result = amdmtService.saveAmdmt(prodAppAmdmt, userSession.getLoggedINUserID());
        prodApplicationses = null;
        facesContext = FacesContext.getCurrentInstance();
        if (result.equalsIgnoreCase("persisted"))
           facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_save"), resourceBundle.getString("global.success")));
        else
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("next_step_error")));

    }

    public String saveAmdmt() {
        amdmtService.saveAmdmt(prodAppAmdmt, userSession.getLoggedINUserID());
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
