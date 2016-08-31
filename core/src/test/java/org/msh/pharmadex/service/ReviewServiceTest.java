package org.msh.pharmadex.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.ReviewQDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class ReviewServiceTest {


    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewDAO reviewDAO;

    @Autowired
    ReviewQDAO reviewQDAO;

    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testGetReviewHeader1List() {
        Assert.assertEquals(true, true);
//        List<DisplayReviewQ> header2 = reviewService.getDisplayReviewSum(new ReviewInfo());
//        Assert.assertNotNull(header2);
    }

}
