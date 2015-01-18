package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.PIPOrderService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
public class PIPOrderBn implements Serializable {

    @ManagedProperty(value = "#{pIPOrderService}")
    private PIPOrderService piporderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private List<PIPOrder> pipOrders;

    private PIPOrder pipOrder;

    private User user;

    private List<PIPOrderChecklist> pipOrderChecklists;

    @PostConstruct
    private void init() {
        pipOrder = new PIPOrder();
        if (userSession.isCompany()) {
            user = userSession.getLoggedInUserObj();
            pipOrder.setCreatedBy(user);
        }
    }

    public String saveOrder(){
        return "";
    }

    public String cancelOrder(){
        pipOrder = new PIPOrder();
        return "/secure/piporderlist";
    }

    public PIPOrderService getPiporderService() {
        return piporderService;
    }

    public void setPiporderService(PIPOrderService piporderService) {
        this.piporderService = piporderService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<PIPOrder> getPipOrders() {
        return pipOrders;
    }

    public void setPipOrders(List<PIPOrder> pipOrders) {
        this.pipOrders = pipOrders;
    }

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public List<PIPOrderChecklist> getPipOrderChecklists() {
        return pipOrderChecklists;
    }

    public void setPipOrderChecklists(List<PIPOrderChecklist> pipOrderChecklists) {
        this.pipOrderChecklists = pipOrderChecklists;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
