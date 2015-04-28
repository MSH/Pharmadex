package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.service.PIPOrderService;
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
public class PIPOrderListBn implements Serializable {

    @ManagedProperty(value = "#{PIPOrderService}")
    PIPOrderService pipOrderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private PIPOrder pipOrder = new PIPOrder();
    private List<PIPOrder> pipOrders;

    @PostConstruct
    private void init() {
    }

    public PIPOrderService getPipOrderService() {
        return pipOrderService;
    }

    public void setPipOrderService(PIPOrderService pipOrderService) {
        this.pipOrderService = pipOrderService;
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
            RetObject retObject = pipOrderService.findAllSubmittedPIP(userSession.getLoggedINUserID(), userSession.getApplcantID(), userSession.isCompany());
            pipOrders = (List<PIPOrder>) retObject.getObj();
        }
        return pipOrders;
    }

    public void setPipOrders(List<PIPOrder> pipOrders) {
        this.pipOrders = pipOrders;
    }
}
