package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.service.DosageFormService;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.RetObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.Date;
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
public class PIPOrderBn extends POrderBn {

    private List<PIPProd> pipProds;
    private PIPOrder pipOrder;
    private List<POrderChecklist> pOrderChecklists;
    private PIPProd pipProd;
    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;
    private boolean showWithdrawn;
    private boolean showSubmit;
    private POrderComment pOrderComment;

    @PostConstruct
    private void init() {
        try {
            String pipOrderst = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pipOrderID");
            if (pipOrderst != null && !pipOrderst.equals("")) {
                Long pipOrderID = Long.valueOf(pipOrderst);
                pipOrder = getpOrderService().findPIPOrderByID(pipOrderID);
                if (pipOrder.getCurrency() == null)
                    pipOrder.setCurrency(new Currency());
                pOrderChecklists = pipOrder.getpOrderChecklists();
                pipProds = pipOrder.getPipProds();
                setApplicantUser(pipOrder.getApplicantUser());
                setApplicant(pipOrder.getApplicantUser().getApplicant());
            } else {
                pipOrder = new PIPOrder(new Currency());
                if (getUserSession().isCompany()) {
                    User applicantUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                    setApplicantUser(applicantUser);
                    setApplicant(applicantUser.getApplicant());
                    pipOrder.setCreatedBy(applicantUser);
                    pipOrder.setApplicantUser(applicantUser);
                    pipOrder.setApplicant(getApplicant());

                    pOrderChecklists = new ArrayList<POrderChecklist>();
                    List<PIPOrderLookUp> allChecklist = findAllChecklists();
                    POrderChecklist eachCheckList;
                    for (int i = 0; allChecklist.size() > i; i++) {
                        eachCheckList = new POrderChecklist();
                        eachCheckList.setPipOrderLookUp(allChecklist.get(i));
                        eachCheckList.setPipOrder(pipOrder);
                        pOrderChecklists.add(eachCheckList);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void calculateTotalPrice(AjaxBehaviorEvent event) {
        if (pipProd.getUnitPrice() != null && pipProd.getQuantity() != null) {
            double unitPrice = pipProd.getUnitPrice();
            double totalPrice = unitPrice * pipProd.getQuantity();
            pipProd.setTotalPrice(totalPrice);
        }
    }

    public void initComment() {
        pOrderComment = new POrderComment();
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
    }


    @Override
    public void currChangeListener() {
        if (pipOrder != null && pipOrder.getCurrency() != null)
            pipOrder.setCurrency(currencyService.findCurrency(pipOrder.getCurrency().getId()));

    }

    @Override
    public void addDocument() {
//        file = userSession.getFile();
        getpOrderDoc().setPipOrder(pipOrder);
        getpOrderDoc().setCreatedDate(new Date());
//        getpOrderDocDAO().save(getpOrderDoc());
        getpOrderDocs().add(getpOrderDoc());
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    @Override
    protected List<PIPOrderLookUp> findAllChecklists() {
        return getpOrderService().findPIPCheckList(getApplicant().getApplicantType(), true);
    }

    @Override
    public void initAddProd() {
//        Currency curr = currencyService.findCurrency(pipOrder.getCurrency().getId());
        setPipProd(new PIPProd(new DosageForm(), new DosUom(), pipOrder, pipOrder.getCurrency().getCurrCD()));

    }

    @Override
    public void addProd() {
        if (pipProds == null) {
            pipProds = pipOrder.getPipProds();
            if (pipProds == null) {
                pipProds = new ArrayList<PIPProd>();
            }
        }

        pipProd.setDosForm(dosageFormService.findDosagedForm(pipProd.getDosForm().getUid()));
        pipProd.setDosUnit(dosageFormService.findDosUom(pipProd.getDosUnit().getId()));
        pipProd.setPipOrder(pipOrder);
        pipProd.setCreatedDate(new Date());
        pipProd.setProductNo("" + (pipProds.size() + 1));
        pipProd.setTotalPrice(pipProd.getQuantity() * pipProd.getUnitPrice());
        pipProds.add(pipProd);
        pipOrder.setTotalPrice(pOrderService.calculateGrandTotal(pipProds, pipOrder.getFreight()));
        initAddProd();
    }

    public String cancelOrder() {
        if (userSession.isCompany())
            return "/secure/piporderlist";
        else
            return "/internal/processpiporderlist";
    }

    public String noOrder() {
        return "";
    }


    @Override
    protected ArrayList<POrderDoc> findPOrdersDocs() {
        return (ArrayList<POrderDoc>) getpOrderService().findPOrderDocs(pipOrder);
    }

    public String saveOrder() {
        try {
            context = FacesContext.getCurrentInstance();
//        pipOrder.setCreatedBy(getApplicantUser());
            pipOrder.setState(AmdmtState.NEW_APPLICATION);
            pipOrder.setpOrderChecklists(getpOrderChecklists());
            pipOrder.setPipProds(pipProds);
            pipOrder.setApplicant(getApplicant());
            pipOrder.setApplicantUser(getApplicantUser());

            if (getUserSession().isCompany())
                pipOrder.setApplicant(getApplicantUser().getApplicant());

            if (pipOrder.getApplicantUser() == null) {
                context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("global_fail"), "Please specify User for Local Agent"));
                return "";
            }

            RetObject retValue = getpOrderService().newOrder(pipOrder);
            if (retValue.getMsg().equals("persist")) {
                pipOrder = (PIPOrder) retValue.getObj();
                String retMsg = super.saveOrder();
                context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
                return "piporderlist";
            } else {
                pipOrder.setState(AmdmtState.SAVED);
                if (retValue.getMsg().equals("missing_doc"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please make sure all the required documents in the checklsit are enclosed"));
                if (retValue.getMsg().equals("no_prod"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "No product specified to be imported"));
                if (retValue.getMsg().equals("error"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Unable to create the order"));

                return "";
            }
        }catch (Exception ex){
            ex.printStackTrace();
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
            return "";
        }
    }

    public String withdraw() {
        try {
            context = FacesContext.getCurrentInstance();
            pipOrder.setState(AmdmtState.WITHDRAWN);

            pOrderComment.setPipOrder(pipOrder);
            pOrderComment.setExternal(true);
//        pOrderComments = ((PurOrder) pOrderBase).getpOrderComments();
            List<POrderComment> pOrderComments = pOrderService.findPOrderComments(pipOrder);
            if (pOrderComments == null)
                pOrderComments = new ArrayList<POrderComment>();
            pOrderComments.add(pOrderComment);
            pipOrder.setpOrderComments(pOrderComments);

            pipOrder = (PIPOrder) pOrderService.saveOrder(pipOrder);
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
            return "piporderlist";
        } catch (Exception ex) {
            ex.printStackTrace();
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
            return "";
        }
    }


    public String removeProd(PIPProd pipProd) {
        try {
            context = FacesContext.getCurrentInstance();
            pipProds.remove(pipProd);
            double total = pipOrder.getFreight();

            PIPProd pp;
            for (int i = 0; i < pipProds.size(); i++) {
                pp = pipProds.get(i);
                pp.setProductNo("" + (i + 1));
                pp.setTotalPrice(pp.getQuantity() * pp.getUnitPrice());
            }

            pipOrder.setTotalPrice(pOrderService.calculateGrandTotal(pipProds, pipOrder.getFreight()));

            String result;
            if(pipOrder.getId()!=null) {
                result = pOrderService.removeProd(pipProd);
                pOrderService.updatePIPOrder(pipOrder);
            }else{
                result = "persist";
            }
            if (result.equals("persist"))
                context.addMessage(null, new FacesMessage(bundle.getString("pipprod_removed")));
            else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), result));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));

        }
        return null;
    }

    @Override
    public void cancelAddProd() {
        setPipProd(new PIPProd());
    }

    public List<PIPProd> getPipProds() {
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

    public List<POrderChecklist> getpOrderChecklists() {
        if (pOrderChecklists == null) {
            pOrderChecklists = pipOrder.getpOrderChecklists();
        }
        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }

    public PIPProd getPipProd() {
        return pipProd;
    }

    public void setPipProd(PIPProd pipProd) {
        this.pipProd = pipProd;
    }

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }

    public boolean isShowWithdrawn() {
        if (pipOrder != null && pipOrder.getState() != null) {
            if (pipOrder.getState().equals(AmdmtState.WITHDRAWN) || pipOrder.getState().equals(AmdmtState.APPROVED)
                    || pipOrder.getState().equals(AmdmtState.REJECTED) || pipOrder.getState().equals(AmdmtState.FEEDBACK)
                    || pipOrder.getState().equals(AmdmtState.SAVED))
                showWithdrawn = false;
            else
                showWithdrawn = true;
        } else {
            showWithdrawn = false;
        }
        return showWithdrawn;
    }

    public void setShowWithdrawn(boolean showWithdrawn) {
        this.showWithdrawn = showWithdrawn;
    }

    public boolean isShowSubmit() {
        if (pipOrder != null && pipOrder.getState() != null) {
            if (pipOrder.getState().equals(AmdmtState.WITHDRAWN) || pipOrder.getState().equals(AmdmtState.FEEDBACK)
                    || pipOrder.getState().equals(AmdmtState.SAVED))
                showSubmit = true;
            else
                showSubmit = false;
        } else {
            showSubmit = true;
        }
        return showSubmit;
    }

    public void setShowSubmit(boolean showSubmit) {
        this.showSubmit = showSubmit;
    }

    public POrderComment getpOrderComment() {
        return pOrderComment;
    }

    public void setpOrderComment(POrderComment pOrderComment) {
        this.pOrderComment = pOrderComment;
    }
}
