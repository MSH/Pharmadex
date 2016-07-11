package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.processes.ReRegistration;
import org.msh.pharmadex.service.ProcessReRegService;
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
 * Created by Одиссей on 26.06.2016.
 */
@ManagedBean
@ViewScoped
public class ReRegListBean implements Serializable {
    @ManagedProperty(value = "#{productService}")
    ProductService productService;

    @ManagedProperty(value = "#{processReRegService}")
    ProcessReRegService processReRegService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

    private ProdTable prodTable;
    private String prodAppNo;
    private List<ReRegistration> reRegAppList;

    public void onItemSelect(SelectEvent event) {
        if(event.getObject() instanceof ProdTable){
            prodTable = (ProdTable) event.getObject();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", prodTable.getProdName()));
        }
    }

    public List<ProdTable> completeProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        List<ProdTable> prods = productService.findAllRegisteredProduct();
        for (ProdTable p : prods) {
            if ((p.getProdName() != null && p.getProdName().toLowerCase().startsWith(query))
                    || (p.getGenName() != null && p.getGenName().toLowerCase().startsWith(query)))
                suggestions.add(p);
        }
        return suggestions;
    }

    public List<ReRegistration> getReRegAppList() {
        return processReRegService.getList();
    }

    public void setReRegAppList(List<ReRegistration> renewAppList) {
        this.reRegAppList = renewAppList;
    }


    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ProcessReRegService getProcessReRegService() {
        return processReRegService;
    }

    public void setProcessReRegService(ProcessReRegService processReRegService) {
        this.processReRegService = processReRegService;
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

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ProdTable getProdTable() {
        return prodTable;
    }

    public void setProdTable(ProdTable prodTable) {
        this.prodTable = prodTable;
    }

    public String getProdAppNo() {
        return prodAppNo;
    }

    public void setProdAppNo(String prodAppNo) {
        this.prodAppNo = prodAppNo;
    }
}
