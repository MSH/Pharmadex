package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.context.FacesContext.getCurrentInstance;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessPIPOrderBn extends ProcessPOrderBn {

    private List<PIPProd> pipProds;

    @PostConstruct
    public void init() {
        try {
            pOrderBase = new PIPOrder();
            Long pipOrderID=null;
            facesContext = FacesContext.getCurrentInstance();
            if (facesContext.getExternalContext().getRequestParameterMap().containsKey("pipOrderID"))
                pipOrderID = Long.valueOf(Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pipOrderID")));
            else{
                String pipOrderIDStr = (String) facesContext.getExternalContext().getFlash().get("pipOrderID");
                if (pipOrderIDStr!=null)
                    pipOrderID = Long.parseLong(pipOrderIDStr);
            }
            //Long pipOrderID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pipOrderID"));
            if (pipOrderID != null) {
                pOrderBase = pOrderService.findPIPOrderByID(pipOrderID);
                initVariables();
            }
        }catch (Exception ec){
            ec.printStackTrace();
        }

    }

    @Override
    public void initVariables() {
        pOrderChecklists = pOrderService.findPOrderChecklists(pOrderBase);
        pOrderDocs = (ArrayList<POrderDoc>) pOrderService.findPOrderDocs(pOrderBase);
        pOrderComments = pOrderService.findPOrderComments(pOrderBase);

        pipProds = ((PIPOrder) pOrderBase).getPipProds();
//        pOrderComments = ((PIPOrder) pOrderBase).getpOrderComments();
        setApplicantUser(pOrderBase.getApplicantUser());
        setApplicant(pOrderBase.getApplicantUser().getApplicant());
        setpOrderDocs(null);
    }

    @Override
    public String withdraw() {
        pOrderBase.setState(AmdmtState.FEEDBACK);
        pOrderComment.setPipOrder((PIPOrder) pOrderBase);
        pOrderComment.setExternal(true);
        pOrderComments = ((PIPOrder) pOrderBase).getpOrderComments();
        pOrderComments = pOrderService.findPOrderComments(pOrderBase);
        if (pOrderComments == null)
            pOrderComments = new ArrayList<POrderComment>();
        pOrderComments.add(pOrderComment);
        return saveApp();
    }

    @Override
    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        PIPOrder pipOrder = (PIPOrder) pOrderBase;
        pipOrder.setpOrderChecklists(pOrderChecklists);
        pipOrder.setpOrderComments(pOrderComments);
        pipOrder.setPipProds(pipProds);

        RetObject retObject = pOrderService.updatePIPOrder(pOrderBase);
        return "/public/processpiporderlist.faces";
    }

    public String newApp() {
        facesContext = FacesContext.getCurrentInstance();
        try {
            if (pipProds == null || pipProds.size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            RetObject retObject = pOrderService.updatePIPOrder(pOrderBase);
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
            return "/public/processpiporderlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    @Override
    public void addDocument() {
        getpOrderDoc().setPipOrder((PIPOrder) pOrderBase);
        pOrderDocs.add(getpOrderDoc());
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @Override
    protected void setPOrderForComment() {
        pOrderComment.setPipOrder((PIPOrder) pOrderBase);

    }

    @Override
    public String cancel() {
        return "/internal/processpiporderlist.faces";
    }

    public List<PIPProd> getPipProds() {
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

}
