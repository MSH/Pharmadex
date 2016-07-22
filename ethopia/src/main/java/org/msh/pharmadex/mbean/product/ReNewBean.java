package org.msh.pharmadex.mbean.product;

import org.hibernate.Hibernate;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.util.Scrooge;

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
    private ProdAppType RenewType;
    private ProdAppType VariationType;
    
    
    public ProdAppType getRenewType() {
		return RenewType;
	}

	public void setRenewType(ProdAppType renewType) {
		RenewType = renewType;
	}

	public ProdAppType getVariationType() {
		return VariationType;
	}

	public void setVariationType(ProdAppType variationType) {
		VariationType = variationType;
	}

	@PostConstruct
    private void init(){
        parentAppId = getParam("prodAppID");
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
        setVariationType(ProdAppType.VARIATION);
        setRenewType(ProdAppType.RENEW);
    }

    public String startReregistration(ProdAppType newtype,Long parentAppId){
        //create copy of inital application and product
        ProdApplications prodAppRenew = new ProdApplications();
        ProdApplications prodApp = prodApplicationsService.findProdApplications(parentAppId);
        Long parentProdId = prodApp.getProduct().getId();
        Scrooge.copyData(prodApp,prodAppRenew);
        prodAppRenew.setProdAppType(newtype);
        Product product = new Product();
        Product reg_product = productDAO.findProductEager(parentProdId);
        Scrooge.copyData(reg_product,product);
        prodAppRenew.setProduct(product);
        prodAppRenew.setParentApplication(prodApp);
        prodAppRenew.setRegState(RegState.NEW_APPL);
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
