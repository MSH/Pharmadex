package org.msh.pharmadex.service;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.CustomPIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderLookUpDAO;
import org.msh.pharmadex.dao.iface.POrderDocDAO;
import org.msh.pharmadex.dao.iface.PurOrderDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by usrivastava on 01/16/2015.
 */
@Service
public class POrderService implements Serializable {

    @Autowired
    private PIPOrderLookUpDAO pipOrderLookUpDAO;

    @Autowired
    private CustomPIPOrderDAO customPIPOrderDAO;

    @Autowired
    private PIPOrderDAO pipOrderDAO;

    @Autowired
    private PurOrderDAO purOrderDAO;

    @Autowired
    private POrderDocDAO pOrderDocDAO;

    @Autowired
    private UserService userService;

    public List<PIPOrderLookUp> findPIPCheckList(ApplicantType applicantType, boolean pip) {
        if (applicantType.getId() < 5)
            applicantType.setId(Long.valueOf(2));
        return customPIPOrderDAO.findAllPIPOrderLookUp(applicantType, pip);
    }

    public RetObject saveOrder(POrderBase pipOrderBase) {
        RetObject retObject = new RetObject();
        try {
            if (pipOrderBase instanceof PIPOrder) {
                PIPOrder pipOrder = (PIPOrder) pipOrderBase;
                pipOrder = pipOrderDAO.save(pipOrder);
                retObject.setObj(pipOrder);
            }
            if (pipOrderBase instanceof PurOrder) {
                PurOrder purOrder = (PurOrder) pipOrderBase;
                purOrder = purOrderDAO.save(purOrder);
                retObject.setObj(purOrder);
            }

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

    public RetObject updatePIPOrder(POrderBase pOrderBase) {

        if (pOrderBase instanceof PIPOrder)
            pOrderBase =  pipOrderDAO.save((PIPOrder) pOrderBase);

        return new RetObject("persist", pOrderBase);
    }

    @Transactional
    public PIPOrder findPIPOrderEager(Long pipOrderID) {
        PIPOrder pOrderBase;
        try {
            pOrderBase = pipOrderDAO.findOne(pipOrderID);
            Hibernate.initialize(pOrderBase.getPipProds());
            Hibernate.initialize(pOrderBase.getpOrderChecklists());
            Hibernate.initialize(pOrderBase.getApplicant());
            Hibernate.initialize(pOrderBase.getCreatedBy());
            Hibernate.initialize(pOrderBase.getpOrderComments());
        } catch (Exception ex) {
            ex.printStackTrace();
            pOrderBase = null;
        }
        return pOrderBase;
    }

    @Transactional
    public PurOrder findPurOrderEager(Long purOrderID) {
        PurOrder purOrder;
        try {
            purOrder = purOrderDAO.findOne(purOrderID);
            Hibernate.initialize(purOrder.getPurProds());
//            Hibernate.initialize(purOrder.getPurOrderChecklists());
            Hibernate.initialize(purOrder.getApplicant());
            Hibernate.initialize(purOrder.getCreatedBy());
        } catch (Exception ex) {
            ex.printStackTrace();
            purOrder = null;
        }
        return purOrder;
    }

    public String delete(POrderDoc pOrderDoc) {
        try {
            pOrderDocDAO.delete(pOrderDoc);
            return "success";
        }catch(Exception ex){
            ex.printStackTrace();
            return "fail";
        }
    }

    public String save(ArrayList<POrderDoc> pOrderDocs) {
        try {
            pOrderDocDAO.save(pOrderDocs);
            return "success";
        }catch(Exception ex){
            ex.printStackTrace();
            return "fail";
        }
    }

    public RetObject save(POrderDoc pOrderDoc) {
        try{
            pOrderDoc = pOrderDocDAO.save(pOrderDoc);
            RetObject retObject = new RetObject("persist", pOrderDoc);
            return retObject;
        }catch(Exception ex){
            ex.printStackTrace();
            return new RetObject("fail", null);
        }
    }

    public List<POrderDoc> findPOrderDocs(POrderBase pOrderBase) {
        List<POrderDoc> pOrderDocs = null;
        if(pOrderBase==null)
            pOrderDocs =  new ArrayList<POrderDoc>();
        if(pOrderBase instanceof PIPOrder)
            pOrderDocs = pOrderDocDAO.findByPipOrder_Id(pOrderBase.getId());
        if(pOrderBase instanceof PurOrder)
            pOrderDocs = pOrderDocDAO.findByPurOrder_Id(pOrderBase.getId());
        return pOrderDocs;

    }

    public RetObject NotifyFeeRecieved(POrderBase pOrderBase) {
        pOrderBase.setState(AmdmtState.REVIEW);
        pOrderBase.setUpdatedDate(new Date());
        pOrderBase.setFeeRecieveDate(new Date());
        RetObject retObject = updatePIPOrder(pOrderBase);
        return retObject;
    }
}