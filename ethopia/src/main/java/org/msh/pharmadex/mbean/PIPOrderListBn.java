package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.User;
//import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.service.TimelineServiceET;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PIPOrderListBn implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{POrderService}")
    protected POrderService pOrderService;

    @ManagedProperty(value = "#{timelineServiceET}")
    TimelineServiceET timelineServiceET;

    private PIPOrder pipOrder = new PIPOrder();
    private List<PIPOrder> pipOrders;
    private String pipNo;
    private User curUser;
    private java.util.ResourceBundle bundle;
    private FacesContext facesContext;

    @PostConstruct
    private void init() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        Long id = userSession.getLoggedINUserID();
        curUser = userService.findUser(id);
    }

    public String openOrder() {
        Long pipId = Scrooge.beanParam("pipOrderID"); //from list xhtml
        PIPOrder pOrder = pOrderService.findPIPOrderByID(pipId);
        if (userService.userHasRole(curUser,"ROLE_STAFF")) {

            if (pOrder.getResponsiblePerson() != null) {
                if (!userSession.getLoggedINUserID().equals(pOrder.getResponsiblePerson().getUserId())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("orderMsgOpening"),bundle.getString("orderResponsiblePerson")));
                    return "";
                }
            } else {//first open of PIP order
                //set responsible person
                pOrder.setResponsiblePerson(curUser);
                pOrderService.updatePOrder(pOrder,curUser);
                //log the starting of review
                timelineServiceET.createTimeLineEvent(pOrder,RegState.REVIEW_BOARD,curUser,"Review started");
            }

        }
        Scrooge.setBeanParam("pipOrderID",pipId); //to PIP order form

        return "/internal/processpiporder.xhtml";
    }

    public String searchPIPOrder(){
        POrderBase pOrderBase = pOrderService.findPOrder(pipNo, false);
        if(pOrderBase!=null) {
            String paramValue=String.valueOf(pOrderBase.getId());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pipOrderID", paramValue);
            return "processpiporder";
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pre-Import permit not found!!!"));
            pipNo = null;
            return "";
        }
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public List<PIPOrder> getPipOrders() {
        if (pipOrders == null) {
            RetObject retObject = pOrderService.findAllSubmittedPIP(userSession.getLoggedINUserID(), userSession.getApplcantID(), userSession.isCompany());
            pipOrders = (List<PIPOrder>) retObject.getObj();
        }
        return pipOrders;
    }

    public void setPipOrders(List<PIPOrder> pipOrders) {
        this.pipOrders = pipOrders;
    }

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
    }

    public String getPipNo() {
        return pipNo;
    }

    public void setPipNo(String pipNo) {
        this.pipNo = pipNo;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public java.util.ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(java.util.ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }
}
