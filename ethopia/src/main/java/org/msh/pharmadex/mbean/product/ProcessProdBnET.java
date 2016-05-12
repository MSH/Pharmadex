package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsServiceET;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnET implements Serializable {


    @ManagedProperty(value = "#{processProdBn}")
    public ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    public UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    public ProdApplicationsServiceET prodApplicationsServiceET;
    protected boolean displayVerify = false;
    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    private JasperPrint jasperPrint;

    public boolean isDisplayVerify() {
        if (userSession.isAdmin() || userSession.isHead() || userSession.isModerator())
            return true;
        if ((userSession.isStaff())) {
            if (processProdBn.getProdApplications() != null && !(processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL)))
                displayVerify = true;
            else
                displayVerify = false;
        }
        return displayVerify;

    }

    public void setDisplayVerify(boolean displayVerify) {
        this.displayVerify = displayVerify;
    }

    public void prescreenfeerecvd(){
        processProdBn.prodApplications.setPrescreenfeeReceived(true);
    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        processProdBn.save();
        ProdApplications prodApplications = processProdBn.getProdApplications();
        TimeLine timeLine = new TimeLine();

        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
        if (prodApplications.getRegState().equals(RegState.FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                processProdBn.setTimeLine(timeLine);
                processProdBn.addTimeline();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Please send sample request letter if required for processing the application.",
                                "Please send sample request letter if required for processing the application."));
            }
        }
        if (prodApplications.getRegState().equals(RegState.SCREENING)) {
            timeLine.setRegState(RegState.FEE)  ;
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
            prodApplications.setPriorityDate(new Date());

        }

        processProdBn.setSelectedTab(2);
    }

    public List<RegState> getRegSate() {
        ProdApplications prodApplications = processProdBn.getProdApplications();
        if (prodApplications != null)
            return prodApplicationsServiceET.nextStepOptions(prodApplications.getRegState(), userSession, processProdBn.getCheckReviewStatus());
        return null;
    }
    public boolean showFeedbackButton(){
        boolean res;
        res = userSession.isHead() && (processProdBn.prodApplications.getRegState().equals(RegState.REVIEW_BOARD)||processProdBn.prodApplications.getRegState().equals(RegState.VERIFY));
        return res;
    }
    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }
}
