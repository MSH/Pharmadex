package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class RegProdMbn {

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

    private Product selectedProd;


    public List<Product> completeProduct(String query) {
        List<Product> suggestions = new ArrayList<Product>();
        for (Product p : globalEntityLists.getRegProducts()) {
            if ((p.getProdName() != null && p.getProdName().toLowerCase().startsWith(query))
                    || (p.getGenName() != null && p.getGenName().toLowerCase().startsWith(query))
                    || (p.getApprvdName() != null && p.getApprvdName().toLowerCase().startsWith(query)))
                suggestions.add(p);
        }
        return suggestions;
    }

    public String searchProduct() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (selectedProd == null)
            return null;

        userSession.setProdApplications(selectedProd.getProdApplications());
        userSession.setProduct(selectedProd);

        ProdApplications pa = selectedProd.getProdApplications();
        if (pa != null) {
            processProdBn.setProdApplications(pa);
            return "/internal/processreg.faces";
        } else {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global_fail"), bundle.getString("legacy_prod_message")));
            return null;
        }
    }

    public Product getSelectedProd() {
        return selectedProd;
    }

    public void setSelectedProd(Product selectedProd) {
        this.selectedProd = selectedProd;
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

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
