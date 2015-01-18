package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.PIPOrderService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

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

    @ManagedProperty(value = "#{pipOrderService}")
    private PIPOrderService pipOrderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private PIPOrder pipOrder;
    private PIPProd pipProd;
    private List<PIPProd> pipProds;
    private List<PIPOrderChecklist> pipOrderChecklists;
    private User user;

    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");


    @PostConstruct
    private void init() {
        pipOrder = new PIPOrder();
        if (userSession.isCompany()) {
            user = userSession.getLoggedInUserObj();
            pipOrder.setCreatedBy(user);
        }

        pipOrderChecklists = new ArrayList<PIPOrderChecklist>();
        List<PIPOrderLookUp> allChecklist = pipOrderService.findAllCheckList();
        PIPOrderChecklist eachCheckList;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachCheckList = new PIPOrderChecklist();
            eachCheckList.setPipOrderLookUp(allChecklist.get(i));
            eachCheckList.setPipOrder(pipOrder);
            pipOrderChecklists.add(eachCheckList);
        }
    }

    public void initAddProd(){
        pipProd = new PIPProd();
    }

    public void addProd(){
        if(pipProds==null) {
            pipProds = pipOrder.getPipProds();
            if(pipProds==null)
                pipProds = new ArrayList<PIPProd>();
        }

        pipProds.add(pipProd);

        pipProd = new PIPProd();
    }

    public String removeProd(PIPProd pipProd) {
        context = FacesContext.getCurrentInstance();
        pipProds.remove(pipProd);
        context.addMessage(null, new FacesMessage(bundle.getString("pipprod_removed")));
        return null;
    }


    public void cancelAddProd(){
        pipProd = new PIPProd();
    }

    public String saveOrder(){
        return "";
    }

    public String cancelOrder(){
        pipOrder = new PIPOrder();
        return "/secure/piporderlist";
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

    public List<PIPOrderChecklist> getPipOrderChecklists() {
        if(pipOrderChecklists==null){
            pipOrderChecklists = pipOrder.getPipOrderChecklists();
        }
        return pipOrderChecklists;
    }

    public void setPipOrderChecklists(List<PIPOrderChecklist> pipOrderChecklists) {
        this.pipOrderChecklists = pipOrderChecklists;
    }

    public List<PIPProd> getPipProds() {
        if(pipProds==null)
            pipProds = pipOrder.getPipProds();
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PIPProd getPipProd() {
        return pipProd;
    }

    public void setPipProd(PIPProd pipProd) {
        this.pipProd = pipProd;
    }
}
