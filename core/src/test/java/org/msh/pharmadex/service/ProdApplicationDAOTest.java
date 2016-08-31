package org.msh.pharmadex.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

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
public class ProdApplicationDAOTest {


    @Autowired
    ProdApplicationsDAO prodApplicationsDAO;

    @Autowired
    UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindProdExpiring() throws Exception {
        Assert.assertEquals(true, true);

        HashMap<String, Object> params = new HashMap<String, Object>();
        Calendar c = new GregorianCalendar(2012, 00, 01);
        params.put("startDt", c.getTime());
        c.set(2014, 00, 01);
        params.put("endDt", c.getTime());

//        List<ProdApplications> prodApps = prodApplicationsDAO.findProdExpiring(params);
//        Assert.assertNotNull(prodApps);
//
//        params = new HashMap<String, Object>();
//        List<User> users = new ArrayList<User>();
//        users.add(userDAO.findUser(new Long(20)));
//        params.put("users", users);
////        params.put("paymentStatus", PaymentStatus.INVOICE_ISSUED);
//
//        try {
//            prodApps = prodApplicationsDAO.findProdExpiring(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Assert.assertNotNull(prodApps);

    }

    @Test
    public void testFindProdByReviewer() throws Exception {
        Assert.assertEquals(true, true);

        HashMap<String, Object> params = new HashMap<String, Object>();
        List<RegState> regStates = new ArrayList<RegState>();
        regStates.add(RegState.REVIEW_BOARD);
        params.put("reviewer", new Long(5));
        params.put("regState", regStates);

        List<ProdApplications> prodApplicationses = prodApplicationsDAO.findProdAppByReviewer(params);
        Assert.assertNotNull(prodApplicationses);


    }


    @Test
    public void testFindProductByFilter() throws Exception {
        Assert.assertEquals(true, true);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ArrayList<RegState> regStates = new ArrayList<RegState>();
        regStates.add(RegState.NEW_APPL);
        regStates.add(RegState.FEE);
        regStates.add(RegState.NEW_APPL);
        regStates.add(RegState.FEE);
        regStates.add(RegState.NEW_APPL);
        regStates.add(RegState.DEFAULTED);
        regStates.add(RegState.FOLLOW_UP);
        regStates.add(RegState.RECOMMENDED);
        regStates.add(RegState.REVIEW_BOARD);
        regStates.add(RegState.SCREENING);
        regStates.add(RegState.VERIFY);
        regStates.add(RegState.REGISTERED);

        params.put("regState", regStates);
        params.put("userId", 2);
        List<ProdApplications> products = prodApplicationsDAO.getProdAppByParams(params);
        System.out.println("products === " + products.size());
        Assert.assertNotNull(products);
//        Assert.assertEquals("Search by product name", products.size(), 1);

    }
}
