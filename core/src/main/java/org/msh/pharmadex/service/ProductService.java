package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class ProductService implements Serializable {


    private static final long serialVersionUID = -5511467617579154680L;

    @Autowired
    ProductDAO productDAO;

    public List<Product> findAllRegisteredProduct() {
        return productDAO.findRegProducts();
    }

    public String updateProduct(Product prod) {
        productDAO.updateProduct(prod);
        return "persisted";
    }

    @Autowired
    ApplicantDAO applicantDAO;

    @Transactional
    public Product findProductById(Long id) {
        Product prod = productDAO.findProduct(id);
        return prod;
    }

    public List<Company> findCompaniesByProd(Long id) {
        return productDAO.findCompanies(id);
    }

    public List<Product> findProductByFilter(ProductFilter filter) {
        HashMap<String, Object> params = filter.getFilters();
        return productDAO.findProductByFilter(params);

    }

    public List<Atc> findAtcsByProduct(Long id) {
        return productDAO.findAtcsByProduct(id);
    }

}
