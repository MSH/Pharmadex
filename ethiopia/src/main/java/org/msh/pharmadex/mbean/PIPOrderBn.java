package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.StrTools;
import org.primefaces.event.SelectEvent;

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
    private boolean showWithdrawn;
    private boolean showSubmit;
    private boolean showAddButton;
    private POrderComment pOrderComment;
    private ProdTable product;
    private List<Attachment> attachments;
    private User curUser;
    @ManagedProperty(value = "#{dosageFormService}")
    private DosageFormService dosageFormService;
    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    @ManagedProperty(value = "#{companyService}")
    CompanyService companyService;
    @ManagedProperty(value = "#{timelineServiceET}")
    private TimelineServiceET timelineServiceET;

    @PostConstruct
    private void init() {
        try {
            Long userId = userSession.getLoggedINUserID();
            curUser = userService.findUser(userId);
            product = new ProdTable();
            String pipOrderst = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pipOrderID");
            if (pipOrderst != null && !pipOrderst.equals("")) {
                Long pipOrderID = Long.valueOf(pipOrderst);
                pipOrder = getpOrderService().findPIPOrderByID(pipOrderID);
                if (pipOrder.getCurrency() == null){
                    pipOrder.setCurrency(new Currency());
                 
                }
                pOrderChecklists = pipOrder.getpOrderChecklists();
                pipProds = pipOrder.getPipProds();
                setApplicantUser(pipOrder.getApplicantUser());
                setApplicant(pipOrder.getApplicantUser().getApplicant());
            } else {//this is new order
                pipOrder = new PIPOrder(new Currency());
                if (getUserSession().isCompany()) {
                    User applicantUser = getUserService().findUser(getUserSession().getLoggedINUserID());
                    setApplicantUser(applicantUser);
                    setApplicant(applicantUser.getApplicant());
                    pipOrder.setCreatedBy(applicantUser);
                    pipOrder.setApplicantUser(applicantUser);
                    pipOrder.setApplicant(getApplicant());
                    pipOrder.getCurrency().setCurrCD("US Dollar");
                    pipProd = new PIPProd(new DosageForm(), new DosUom(), pipOrder, pipOrder.getCurrency().getCurrCD());
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
        getpOrderDoc().setPipOrder(pipOrder);
        getpOrderDoc().setCreatedDate(new Date());
        getpOrderDocs().add(getpOrderDoc());
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        if (!isShowSubmit()){//if it is mode, submit button can not be shown, save order
            pOrderService.save(getpOrderDocs());
        }
    }

    @Override
    protected List<PIPOrderLookUp> findAllChecklists() {
        return getpOrderService().findPIPCheckList(getApplicant().getApplicantType(), true);
    }

    @Override
    public void initAddProd() {
        DosUom uom = new DosUom();
        uom.setUom("");
        uom.setId(0);
        setPipProd(new PIPProd(new DosageForm(), uom, pipOrder, pipOrder.getCurrency().getCurrCD()));
        pipProd.setProductName("");
        setPipProd(pipProd);

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

     public List<ProdTable> completeProducts(String query){
        List<ProdTable> found = productService.findAllRegisteredProduct();
        List<ProdTable> result = new ArrayList<ProdTable>();
        query = query.toUpperCase();
        //product.setProdName(query);
        pipProd.setProductName(query);
        if (StrTools.isEmptyString(query))
            return found;
        else{
            for(ProdTable prod:found){
                if (prod.getProdName().toUpperCase().startsWith(query) || prod.getGenName().toUpperCase().startsWith(query))
                    result.add(prod);
            }
        }

        return result;
    }

    public List<String> completeManufacturer(String query){
        if (query==null || "".equals(query)) return null;
        List<String> res = new ArrayList<String>();
        List<Company> manfs = globalEntityLists.getManufacturers();
        query = query.toLowerCase();
        for (Company man : manfs){
            if (man.getCompanyName().toLowerCase().startsWith(query))
                if (!res.contains(man.getCompanyName()))
                    res.add(man.getCompanyName());
        }
        return res;
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
            String timeLineSubj = "";
            RegState timeLineState = RegState.NEW_APPL;
            context = FacesContext.getCurrentInstance();
            if (pipOrder.getState()==null) {
                pipOrder.setState(AmdmtState.NEW_APPLICATION);
                timeLineSubj = "Pre-import order created";
            }else if (pipOrder.getState().equals(AmdmtState.NEW_APPLICATION)){
                timeLineSubj = "Pre-import order created";
            }else{
                timeLineSubj = "Pre-import order updated";
                pipOrder.setState(AmdmtState.NEW_APPLICATION);
                timeLineState = RegState.NEW_APPL;
            }

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
                timelineServiceET.createTimeLineEvent(pipOrder,timeLineState,curUser,timeLineSubj);
                return "piporderlist";
            } else {
                pipOrder.setState(AmdmtState.SAVED);
                if (retValue.getMsg().equals("missing_doc"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Please make sure all the required documents in the checklsit are enclosed"));
                if (retValue.getMsg().equals("no_prod"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "No product specified to be imported"));
                if (retValue.getMsg().equals("error"))
                    context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "Unable to create the order"));
                if (pipOrder.getState().equals(RegState.NEW_APPL))
                    timelineServiceET.createTimeLineEvent(pipOrder,timeLineState,curUser,timeLineSubj);

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
            timelineServiceET.createTimeLineEvent(pipOrder,RegState.NEW_APPL,curUser,"PIP withdrown");
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
                pOrderService.updatePOrder(pipOrder);
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

    public boolean isShowAddButton(){
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

    public boolean isShowSubmit() {
        if (pipOrder != null && pipOrder.getState() != null){
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

    private void updateProductInfo(){
        if (product!=null) {
            Long paId = product.getProdAppID();
            Product prod = productService.findProduct(product.getId());
            pipProd.setDosForm(prod.getDosForm());
            pipProd.setDosUnit(prod.getDosUnit());
            pipProd.setDosStrength(prod.getDosStrength());
            pipProd.setProductDesc(prod.getProdDesc());
            pipProd.setShelfLife(prod.getShelfLife());
            pipProd.setManufName(prod.getManufName());
            pipProd.setProductName(prod.getProdName());
            String name = pipProd.getProductName();
            if (!name.endsWith("(registered)"))
                pipProd.setProductName(name+" (registered)");
            Long companyId=null;
            if (prod.getProdCompanies() != null){
                for (ProdCompany pcompany : prod.getProdCompanies()) {
                    if (pcompany.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
                        companyId = pcompany.getCompany().getId();
                    }
                }
                if (companyId!=null){
                    Company company = companyService.findCompanyById(companyId);
                    if (company!=null){
                        if (company.getAddress()!=null){
                            if (company.getAddress().getCountry()!=null){
                                pipProd.setCountry(company.getAddress().getCountry());
                            }
                        }
                    }
                }
            }
        }else{
            pipProd.setDosForm(null);
            pipProd.setDosUnit(null);
            pipProd.setDosStrength(null);
            pipProd.setProductDesc(null);
            pipProd.setShelfLife(null);
            pipProd.setManufName(null);
//            pipProd.setProductName(null);
            pipProd.setCountry(null);
        }
    }

    public void productSelectListenener(SelectEvent event) {
        product = (ProdTable) event.getObject();
        updateProductInfo();
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

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public ProdTable getProduct() {
        return product;
    }

    public void setProduct(ProdTable product) {
        this.product = product;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }
}
