package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProdApplicationsServiceET;
import org.msh.pharmadex.utils.Scrooge;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;

/**
 * Created by Одиссей on 03.07.2016.
 */
@ManagedBean
@ViewScoped
public class ReNewBean {
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{productDAO}")
    private ProductDAO productDAO;

    private ResourceBundle bundle;
    private User curUser;
    private FacesContext facesContext;
    private Long prodAppId;
    private Long parentAppId;

    @PostConstruct
    private void init(){
        parentAppId = getParam("prodAppID");
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
    }

    public String startReregistration(){
        //create copy of inital application and product
        ProdApplications prodAppRenew = new ProdApplications();
        ProdApplications prodApp = prodApplicationsService.findProdApplications(parentAppId);
        Scrooge.copyData(prodApp,prodAppRenew);
//        prodAppRenew.setId((long) 0);
        prodAppRenew.setProdAppType(ProdAppType.RENEW);
        Product product = new Product();
        Product reg_product = productDAO.findProductEager(parentAppId);
        Scrooge.copyData(reg_product,product);
        //productDAO.saveProduct(product);
        prodAppRenew.setProduct(product);
        prodAppRenew = prodApplicationsService.saveApplication(prodAppRenew,curUser.getUserId());
        prodAppId = prodAppRenew.getId();
        facesContext.getExternalContext().getFlash().put("prodAppID",prodAppId);
        facesContext.getExternalContext().getFlash().put("parentAppId",parentAppId);
        return "/secure/prodreghome.xhtml";
    }

    private Long getParam(String parameter){
        facesContext = FacesContext.getCurrentInstance();
        String prodAppId=null;
        if (facesContext.getExternalContext().getFlash()!=null)
            prodAppId = (String) facesContext.getExternalContext().getFlash().get(parameter);
        if (prodAppId==null){
            if (facesContext.getExternalContext().getRequestParameterMap()!=null)
                prodAppId = facesContext.getExternalContext().getRequestParameterMap().get(parameter);
        }
        if (prodAppId!=null){
            return Long.parseLong(prodAppId);
        }
        return null;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Long getProdAppId() {
        return prodAppId;
    }

    public void setProdAppId(Long prodAppId) {
        this.prodAppId = prodAppId;
    }

    public Long getParentAppId() {
        return parentAppId;
    }

    public void setParentAppId(Long parentAppId) {
        this.parentAppId = parentAppId;
    }
}
