package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
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
import java.io.Serializable;
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
    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    private List<PurProd> purProds;
    private PurOrder purOrder;
    private List<POrderChecklist> pOrderChecklists;
    private PurProd purProd;
    private ProdTable product;

    @PostConstruct
    private void init() {
        purOrder = new PurOrder();
        if (getUserSession().isCompany()) {
            User applicantUser = getUserService().findUser(getUserSession().getLoggedINUserID());
            setApplicantUser(applicantUser);
            setApplicant(applicantUser.getApplicant());
            purOrder.setCreatedBy(applicantUser);
            purOrder.setApplicantUser(applicantUser);
        }

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

    public List<ProdTable> completeProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        for (ProdTable p : globalEntityLists.getRegProducts()) {
            if ((p.getProdName() != null && p.getProdName().toLowerCase().startsWith(query))
                    || (p.getGenName() != null && p.getGenName().toLowerCase().startsWith(query)))
//                    || (p.getApprvdName() != null && p.getApprvdName().toLowerCase().startsWith(query)))
                suggestions.add(p);
        }
        return suggestions;
    }

    @Override
    public void addDocument() {
//        file = userSession.getFile();
        getpOrderDoc().setPurOrder(purOrder);
//        getpOrderService().save(getpOrderDoc());
        setpOrderDocs(null);
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }


    @Override
    protected List<PIPOrderLookUp> findAllChecklists() {
        return getpOrderService().findPIPCheckList(getApplicant().getApplicantType(), false);
    }


    public void initAddProd() {
        purProd = new PurProd();
        product = new ProdTable();
    }

    public void addProd() {
        context = FacesContext.getCurrentInstance();
        bundle = context.getApplication().getResourceBundle(context, "msgs");

        if (purProds == null) {
            purProds = purOrder.getPurProds();
            if (purProds == null)
                purProds = new ArrayList<PurProd>();
        }

        if(purProd.getProduct()==null){
            context.addMessage(null,new FacesMessage("Please select the product."));
        }

//        purProd.setPurOrder(purOrder);
        purProd.setCreatedDate(new Date());
        purProd.setProductDesc(purProd.getProduct().getProdDesc());
        purProd.setProductName(purProd.getProduct().getProdName());
//        purProd.setProductNo(purProd.getProduct().get);
        purProds.add(purProd);


        purProd = new PurProd();
        product = new ProdTable();
    }

    public String removeProd(PIPProd pipProd) {
        return null;
    }

    public String removeProd(PurProd purProd) {
        context = FacesContext.getCurrentInstance();
        purProds.remove(purProd);
        context.addMessage(null, new FacesMessage(bundle.getString("pipprod_removed")));
        return null;
    }


    public void cancelAddProd() {
        purProd = new PurProd();
    }

    public String saveOrder() {
        System.out.println("Inside saveorder");

        context = FacesContext.getCurrentInstance();
        purOrder.setSubmitDate(new Date());
        purOrder.setCreatedBy(getApplicantUser());
        purOrder.setState(AmdmtState.NEW_APPLICATION);
//        purOrder.setPurOrderChecklists(purOrderChecklists);
        purOrder.setPurProds(purProds);
        purOrder.setApplicant(getApplicant());
        purOrder.setApplicantUser(getApplicantUser());


        if (getUserSession().isCompany())
            purOrder.setApplicant(purOrder.getApplicant());

        RetObject retValue = getpOrderService().saveOrder(purOrder);
        if (retValue.getMsg().equals("persist")) {
            purOrder = (PurOrder) retValue.getObj();
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("global.success")));
            return "purorderlist";
        } else {
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
            return "";
        }
    }

    public String cancelOrder() {
        purOrder = new PurOrder();
        return "/home.faces";
    }

    @Override
    protected ArrayList<POrderDoc> findPOrdersDocs() {
        return  (ArrayList<POrderDoc>) getpOrderService().findPOrderDocs(purOrder);
    }


    public void appChangeListenener(SelectEvent event) {
        logger.error("inside appChangeListenener");
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
//        if (selectedCompany.isGmpInsp())
//            showGMP = true;
//        else
//            showGMP = false;
        logger.error("inside gmpChangeListener");
        if (product != null && product.getId() != null) {
            Product prod = productService.findProduct(product.getId());
            purProd.setProduct(prod);
            purProd.setProductNo(product.getRegNo());
//            showApp = true;
//            convertUser(selectedApplicant.getUsers());
//            if (users.size() > 1) {
//                setShowUserSelect(true);
//            } else {
//                if (users.size() == 1) {
//                    selectedUser = users.get(0);
//                    showUser = true;
//                }
//            }
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

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public List<POrderChecklist> getpOrderChecklists() {
        return pOrderChecklists;
    }

    public void setpOrderChecklists(List<POrderChecklist> pOrderChecklists) {
        this.pOrderChecklists = pOrderChecklists;
    }
}
