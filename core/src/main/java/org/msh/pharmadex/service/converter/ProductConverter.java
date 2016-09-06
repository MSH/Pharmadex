package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProductService;
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
    private ProductService productService;

    private List<ProdTable> products;

    public List<ProdTable> getProducts() {
        if(products == null)
            products = productService.findAllRegisteredProduct();
        return products;
    }

    public void setProducts(List<ProdTable> products) {
        this.products = products;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                for (ProdTable p : getProducts()) {
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
            if (value instanceof Long){
                return String.valueOf(value);
            }else if (value instanceof  Product){
                return String.valueOf(((Product) value).getId());
            }else if (value instanceof  ProdTable){
                ProdTable prodTable = (ProdTable) value;
                return String.valueOf(prodTable.getId());
            }else if (value instanceof  String){
                Product prod = productService.findOneByName((String) value);
                if (prod!=null)
                    return String.valueOf(prod.getId());
            }
        }
        return "";
    }
}


