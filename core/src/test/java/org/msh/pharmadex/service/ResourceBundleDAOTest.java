package org.msh.pharmadex.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.ResourceBundleDAO;
import org.msh.pharmadex.domain.ResourceBundle;
import org.msh.pharmadex.domain.ResourceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
public class ResourceBundleDAOTest {


    @Autowired
    ResourceBundleDAO resourceBundleDAO;


    @Before
    public void setUp() throws Exception {
//        super.setUp();


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSaveLocale() throws Exception {
        Assert.assertEquals(true, true);
//        ResourceBundle rb = new ResourceBundle();
//        rb.setBasename("messages");
//        rb.setLocale(Locale.getDefault().getDisplayName());
//        List<ResourceMessage> resourceMessages = new ArrayList<ResourceMessage>();
//        ResourceMessage rm = new ResourceMessage();
//        rm.setKey("label_home");
//        rm.setValue("Home");
//        resourceMessages.add(rm);
//        rb.setMessages(resourceMessages);
//        resourceBundleDAO.save(rb);
//        Assert.assertEquals(true, true);

    }
}
