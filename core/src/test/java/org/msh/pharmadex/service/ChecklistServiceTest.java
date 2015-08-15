package org.msh.pharmadex.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.dao.iface.ChecklistDAO;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdAppType;
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
public class ChecklistServiceTest {


    @Autowired
    ChecklistService checklistService;

    @Autowired
    ChecklistDAO checklistDAO;

    private int genCount;
    private int newMedCount;
    private int recognizedCount;

    @Before
    public void setUp() throws Exception {
//        super.setUp();

        genCount = checklistDAO.findByGenMed(true).size();
        newMedCount = checklistDAO.findByNewMed(true).size();
        recognizedCount = checklistDAO.findByHeaderAndRecognizedMed(true, true).size();

    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testGetChecklist() throws Exception {
        Assert.assertEquals(true, true);

        ProdApplications prodApplications = new ProdApplications();
        prodApplications.setSra(false);
        prodApplications.setProdAppType(ProdAppType.GENERIC);

        List<Checklist> genChecklists = checklistService.getChecklists(prodApplications, true);
        Assert.assertNotNull(genChecklists);
        Assert.assertEquals(genChecklists.size(), genCount);

        prodApplications.setProdAppType(ProdAppType.NEW_CHEMICAL_ENTITY);
        List<Checklist> newMedChecklists = checklistService.getChecklists(prodApplications, true);
        Assert.assertNotNull(newMedChecklists);
        Assert.assertEquals(newMedChecklists.size(), newMedCount);

//        List<Checklist> recognizedChecklists = checklistService.getChecklists(ProdAppType.RECOGNIZED, true);
//        Assert.assertNotNull(recognizedChecklists);
//        Assert.assertEquals(recognizedChecklists.size(), recognizedCount);
    }


}
