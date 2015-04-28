package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.service.ProductService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProductMbean implements Serializable {
    private static final long serialVersionUID = -7982763544138941526L;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    private List<ProdTable> products;
    private List<ProdTable> filteredProducts;

    public String sentToDetail(Long id) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        return "/internal/processreg?faces-redirect=true";
    }

    public List<ProdTable> getProducts() {
        if (products == null)
            products = productService.findAllRegisteredProduct();
        return products;
    }

    public void setProducts(List<ProdTable> products) {
        this.products = products;
    }

    public List<ProdTable> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<ProdTable> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    public String goToDetails(Long id) {
        System.out.println("Product ID ==" + id);
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        return "productdetail";
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
