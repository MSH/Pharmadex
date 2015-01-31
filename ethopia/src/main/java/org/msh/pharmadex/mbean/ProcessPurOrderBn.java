package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.PurOrderService;
import org.springframework.web.util.WebUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.ResourceBundle;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessPurOrderBn {

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private PurOrder purOrder;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{purOrderService}")
    private PurOrderService purOrderService;
    private boolean displayRecommend;

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            purOrder.setUpdatedDate(new Date());
            if (purOrder.getPurProds() == null || purOrder.getPurProds().size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            purOrder = purOrderService.updatePurOrder(purOrder);

            if (purOrder == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
                return null;
            }

            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
            return "/public/processpurorderlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    public String approveOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext,"msgs");
        if(purOrder.getState().equals(AmdmtState.RECOMMENDED)) {
            purOrder.setState(AmdmtState.APPROVED);
            purOrder.setApprovalDate(new Date());
            return saveApp();
        }else{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"),"Invalid Operation!!!" ));
            return "";
        }
    }

    public String rejectOrder() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext,"msgs");
        if(purOrder.getState().equals(AmdmtState.NOT_RECOMMENDED)) {
            purOrder.setState(AmdmtState.REJECTED);
            purOrder.setApprovalDate(new Date());
            return saveApp();
        }else{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), "Invalid Operation!!!"));
            return "";
        }

    }

    public String recommendOrder() {
        if(purOrder.getState().equals(AmdmtState.NEW_APPLICATION)||purOrder.getState().equals(AmdmtState.NOT_RECOMMENDED)){
            purOrder.setState(AmdmtState.RECOMMENDED);
            return saveApp();
        }else{
            return "";
        }
    }

    public String notRecommendedOrder() {
        purOrder.setState(AmdmtState.NOT_RECOMMENDED);

        return saveApp();
    }
//    public String registerApplicant() {
//        applicant.setState(ApplicantState.REGISTERED);
//        applicantService.updateApp(applicant, null);
//        globalEntityLists.setRegApplicants(null);
//        FacesContext context = FacesContext.getCurrentInstance();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "processAppBn", null);
//        return "/internal/processpurorderlist.faces";
//
//    }

    public String cancel() {
        purOrder = new PurOrder();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processAppBn", null);
        return "/internal/processpurorderlist.faces";
    }

    //    public Applicant getApplicant() {
//        if (applicant == null) {
//            if (userSession.getApplcantID() != null) {
//                applicant = applicantService.findApplicant(userSession.getApplcantID());
//                userSession.setApplcantID(null);
//                if (applicant.getAddress() == null)
//                    applicant.setAddress(new Address());
//                if (applicant.getAddress().getCountry() == null)
//                    applicant.getAddress().setCountry(new Country());
//            } else {
//                applicant = null;
//            }
//        }
//        return applicant;
//    }
    public PurOrder getPurOrder() {
        if (purOrder == null) {
            if (userSession.getPurOrderID() != null) {
                purOrder = purOrderService.findPurOrderEager(Long.valueOf(userSession.getPurOrderID()));

            } else {
                purOrder = null;
            }
        }
        if(purOrder!=null) {
            if (purOrder.getState().equals(AmdmtState.NEW_APPLICATION))
                displayRecommend = true;
            else
                displayRecommend = false;
        }
        return purOrder;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public void setPurOrder(PurOrder purOrder) {

        this.purOrder = purOrder;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public PurOrderService getPurOrderService() {
        return purOrderService;
    }

    public void setPurOrderService(PurOrderService purOrderService) {
        this.purOrderService = purOrderService;
    }

    public boolean isDisplayRecommend() {
        getPurOrder();
        return displayRecommend;
    }

    public void setDisplayRecommend(boolean displayRecommend) {
        this.displayRecommend = displayRecommend;
    }
}
