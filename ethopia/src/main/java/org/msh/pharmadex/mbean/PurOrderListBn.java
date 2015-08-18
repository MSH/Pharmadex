package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.service.POrderService;
import org.msh.pharmadex.service.PurOrderService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;

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
public class PurOrderListBn implements Serializable {

    @ManagedProperty(value = "#{POrderService}")
    POrderService pOrderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private PurOrder purOrder = new PurOrder();
    private List<PurOrder> purOrders;
    private String pipNo;


    @PostConstruct
    private void init() {
    }

    public String sendToDetails(Long purOrderID){
        JsfUtils.flashScope().put("purOrderID", purOrderID);
        return "/secure/purorder";

    }

    public String searchPurOrder(){
            POrderBase pOrderBase = pOrderService.findPOrder(pipNo, true);
            if(pOrderBase!=null) {
                return sendToProcess(pOrderBase.getId());
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Purchase order not found!!!"));
                pipNo = null;
                return null;
            }
        }

    public String sendToProcess(Long pipOrderID){
        JsfUtils.flashScope().put("purOrderID", pipOrderID);
        return "processpurorder";

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
}
