package org.msh.pharmadex.service;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.CustomPIPOrderDAO;
import org.msh.pharmadex.dao.CustomPurOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderLookUpDAO;
import org.msh.pharmadex.dao.iface.PurOrderDAO;
import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.PurOrder;
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
public class PurOrderService implements Serializable{

    @Autowired
    private PIPOrderLookUpDAO pipOrderLookUpDAO;

    @Autowired
    private PurOrderDAO purOrderDAO;

    @Autowired
    private CustomPurOrderDAO customPurOrderDAO;

    public List<PIPOrderLookUp> findPurOrderCheckList() {
        return customPurOrderDAO.findAllPurOrderLookUp();
    }

    public RetObject saveOrder(PurOrder pipOrder) {
        RetObject retObject = new RetObject();
        try {
            retObject.setObj(purOrderDAO.save(pipOrder));
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            retObject.setObj(ex.getMessage());
            return retObject;
        }
        return retObject;


    }

    public RetObject findAllSubmittedPo(User loggedInUserObj, boolean companyUser) {
        RetObject retObject = new RetObject();
        List<PurOrder> pipOrders = null;
        Long applcntId;
        Long userId;

        if (loggedInUserObj == null) {
            retObject.setMsg("error");
        }

        try {
            if (companyUser) {
                applcntId = (loggedInUserObj.getApplicant() != null ? loggedInUserObj.getApplicant().getApplcntId() : null);
                userId = loggedInUserObj.getUserId();
                pipOrders = customPurOrderDAO.findPurOrderByUser(userId, applcntId);
            } else {
                pipOrders = customPurOrderDAO.findAllPIPOrder();
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

    public PurOrder updatePurOrder(PurOrder purOrder) {
        return purOrderDAO.save(purOrder);
    }

    @Transactional
    public PurOrder findPurOrderEager(Long purOrderID) {
        PurOrder purOrder;
        try {
            purOrder = purOrderDAO.findOne(purOrderID);
            Hibernate.initialize(purOrder.getPurProds());
            Hibernate.initialize(purOrder.getPurOrderChecklists());
            Hibernate.initialize(purOrder.getApplicant());
            Hibernate.initialize(purOrder.getCreatedBy());
        } catch (Exception ex) {
            ex.printStackTrace();
            purOrder = null;
        }
        return purOrder;
    }
}