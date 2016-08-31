package org.msh.pharmadex.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.domain.ProdApplications;
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
public class ApplicantServiceTest {


    @Autowired
    ApplicantService applicantService;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testFindProductByApp() throws Exception {
        Assert.assertEquals(true, true);

        List<ProdApplications> products = applicantService.findRegProductForApplicant(new Long(22));
        System.out.println("products === " + products.size());
        for (ProdApplications p : products) {
            System.out.println("------------------");
            System.out.println("Product name == " + p.getProduct().getProdName());
        }
        Assert.assertNotNull(products);


    }
}
