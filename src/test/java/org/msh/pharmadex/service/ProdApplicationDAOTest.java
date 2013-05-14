package org.msh.pharmadex.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 2/16/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ProdApplicationDAOTest {


    @Autowired
    ProdApplicationsDAO prodApplicationsDAO;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testFindProductByFilter() throws Exception {
        Assert.assertEquals(true, true);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ArrayList<RegState> regStates = new ArrayList<RegState>();
        regStates.add(RegState.NEW_APPL);
        regStates.add(RegState.FEE);

        params.put("regState", regStates);
        params.put("userId", 2);
        List<ProdApplications> products = prodApplicationsDAO.getProdAppByParams(params);
        System.out.println("products === " + products.size());
        Assert.assertNotNull(products);
        Assert.assertEquals("Search by product name", products.size(), 1);

    }
}
