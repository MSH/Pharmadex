package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.service.PurOrderService;
import org.msh.pharmadex.util.RetObject;

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
public class PurOrderListBn implements Serializable {

    @ManagedProperty(value = "#{purOrderService}")
    PurOrderService purOrderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private PurOrder purOrder = new PurOrder();
    private List<PurOrder> purOrders;

    @PostConstruct
    private void init() {
    }

    public PurOrderService getPurOrderService() {
        return purOrderService;
    }

    public void setPurOrderService(PurOrderService purOrderService) {
        this.purOrderService = purOrderService;
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
            RetObject retObject = purOrderService.findAllSubmittedPo(userSession.getLoggedInUserObj(), userSession.isCompany());
            purOrders = (List<PurOrder>) retObject.getObj();
        }
        return purOrders;
    }

    public void setPurOrders(List<PurOrder> purOrders) {
        this.purOrders = purOrders;
    }
}
