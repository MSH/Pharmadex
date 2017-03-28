package org.msh.pharmadex.mbean;

import org.msh.pharmadex.dao.iface.PurOrderDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.Currency;
import org.msh.pharmadex.domain.ResourceBundle;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PurOrderBn extends POrderBn {
    private static final Logger logger = LoggerFactory.getLogger(PurOrderBn.class);

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;

    @ManagedProperty(value = "#{companyService}")
    private CompanyService companyService;

    @ManagedProperty(value = "#{timelineServiceET}")
    private TimelineServiceET timelineServiceET;

    private List<PurProd> purProds;
    private PurOrder purOrder;
    private List<POrderChecklist> pOrderChecklists;
    private PurProd purProd;
    private ProdTable product;
    private boolean showWithdrawn;
    private boolean showSubmit;
    private POrderComment pOrderComment;
    private User curUser;
    private boolean showComment;
    private FacesContext facesContext;
    private java.util.ResourceBundle resourceBundle;

    @PostConstruct
    private void init() {
        String purOrderSt = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("purOrderID");
        if (purOrderSt != null){
            if (!purOrderSt.equals("")) {
                curUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                Long purOrderID = Long.valueOf(purOrderSt);
                purOrder = getpOrderService().findPurOrderEager(purOrderID);
                purOrder.getpOrderComments();
                if (purOrder.getCurrency() == null)
                    purOrder.setCurrency(new Currency());
                pOrderChecklists = purOrder.getpOrderChecklists();
                purProds = purOrder.getPurProds();
                setApplicantUser(purOrder.getApplicantUser());
                setApplicant(purOrder.getApplicantUser().getApplicant());
            }
        } else {
            purOrder = new PurOrder(new Currency());
            curUser = getUserService().findUser(getUserSession().getLoggedINUserID());
            if (getUserSession().isCompany()) {
                curUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                setApplicantUser(curUser);
                setApplicant(curUser.getApplicant());
                purOrder.setCreatedBy(curUser);
                purOrder.setApplicantUser(curUser);
                purOrder.setApplicant(getApplicant());

                pOrderChecklists = new ArrayList<POrderChecklist>();
                List<PIPOrderLookUp> allChecklist = findAllChecklists();
                POrderChecklist eachCheckList;
                for (int i = 0; allChecklist.size() > i; i++) {
                    eachCheckList = new POrderChecklist();
                    eachCheckList.setPipOrderLookUp(allChecklist.get(i));
                    eachCheckList.setPurOrder(purOrder);
                    pOrderChecklists.add(eachCheckList);
                }
            }
        }
    }

    public void initComment() {
        pOrderComment = new POrderComment();
        pOrderComment.setUser(userService.findUser(userSession.getLoggedINUserID()));
        pOrderComment.setDate(new Date());
    }

    public List<ProdTable> completeProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        query = query.toUpperCase();
        if (getApplicant() != null) {
            suggestions  = pOrderService.findProdByLH(getApplicant().getApplcntId(),query);
        }
        return suggestions;
    }

    public void calculateTotalPrice(AjaxBehaviorEvent event) {
        if (purProd != null && purProd.getUnitPrice() != null && purProd.getQuantity() != null) {
            double unitPrice = purProd.getUnitPrice();
            purProd.setTotalPrice(unitPrice * purProd.getQuantity());
        }
    }

    @Override
    public void currChangeListener() {
        if (purOrder != null && purOrder.getCurrency() != null)
            purOrder.setCurrency(currencyService.findCurrency(purOrder.getCurrency().getId()));


    }

    @Override
    public void addDocument() {
        getpOrderDoc().setPurOrder(purOrder);
        getpOrderDocs().add(getpOrderDoc());
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    @Override
    protected List<PIPOrderLookUp> findAllChecklists() {
        return getpOrderService().findPIPCheckList(getApplicant().getApplicantType(), false);
    }

    @Override
    public void initAddProd() {
        setPurProd(new PurProd(new DosageForm(), new DosUom(), purOrder, purOrder.getCurrency().getCurrCD()));

    }

    @Override
    public void addProd() {
        if (purProds == null) {
            purProds = purOrder.getPurProds();
            if (purProds == null) {
                purProds = new ArrayList<PurProd>();
            }
        }

        purProd.setDosForm(dosageFormService.findDosagedForm(purProd.getDosForm().getUid()));
        purProd.setDosUnit(dosageFormService.findDosUom(purProd.getDosUnit().getId()));
        purProd.setPurOrder(purOrder);
        purProd.setCreatedDate(new Date());
        purProd.setProductNo("" + (purProds.size() + 1));
        purProd.setTotalPrice(purProd.getQuantity() * purProd.getUnitPrice());

        purProds.add(purProd);
        purOrder.setTotalPrice(pOrderService.calculateGrandTotal(purProds, purOrder.getFreight()));
        initAddProd();
    }

    public String removeProd(PurProd purProd) {
        try {
            context = FacesContext.getCurrentInstance();
            purProds.remove(purProd);

            PurProd pp;
            for (int i = 0; i < purProds.size(); i++) {
                pp = purProds.get(i);
                pp.setProductNo("" + (i + 1));
                pp.setTotalPrice(pp.getQuantity() * pp.getUnitPrice());
            }

            purOrder.setTotalPrice(pOrderService.calculateGrandTotal(purProds, purOrder.getFreight()));

            String result;
            if(purOrder.getId()!=null) {
                result = pOrderService.removeProd(purProd);
                pOrderService.updatePOrder(purOrder);
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
        purProd = new PurProd();
    }

    public String saveOrder() {
        try {
            context = FacesContext.getCurrentInstance();
            String timeLineSubj="";
            RegState timeLineState = RegState.NEW_APPL;
            if (purOrder.getState()==null) {
                purOrder.setState(AmdmtState.NEW_APPLICATION);
                timeLineSubj = "Purchase order created";
            }else if (purOrder.getState().equals(AmdmtState.NEW_APPLICATION)){
                timeLineSubj = "Purchase order created";
            }else{
                timeLineSubj = "Purchase order updated";
                timeLineState = RegState.REVIEW_BOARD;
            }

            purOrder.setState(AmdmtState.NEW_APPLICATION);
            purOrder.setpOrderChecklists(pOrderChecklists);
            purOrder.setPurProds(purProds);
            purOrder.setApplicant(getApplicant());
            purOrder.setApplicantUser(getApplicantUser());

            if (getUserSession().isCompany())
                purOrder.setApplicant(purOrder.getApplicant());

            RetObject retValue = getpOrderService().newOrder(purOrder);
            if (retValue.getMsg().equals("persist")) {
                purOrder = (PurOrder) retValue.getObj();
                String retMsg = super.saveOrder();
                context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
                timelineServiceET.createTimeLineEvent(purOrder, timeLineState,curUser,timeLineSubj);
                return "purorderlist";
            } else {
                purOrder.setState(AmdmtState.SAVED);
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
            purOrder.setState(AmdmtState.WITHDRAWN);

            pOrderComment.setPurOrder(purOrder);
            pOrderComment.setExternal(true);
            List<POrderComment> pOrderComments = pOrderService.findPOrderComments(purOrder);
            if (pOrderComments == null)
                pOrderComments = new ArrayList<POrderComment>();
            pOrderComments.add(pOrderComment);
            purOrder.setpOrderComments(pOrderComments);

            purOrder = (PurOrder) pOrderService.saveOrder(purOrder);
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
            return "purorderlist";
        }catch (Exception ex){
            ex.printStackTrace();
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ex.getMessage()));
            return "";
        }
    }


    public String cancelOrder() {
        if (userSession.isCompany())
            return "/secure/purorderlist";
        else
            return "/internal/processpurorderlist";
    }

    public String noOrder() {
        return "";
    }

    @Override
    protected ArrayList<POrderDoc> findPOrdersDocs() {
        return (ArrayList<POrderDoc>) getpOrderService().findPOrderDocs(purOrder);
    }

    public void appSelectListenener(SelectEvent event) {
        gmpChangeListener();


    }

    public void appChangeListenener(AjaxBehaviorEvent event) {
//        logger.error("event " + event.getSource());
        gmpChangeListener();
    }

    @Transactional
    public void gmpChangeListener() {
        if (product != null && product.getId() != null) {
            List<ProdApplications> prodApplicationsList = prodApplicationsService.findProdApplicationByProduct(product.getId());
            ProdApplications prodApplications = null;
            for (ProdApplications pa : prodApplicationsList) {
                if (pa.isActive())
                    prodApplications = pa;
            }
            if (prodApplications==null) return;
            Product prod = prodApplications.getProduct();
            List<ProdCompany> prodCompanies = prod.getProdCompanies();
            Company c = null;
            for (ProdCompany pc : prodCompanies) {
                if (pc.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)) {
                    c = pc.getCompany();
                    c = companyService.findCompanyById(c.getId());

                }
            }
            purProd.setProduct(prod);
            purProd.setProductName(prod.getProdName());
            purProd.setDosUnit(prod.getDosUnit());
            purProd.setDosForm(prod.getDosForm());
            purProd.setProductDesc(prod.getProdDesc());
            purProd.setShelfLife(prod.getShelfLife());
            purProd.setProductNo(prodApplications.getProdRegNo());
            purProd.setDosStrength(prod.getDosStrength());
            purProd.setManufName(c.getCompanyName());
            purProd.setManufSite(c.getAddress().getAddress1() + ", " + c.getAddress().getAddress2());
            purProd.setCountry(c.getAddress().getCountry());
        }

    }


    public List<PurProd> getPurProds() {
        if (purProds == null)
            purProds = purOrder.getPurProds();
        return purProds;
    }

    public void setPurProds(List<PurProd> purProds) {
        this.purProds = purProds;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }

    public PurProd getPurProd() {
        return purProd;
    }

    public void setPurProd(PurProd purProd) {
        this.purProd = purProd;
    }

    public ProdTable getProduct() {
        return product;
    }

    public void setProduct(ProdTable product) {
        this.product = product;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public List<POrderChecklist> getpOrderChecklists() {
        if (pOrderChecklists == null) {
            pOrderChecklists = purOrder.getpOrderChecklists();
        }

        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }

    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public boolean isShowComment(){
        showComment = false;
        if (purOrder != null && purOrder.getState() != null) {
            if (purOrder.getState().equals(AmdmtState.FEEDBACK))
                showComment = true;
        }
        return showComment;
    }

    public void setShowComment(boolean showComment) {
        this.showComment = showComment;
    }

    public boolean isShowWithdrawn() {
        if (purOrder != null && purOrder.getState() != null) {
            if (purOrder.getState().equals(AmdmtState.WITHDRAWN) || purOrder.getState().equals(AmdmtState.APPROVED)
                    || purOrder.getState().equals(AmdmtState.REJECTED) || purOrder.getState().equals(AmdmtState.FEEDBACK)
                    || purOrder.getState().equals(AmdmtState.SAVED))

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
        if (purOrder != null && purOrder.getState() != null) {
            if (purOrder.getState().equals(AmdmtState.WITHDRAWN) || purOrder.getState().equals(AmdmtState.FEEDBACK)
                    || purOrder.getState().equals(AmdmtState.SAVED))

                showSubmit = true;
            else
                showSubmit = false;
        } else {
            showSubmit = true;
        }
        return showSubmit;
    }

    public void addComment() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if (!userSession.isStaff())
                purOrder.setReviewState(RecomendType.FEEDBACK);
            else
                purOrder.setReviewState(RecomendType.COMMENT);
            pOrderComment.setRecomendType(purOrder.getReviewState());
            enterComment();
            String retObject = newApp();
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), ex.getMessage()));
        }
    }

    public String newApp() {
        facesContext = FacesContext.getCurrentInstance();
        getResourceBundle();
        try {
            if (purProds == null || purProds.size() == 0) {
                FacesMessage error = new FacesMessage(resourceBundle.getString("valid_no_app_user"));
                error.setSeverity(SEVERITY_ERROR);
                facesContext.addMessage(null, error);
                return null;
            }
            RetObject retObject = pOrderService.updatePOrder(purOrder);
            if (!retObject.getMsg().equals("persist")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), retObject.getMsg()));
                return null;
            } else {
                //purOrder = (PurOrder) retObject.getObj();
                purOrder = getpOrderService().findPurOrderEager(purOrder.getId());
            }

            if (purOrder == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
                return null;
            }

            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("app_save_success")));
            return "/public/processpurorderlist.faces";
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail"), e.getMessage()));
            return null;
        }
    }

    private void enterComment() {
        List<POrderComment> pOrderComments = pOrderService.findPOrderComments(purOrder);
        if (pOrderComments == null)
            pOrderComments = new ArrayList<POrderComment>();
        pOrderComment.setExternal(false);
        pOrderComments.add(pOrderComment);
        pOrderComment.setPurOrder(purOrder);
        purOrder.setpOrderComments(pOrderComments);
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

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public java.util.ResourceBundle getResourceBundle() {
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        return resourceBundle;
    }

    public void setResourceBundle(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
}
