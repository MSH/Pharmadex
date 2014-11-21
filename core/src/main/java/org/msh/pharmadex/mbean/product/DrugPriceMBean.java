package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.DrugPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class DrugPriceMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(DrugPriceMBean.class);
    private static final long serialVersionUID = 5084991828668543L;

    @ManagedProperty(value = "#{regHomeMbean}")
    RegHomeMbean regHomeMbean;

    private DrugPrice selectedDrugPrice;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void addDrugPrice() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            selectedDrugPrice.setPricing(regHomeMbean.getProdApplications().getPricing());
            regHomeMbean.getDrugPrices().add(selectedDrugPrice);
            regHomeMbean.setShowDrugPrice(false);
            selectedDrugPrice = new DrugPrice();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("msgs"), e.getMessage()));
        }
    }

    public String cancelAdd() {
        return null;
    }

    public DrugPrice getSelectedDrugPrice() {
        if (selectedDrugPrice == null)
            selectedDrugPrice = new DrugPrice();
        return selectedDrugPrice;
    }

    public void setSelectedDrugPrice(DrugPrice drugPrice) {
        this.selectedDrugPrice = drugPrice;
    }

    public RegHomeMbean getRegHomeMbean() {
        return regHomeMbean;
    }

    public void setRegHomeMbean(RegHomeMbean regHomeMbean) {
        this.regHomeMbean = regHomeMbean;
    }
}
