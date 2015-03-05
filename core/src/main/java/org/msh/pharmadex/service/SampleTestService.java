/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class SampleTestService implements Serializable {


    @Autowired
    private SampleTestDAO sampleTestDAO;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    public SampleTest findSampleTest(SampleTest sampleTest) {
        sampleTest = sampleTestDAO.findOne(sampleTest.getId());
//        List<ReviewChecklist> reviewChecklists = review.getReviewChecklists();
//        if (reviewChecklists.size() < 1) {
//            reviewChecklists = new ArrayList<ReviewChecklist>();
//            review.setReviewChecklists(reviewChecklists);
//            List<Checklist> allChecklist = checklistService.getChecklists(review.getProdApplications().getProdAppType(), true);
//            ReviewChecklist eachReviewChecklist;
//            for (int i = 0; allChecklist.size() > i; i++) {
//                eachReviewChecklist = new ReviewChecklist();
//                eachReviewChecklist.setChecklist(allChecklist.get(i));
//                eachReviewChecklist.setReview(review);
//                reviewChecklists.add(eachReviewChecklist);
//            }
//            review.setReviewChecklists(reviewChecklists);
//        }
        return sampleTest;
    }

    public SampleTest findSampleForProd(Long prodApplicationsId) {
        List<SampleTest> sampleTests = sampleTestDAO.findByProdApplications_Id(prodApplicationsId);
        if (sampleTests.size() > 0)
            return sampleTests.get(0);
        else
            return null;

    }

    public RetObject saveSample(SampleTest sampleTest) {

        RetObject retObject = new RetObject();
        try {
            SampleTest sampleTest1 = sampleTestDAO.save(sampleTest);
            retObject.setObj(sampleTest1);
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setObj(ex.getMessage());
            retObject.setMsg("error");
        }
        return retObject;
    }
}
