package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.service.AtcService;
import org.msh.pharmadex.service.InnService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class ProductDisplay {

    @Autowired
    ProdApplicationsService prodApplicationsService;

    @Autowired
    ProductService productService;

    @Autowired
    InnService innService;

    @Autowired
    AtcService atcService;

    private Product product;

    private ProdApplications prodApplications;

    public Product getProduct() {
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setProduct(Product product) {
        this.product = product;
        prodApplications = prodApplicationsService.findProdApplicationByProduct(product.getId());
        prodApplications.setProdAppChecklists(prodApplicationsService.findAllProdChecklist(prodApplications.getId()));
        product.setInns(innService.findInnByProdApp(prodApplications.getId()));
        product.setCompanies(prodApplicationsService.findCompanies(product.getId()));

        prodApplications.setProd(product);


    }


    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }
}
