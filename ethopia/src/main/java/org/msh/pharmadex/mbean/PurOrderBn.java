package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.CompanyService;
import org.msh.pharmadex.service.DosageFormService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.util.JsfUtils;
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

    private List<PurProd> purProds;
    private PurOrder purOrder;
    private List<POrderChecklist> pOrderChecklists;
    private PurProd purProd;
    private ProdTable product;
    private boolean showWithdrawn;
    private boolean showSubmit;
    private POrderComment pOrderComment;

    @PostConstruct
    private void init() {
        String purOrderSt = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("purOrderID");
        if (purOrderSt != null){
            if (!purOrderSt.equals("")) {
                Long purOrderID = Long.valueOf(purOrderSt);
                purOrder = getpOrderService().findPurOrderEager(purOrderID);
                if (purOrder.getCurrency() == null)
                    purOrder.setCurrency(new Currency());
                pOrderChecklists = purOrder.getpOrderChecklists();
                purProds = purOrder.getPurProds();
                setApplicantUser(purOrder.getApplicantUser());
                setApplicant(purOrder.getApplicantUser().getApplicant());
            }
        } else {
            purOrder = new PurOrder(new Currency());
            if (getUserSession().isCompany()) {
                User applicantUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                setApplicantUser(applicantUser);
                setApplicant(applicantUser.getApplicant());
                purOrder.setCreatedBy(applicantUser);
                purOrder.setApplicantUser(applicantUser);
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
        if (getApplicant() != null) {
            suggestions = pOrderService.findProdByLH(getApplicant().getApplcntId());
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
                pOrderService.updatePIPOrder(purOrder);
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
//        purOrder.setCreatedBy(getApplicantUser());
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
//        pOrderComments = ((PurOrder) pOrderBase).getpOrderComments();
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
        logger.error("inside appSelectListenener");
        gmpChangeListener();


    }

    public void appChangeListenener(AjaxBehaviorEvent event) {
        logger.error("inside appChangeListenener");
//        logger.error("Selected company is " + selectedApplicant.getAppName());
        logger.error("event " + event.getSource());
        gmpChangeListener();


    }

    @Transactional
    public void gmpChangeListener() {
        logger.error("inside gmpChangeListener");
        if (product != null && product.getId() != null) {
            List<ProdApplications> prodApplicationsList = prodApplicationsService.findProdApplicationByProduct(product.getId());
            ProdApplications prodApplications = null;
            for (ProdApplications pa : prodApplicationsList) {
                if (pa.isActive())
                    prodApplications = pa;
            }
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
