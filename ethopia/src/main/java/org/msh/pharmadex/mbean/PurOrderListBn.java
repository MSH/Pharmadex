package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.POrderService;
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
import java.util.*;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PurOrderListBn implements Serializable {

    @ManagedProperty(value = "#{POrderService}")
    POrderService pOrderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private PurOrder purOrder = new PurOrder();
    private List<PurOrder> purOrders;
    private String pipNo;
    private User curUser;
    FacesContext facesContext;
    java.util.ResourceBundle bundle;

    @PostConstruct
    private void init() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        Long id = userSession.getLoggedINUserID();
        curUser = userService.findUser(id);
    }

    public String openOrder() {
        Long pId = Scrooge.beanParam("purOrderID"); //from list xhtml
        PIPOrder pOrder = pOrderService.findPIPOrderByID(pId);
        if (userService.userHasRole(curUser,"ROLE_STAFF")) {
            if (pOrder.getResponsiblePerson() != null) {
                if (!userSession.getLoggedINUserID().equals(pOrder.getResponsiblePerson().getUserId())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("orderMsgOpening"),bundle.getString("orderResponsiblePerson")));
                    return "";
                }
            } else {
                pOrder.setResponsiblePerson(curUser);
                pOrderService.updatePOrder(pOrder,curUser);
            }

        }
        Scrooge.setBeanParam("purOrderID",pId); //to PIP order form

        return "/internal/purorder.xhtml";
    }

    public String searchPurOrder(){
            POrderBase pOrderBase = pOrderService.findPOrder(pipNo, true);
            if(pOrderBase!=null) {
            	String paramValue = String.valueOf(pOrderBase.getId());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("purOrderID", paramValue);
                
                //FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().put("purOrderID", ""+pOrderBase.getId());
                return "processpurorder";
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Purchase order not found!!!"));
                pipNo = null;
                return null;
            }
        }

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }

    public List<PurOrder> getPurOrders() {
        if (purOrders == null) {
            RetObject retObject = pOrderService.findAllSubmittedPO(userSession.getLoggedINUserID(), userSession.getApplcantID(), userSession.isCompany());
            purOrders = (List<PurOrder>) retObject.getObj();
        }
        return purOrders;
    }

    public void setPurOrders(List<PurOrder> purOrders) {
        this.purOrders = purOrders;
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

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
