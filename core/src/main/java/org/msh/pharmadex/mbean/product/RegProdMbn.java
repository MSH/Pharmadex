package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.primefaces.event.SelectEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class RegProdMbn implements Serializable {

    @ManagedProperty(value = "#{productService}")
    ProductService productService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

    private ProdTable prodTable;

    public void onItemSelect(SelectEvent event) {
        if(event.getObject() instanceof ProdTable){
            prodTable = (ProdTable) event.getObject();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", prodTable.getProdName()));
        }
    }

    public List<ProdTable> completeProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        for (ProdTable p : productService.findAllRegisteredProduct()) {
            if ((p.getProdName() != null && p.getProdName().toLowerCase().startsWith(query))
                    || (p.getGenName() != null && p.getGenName().toLowerCase().startsWith(query)))
//                    || (p.getApprvdName() != null && p.getApprvdName().toLowerCase().startsWith(query)))
                suggestions.add(p);
        }
        return suggestions;
    }

    public String searchProduct() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (prodTable == null)
            return null;
        return "/internal/processreg.faces";
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ProdTable getProdTable() {
        return prodTable;
    }

    public void setProdTable(ProdTable prodTable) {
        this.prodTable = prodTable;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
