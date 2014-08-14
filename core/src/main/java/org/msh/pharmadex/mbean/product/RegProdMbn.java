package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class RegProdMbn {

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    ProdApplicationsService prodApplicationsService;

    @Autowired
    ProcessProdBn processProdBn;

    @Autowired
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
}
