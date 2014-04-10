package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.DrugPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class DrugPriceMBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(DrugPriceMBean.class);
    private static final long serialVersionUID = 5084991828668543L;

    @Autowired
    RegHomeMbean regHomeMbean;

    private DrugPrice selectedDrugPrice;

    public void addDrugPrice() {
        try {
            selectedDrugPrice.setPricing(regHomeMbean.getProdApplications().getPricing());
            regHomeMbean.getDrugPrices().add(selectedDrugPrice);
            regHomeMbean.setShowCompany(false);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getMessage()));
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

}
