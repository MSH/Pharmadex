package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.PurOrderService;
import org.msh.pharmadex.service.UserService;
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
public class PurOrderBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PurOrderBn.class);
    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    @ManagedProperty(value = "#{purOrderService}")
    private PurOrderService purOrderService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;
    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    private PurOrder purOrder;
    private PurProd purProd;
    private List<PurProd> purProds;
    private List<PurOrderChecklist> purOrderChecklists;
    private User applicantUser;
    private Applicant applicant;
    private Product product;

    @PostConstruct
    private void init() {
        purOrder = new PurOrder();
        if (userSession.isCompany()) {
            applicantUser = userService.findUser(userSession.getLoggedINUserID());
            applicant = applicantUser.getApplicant();
            purOrder.setCreatedBy(applicantUser);
            purOrder.setApplicantUser(applicantUser);
        }

        purOrderChecklists = new ArrayList<PurOrderChecklist>();
        List<PIPOrderLookUp> allChecklist = purOrderService.findPurOrderCheckList();
        PurOrderChecklist eachCheckList;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachCheckList = new PurOrderChecklist();
            eachCheckList.setPipOrderLookUp(allChecklist.get(i));
            eachCheckList.setPurOrder(purOrder);
            purOrderChecklists.add(eachCheckList);
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


    public void initAddProd() {
        purProd = new PurProd();
        product = new Product();
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

        purProd.setPurOrder(purOrder);
        purProd.setCreatedDate(new Date());
        purProd.setProductDesc(purProd.getProduct().getProdDesc());
        purProd.setProductName(purProd.getProduct().getProdName());
//        purProd.setProductNo(purProd.getProduct().getRegNo());
        purProds.add(purProd);


        purProd = new PurProd();
        product = new Product();
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
        purOrder.setCreatedBy(applicantUser);
        purOrder.setState(AmdmtState.NEW_APPLICATION);
        purOrder.setPurOrderChecklists(purOrderChecklists);
        purOrder.setPurProds(purProds);
        purOrder.setApplicant(applicant);
        purOrder.setApplicantUser(applicantUser);


        if (userSession.isCompany())
            purOrder.setApplicant(purOrder.getApplicant());

        RetObject retValue = purOrderService.saveOrder(purOrder);
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


    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
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
            product = productService.findProduct(product.getId());
            purProd.setProduct(product);
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


    public List<PurOrderChecklist> getPurOrderChecklists() {
        if (purOrderChecklists == null) {
            purOrderChecklists = purOrder.getPurOrderChecklists();
        }
        return purOrderChecklists;
    }

    public void setPurOrderChecklists(List<PurOrderChecklist> purOrderChecklists) {
        this.purOrderChecklists = purOrderChecklists;
    }

    public void setPipOrderChecklists(List<PurOrderChecklist> pipOrderChecklists) {
        this.purOrderChecklists = pipOrderChecklists;
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

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public PurOrderService getPurOrderService() {
        return purOrderService;
    }

    public void setPurOrderService(PurOrderService purOrderService) {
        this.purOrderService = purOrderService;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
