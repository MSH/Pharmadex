package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPProd;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

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
        pOrderBase = new PIPOrder();
        Long pipOrderID = (Long) JsfUtils.flashScope().get("pipOrderID");
        if (pipOrderID != null) {
            pOrderBase = pOrderService.findPIPOrderEager(pipOrderID);
            initVariables();
            JsfUtils.flashScope().keep("pipOrderID");
        }

    }

    @Override
    public void initVariables() {
        pOrderChecklists = ((PIPOrder) pOrderBase).getpOrderChecklists();
        pipProds = ((PIPOrder) pOrderBase).getPipProds();
        pOrderComments = ((PIPOrder) pOrderBase).getpOrderComments();
        setApplicantUser(pOrderBase.getApplicantUser());
        setApplicant(pOrderBase.getApplicantUser().getApplicant());
        setpOrderDocs(null);
    }

    public String saveApp() {
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

    public List<PIPProd> getPipProds() {
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }
}
