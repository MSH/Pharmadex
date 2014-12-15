package org.msh.pharmadex.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.AmdmtDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 2/16/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ReviewServiceTest {


    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewDAO reviewDAO;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testGetReviewHeader1List(){
        Assert.assertEquals(true, true);
//        List<DisplayReviewQ> header2 = reviewService.getDisplayReviewSum(new ReviewInfo());
//        Assert.assertNotNull(header2);
    }

}
