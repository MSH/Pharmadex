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


    @PostConstruct
    private void init() {
        Long pipOrderID = (Long) JsfUtils.flashScope().get("pipOrderID");
        if (pipOrderID != null) {
            pipOrder = getpOrderService().findPIPOrderEager(pipOrderID);
            pOrderChecklists = pipOrder.getpOrderChecklists();
            pipProds = pipOrder.getPipProds();
            setApplicantUser(pipOrder.getApplicantUser());
            setApplicant(pipOrder.getApplicantUser().getApplicant());
            JsfUtils.flashScope().keep("pipOrderID");
        } else {
            pipOrder = new PIPOrder();
            if (getUserSession().isCompany()) {
                User applicantUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                setApplicantUser(applicantUser);
                setApplicant(applicantUser.getApplicant());
                pipOrder.setCreatedBy(applicantUser);
                pipOrder.setApplicantUser(applicantUser);

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
    }

    public void calculateTotalPrice() {
        if (pipProd.getUnitPrice() != null && pipProd.getQuantity() != null) {
            int unitPrice = pipProd.getUnitPrice();
            pipProd.setTotalPrice("" + (unitPrice * pipProd.getQuantity()));
        }
    }

    @Override
    public void addDocument() {
//        file = userSession.getFile();
        getpOrderDoc().setPipOrder(pipOrder);
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
        setPipProd(new PIPProd(new DosageForm(), new DosUom(), pipOrder));

    }

    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;


    @Override
    public void addProd() {
        if (pipProds == null) {
            pipProds = pipOrder.getPipProds();
            if (pipProds == null)
                pipProds = new ArrayList<PIPProd>();
        }

        pipProd.setDosForm(dosageFormService.findDosagedForm(pipProd.getDosForm().getUid()));
        pipProd.setDosUnit(dosageFormService.findDosUom(pipProd.getDosUnit().getId()));
        pipProd.setPipOrder(pipOrder);
        pipProd.setCreatedDate(new Date());
        pipProds.add(pipProd);
        initAddProd();
    }

    public String cancelOrder() {
        pipOrder = new PIPOrder();
        return "/home.faces";
    }

    @Override
    protected ArrayList<POrderDoc> findPOrdersDocs() {
        return (ArrayList<POrderDoc>) getpOrderService().findPOrderDocs(pipOrder);
    }

    public String saveOrder() {
        System.out.println("Inside saveorder");


        context = FacesContext.getCurrentInstance();
//        pipOrder.setCreatedBy(getApplicantUser());
        pipOrder.setState(AmdmtState.NEW_APPLICATION);
        pipOrder.setpOrderChecklists(getpOrderChecklists());
        pipOrder.setPipProds(pipProds);
        pipOrder.setApplicant(getApplicant());
        pipOrder.setApplicantUser(getApplicantUser());


        if (getUserSession().isCompany())
            pipOrder.setApplicant(getApplicantUser().getApplicant());

        RetObject retValue = getpOrderService().saveOrder(pipOrder);
        if (retValue.getMsg().equals("persist")) {
            pipOrder = (PIPOrder) retValue.getObj();
            String retMsg = super.saveOrder();
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
            return "piporderlist";
        } else {
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), retValue.getMsg()));
            return "";
        }
    }


    public String removeProd(PIPProd pipProd) {
        context = FacesContext.getCurrentInstance();
        pipProds.remove(pipProd);
        context.addMessage(null, new FacesMessage(bundle.getString("pipprod_removed")));
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
}
