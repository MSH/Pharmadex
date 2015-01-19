package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.PIPOrderService;
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
public class ProcessPIPOrderBn {

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private PIPOrder pipOrder;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{PIPOrderService}")
    private PIPOrderService pipOrderService;

    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            pipOrder.setUpdatedDate(new Date());
            if (pipOrder.getPipProds() == null || pipOrder.getPipProds().size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            pipOrder = pipOrderService.updatePIPOrder(pipOrder);

            if (pipOrder == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
                return null;
            }

            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
            return "/public/processpiporderlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    public String approveOrder() {
        pipOrder.setState(AmdmtState.APPROVED);

        return saveApp();
    }

//    public String registerApplicant() {
//        applicant.setState(ApplicantState.REGISTERED);
//        applicantService.updateApp(applicant, null);
//        globalEntityLists.setRegApplicants(null);
//        FacesContext context = FacesContext.getCurrentInstance();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "processAppBn", null);
//        return "/internal/processpiporderlist.faces";
//
//    }

    public String cancel() {
        pipOrder = new PIPOrder();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "processAppBn", null);
        return "/internal/processpiporderlist.faces";
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
    public PIPOrder getPipOrder() {
        if (pipOrder == null) {
            if (userSession.getPipOrderID() != null) {
                pipOrder = pipOrderService.findPIPOrderEager(Long.valueOf(userSession.getPipOrderID()));

            } else {
                pipOrder = null;
            }
        }
        return pipOrder;
    }


    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }


    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public PIPOrderService getPipOrderService() {
        return pipOrderService;
    }

    public void setPipOrderService(PIPOrderService pipOrderService) {
        this.pipOrderService = pipOrderService;
    }
}
