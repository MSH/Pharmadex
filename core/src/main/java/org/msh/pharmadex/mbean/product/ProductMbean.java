package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.ProductService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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
    private List<Product> products;
    private List<Product> filteredProducts;


    public List<Product> getProducts() {
        if (products == null)
            products = productService.findAllRegisteredProduct();
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<Product> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    public String goToDetails(Product prod) {
        System.out.println("Product == " + prod.getProdName() + " ID ==" + prod.getId());
        return "productdetail";  //To change body of created methods use File | Settings | File Templates.
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
