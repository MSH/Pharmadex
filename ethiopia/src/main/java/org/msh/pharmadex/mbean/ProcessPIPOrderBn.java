package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UserRole;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.TimelineServiceET;
import org.msh.pharmadex.util.RetObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.ResourceBundle;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessPIPOrderBn extends ProcessPOrderBn {

    private List<PIPProd> pipProds;

    private java.util.ResourceBundle bundle;

    @PostConstruct
    public void init() {
        try {
            pOrderBase = new PIPOrder();
            Long pipOrderID=null;
            facesContext = FacesContext.getCurrentInstance();
            bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
            if (facesContext.getExternalContext().getRequestParameterMap().containsKey("pipOrderID"))
                pipOrderID = Long.valueOf(Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pipOrderID")));
            else{
                String pipOrderIDStr = (String) facesContext.getExternalContext().getFlash().get("pipOrderID");
                if (pipOrderIDStr!=null)
                    pipOrderID = Long.parseLong(pipOrderIDStr);
            }
            if (pipOrderID != null) {
                pOrderBase = pOrderService.findPIPOrderByID(pipOrderID);
                initVariables();
                createStartingProcessEvent(timelineServiceET.findTimelineByAppNo(pipOrderID,pOrderBase));
            }

        }catch (Exception ec){
            ec.printStackTrace();
        }

    }

    private void createStartingProcessEvent(List<TimeLineBase> tlist){
        if (userService.userHasRole(curUser, UserRole.ROLE_STAFF)){//is CSO
            for(TimeLineBase tl:tlist){
                if (tl.getRegState().equals(RegState.PRE_SCREENING))
                    return; //if exists - nothing to do
            }
        }
        timelineServiceET.createTimeLineEvent(pOrderBase, RegState.PRE_SCREENING,curUser,"Process registration of order started");
    }

    @Override
    public void initVariables() {
        pOrderChecklists = pOrderService.findPOrderChecklists(pOrderBase);
        pOrderDocs = (ArrayList<POrderDoc>) pOrderService.findPOrderDocs(pOrderBase);
        pOrderComments = pOrderService.findPOrderComments(pOrderBase);

        pipProds = ((PIPOrder) pOrderBase).getPipProds();
        setApplicantUser(pOrderBase.getApplicantUser());
        setApplicant(pOrderBase.getApplicantUser().getApplicant());
        setpOrderDocs(null);
        curUser = userService.findUser(userSession.getLoggedINUserID());

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
        String ret = saveApp();
        timelineServiceET.createTimeLineEvent(pOrderBase, RegState.FOLLOW_UP,curUser,"PIP sent on "+bundle.getString("open_app"));
        return ret;
    }

    @Override
    public String saveApp() {
        facesContext = FacesContext.getCurrentInstance();
        PIPOrder pipOrder = (PIPOrder) pOrderBase;
        pipOrder.setpOrderChecklists(pOrderChecklists);
        pipOrder.setpOrderComments(pOrderComments);
        pipOrder.setPipProds(pipProds);

        RetObject retObject = pOrderService.updatePOrder(pOrderBase,curUser);
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
            RetObject retObject =  pOrderService.updatePOrder(pOrderBase,curUser);
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

    public java.util.ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

}
