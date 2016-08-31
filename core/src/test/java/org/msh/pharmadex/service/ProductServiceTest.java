package org.msh.pharmadex.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 2/16/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductServiceTest {


    @Autowired
    ProductService productService;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindRegProducts(){
        Assert.assertEquals(true, true);
        List<ProdTable> prodTables = productService.findAllRegisteredProduct();
        Assert.assertNotNull(prodTables);
//        Assert.assertEquals(prodTables.size()>0, true);
    }

    @Test
    public void testFindProductByFilter() throws Exception {
        Assert.assertEquals(true, true);

        ProductFilter filter = new ProductFilter();
        filter.setProdName("Ranlor");
        List<Product> products = productService.findProductByFilter(filter);
        System.out.println("products === " + products.size());
        Assert.assertNotNull(products);
        Assert.assertEquals("Search by product name", products.size(), 0);

//
//        filter = new ProductFilter();
//        Applicant applicant = new Applicant();
//        applicant.setApplcntId(Long.valueOf(140));
//        filter.setApplicant(applicant);
//        products = productService.findProductByFilter(filter);
//        System.out.println("products === " + products.size());
//        Assert.assertNotNull(products);
//        Assert.assertEquals("Search by applicant", products.size(), 30);

    }

    @Test
    public void testFindProductEager() {
        Assert.assertEquals(true, true);
//
//        Product prod = productService.findProduct(Long.valueOf(4781));
//        Assert.assertNotNull(prod);

    }
}
