package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPProd;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.service.POrderService;
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
public class PIPOrderListBn implements Serializable {

    @ManagedProperty(value = "#{POrderService}")
    private POrderService pOrderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private PIPOrder pipOrder = new PIPOrder();
    private List<PIPOrder> pipOrders;
    private String pipNo;

    @PostConstruct
    private void init() {
    }

    public String searchPIPOrder(){
        POrderBase pOrderBase = pOrderService.findPOrder(pipNo, false);
        if(pOrderBase!=null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().put("processpiporder", ""+pOrderBase.getId());
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
}
