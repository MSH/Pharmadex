package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessPurOrderBn extends ProcessPOrderBn{

    private List<PurProd> purProds;
    private java.util.ResourceBundle bundle;

    @PostConstruct
    public void init() {
        pOrderBase = new PurOrder();
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
        	Object obj = FacesContext.getCurrentInstance().getExternalContext().getFlash().get("purOrderID");
            if (obj==null){
                if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()!=null)
                    obj = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("purOrderID");
            }
        	if(obj != null){
        		Long purOrderID = Long.valueOf(obj.toString());
        		if (purOrderID != null) {
                    pOrderBase = pOrderService.findPurOrderEager(purOrderID);
                    initVariables();
                }
        	}
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public String openOrder() {
        Long pId = Scrooge.beanParam("purOrderID"); //from list xhtml
        PurOrder pOrder = pOrderService.findPurOrderEager(pId);
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

        return "/internal/processpurorder.xhtml";
    }
    @Override
    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        PurOrder pipOrder = (PurOrder) pOrderBase;
        pipOrder.setpOrderChecklists(pOrderChecklists);
        pipOrder.setpOrderComments(pOrderComments);
        pipOrder.setPurProds(purProds);

        RetObject retObject = pOrderService.updatePOrder(pOrderBase);
        return "/public/processpurorderlist.faces";
    }

    @Override
    public void initVariables() {
        purProds = ((PurOrder) pOrderBase).getPurProds();
        setApplicantUser(pOrderBase.getApplicantUser());
        setApplicant(pOrderBase.getApplicantUser().getApplicant());
        setpOrderDocs(null);
        curUser = userService.findUser(userSession.getLoggedINUserID());
    }

    @Override
    public String withdraw() {
        pOrderBase.setState(AmdmtState.FEEDBACK);
        pOrderComment.setPurOrder((PurOrder) pOrderBase);
        pOrderComment.setExternal(true);
//        pOrderComments = ((PurOrder) pOrderBase).getpOrderComments();
        pOrderComments = pOrderService.findPOrderComments(pOrderBase);
        if (pOrderComments == null)
            pOrderComments = new ArrayList<POrderComment>();
        pOrderComments.add(pOrderComment);
        ((PurOrder) pOrderBase).setpOrderComments(pOrderComments);
        return saveApp();
    }

    public String newApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            if (purProds == null || purProds.size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            RetObject retObject = pOrderService.updatePOrder(pOrderBase);
            if (!retObject.getMsg().equals("persist")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), retObject.getMsg()));
                return null;
            } else {
                pOrderBase = (POrderBase) retObject.getObj();
                initVariables();
            }

            if (pOrderBase == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
                return null;
            }

            pOrderService.save(pOrderDocs);
            initVariables();

            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
            return "/public/processpurorderlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    @Override
    public void addDocument() {
        getpOrderDoc().setPurOrder((PurOrder) pOrderBase);
        pOrderDocs.add(getpOrderDoc());
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @Override
    protected void setPOrderForComment() {
        pOrderComment.setPurOrder((PurOrder) pOrderBase);

    }

    @Override
    public String cancel() {
        return "/internal/processpurorderlist.faces";
    }

    public List<PurProd> getPurProds() {
        return purProds;
    }

    public void setPurProds(List<PurProd> purProds) {
        this.purProds = purProds;
    }


}
