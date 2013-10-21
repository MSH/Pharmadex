package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 10/19/13
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Scheduler implements Serializable {

    @Autowired
    ProdApplicationsDAO prodApplicationsDAO;

//    @Scheduled(cron = "* */10 * * * ? ")
//    public void demoScheduler() {
//        System.out.println("Method executed every 5 seconds");
//
//        System.out.println("Current time is ::" + new Date());
//
//        HashMap<String, Object> params = new HashMap<String, Object>();
////        prodApplicationsDAO.findProdExpiring(params);
//    }

}
