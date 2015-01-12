package org.msh.pharmadex.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.AmdmtDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.AmdmtState;
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
@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class LicHolderServiceTest {


    @Autowired
    AmdmtService amdmtService;

    @Autowired
    AmdmtDAO amdmtDAO;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testFindAmdmtRecvd() throws Exception {
        Assert.assertEquals(true, true);

        List<ProdApplications> prodAppAmdmts = amdmtService.findAmdmtsRecieved();
        System.out.println("----------- Inside testFindAmdmtRecvd ---------");
        System.out.println("ProdAPPS size ==== " + prodAppAmdmts.size());
        Assert.assertNotNull(prodAppAmdmts);

    }

    @Test
    public void testFindAmdmtByState() throws Exception {
        Assert.assertEquals(true, true);

        List<ProdApplications> prodAppAmdmts = amdmtDAO.findByAmdmtState(AmdmtState.NEW_APPLICATION);
        System.out.println("----------- Inside testFindAmdmtRecvd ---------");
        System.out.println("ProdAPPS size ==== " + prodAppAmdmts.size());
        Assert.assertNotNull(prodAppAmdmts);

    }

}
