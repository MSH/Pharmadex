package org.msh.pharmadex.service;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.CustomPIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderLookUpDAO;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Created by usrivastava on 01/16/2015.
 */
@Service
public class PIPOrderService implements Serializable{

    @Autowired
    private PIPOrderLookUpDAO pipOrderLookUpDAO;

    @Autowired
    private PIPOrderDAO pipOrderDAO;

    @Autowired
    private CustomPIPOrderDAO customPIPOrderDAO;

    public List<PIPOrderLookUp> findPIPCheckList() {
        return customPIPOrderDAO.findAllPIPOrderLookUp();
    }

    public RetObject saveOrder(PIPOrder pipOrder) {
        RetObject retObject = new RetObject();
        try {
            retObject.setObj(pipOrderDAO.save(pipOrder));
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            retObject.setObj(ex.getMessage());
            return retObject;
        }
        return retObject;


    }

    public RetObject findAllSubmittedPIP(Long userID, Long applcntId, boolean companyUser) {
        RetObject retObject = new RetObject();
        List<PIPOrder> pipOrders = null;

        if (userID == null) {
            retObject.setMsg("error");
        }

        try {
            if (companyUser) {
               pipOrders = customPIPOrderDAO.findPIPOrderByUser(userID, applcntId);
            } else {
                pipOrders = customPIPOrderDAO.findAllPIPOrder();
            }


            retObject.setObj(pipOrders);
            retObject.setMsg("persist");

        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            retObject.setObj(null);
        }
        return retObject;
    }

    public PIPOrder updatePIPOrder(PIPOrder pipOrder) {
        return pipOrderDAO.save(pipOrder);
    }

    @Transactional
    public PIPOrder findPIPOrderEager(Long pipOrderID) {
        PIPOrder pipOrder;
        try {
            pipOrder = pipOrderDAO.findOne(pipOrderID);
            Hibernate.initialize(pipOrder.getPipProds());
            Hibernate.initialize(pipOrder.getPipOrderChecklists());
            Hibernate.initialize(pipOrder.getApplicant());
            Hibernate.initialize(pipOrder.getCreatedBy());
        } catch (Exception ex) {
            ex.printStackTrace();
            pipOrder = null;
        }
        return pipOrder;
    }
}