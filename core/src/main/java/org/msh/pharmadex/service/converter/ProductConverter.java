package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@FacesConverter(value = "productConverter")
@Component
public class ProductConverter implements Converter, Serializable {
    private static final long serialVersionUID = -1976549120206232303L;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    private List<Product> products;

    public List<Product> getProducts() {
        if (products == null)
            products = globalEntityLists.getRegProducts();
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
//                int number = Integer.parseInt(submittedValue);
                for (Product p : getProducts()) {
                    if (String.valueOf(p.getId()).equalsIgnoreCase(submittedValue))
                        return p;
                }
            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Product"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }
}


