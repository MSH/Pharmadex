package org.msh.pharmadex.service.converter;

import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.util.Scrooge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Одиссей on 29.07.2016.
 */
@FacesConverter(value = "productLicConverter")
@Component
@Scope("singleton")
public class ProductLicConverter implements Converter, Serializable {
    @Autowired
    private LicenseHolderService licenseHolderService;
    private List<Product> products;

    public List<Product> getProducts(){
        if (products==null){
            Long id = Scrooge.beanParam("licenseHoldersId");
            if (id!=null){
                LicenseHolder lc = licenseHolderService.findLicHolder(id);
                if (lc!=null)
                    products = lc.getProducts();
            }

        }
        return  products;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }
}
