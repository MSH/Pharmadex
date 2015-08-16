package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.impl.SessionImpl;
import org.msh.pharmadex.dao.CustomPIPOrderDAO;
import org.msh.pharmadex.dao.CustomPurOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderDAO;
import org.msh.pharmadex.dao.iface.PIPOrderLookUpDAO;
import org.msh.pharmadex.dao.iface.POrderDocDAO;
import org.msh.pharmadex.dao.iface.PurOrderDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

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
    private CustomPurOrderDAO customPurOrderDAO;

    @Autowired
    private PIPOrderDAO pipOrderDAO;

    @Autowired
    private PurOrderDAO purOrderDAO;

    @Autowired
    private POrderDocDAO pOrderDocDAO;

    @Autowired
    private UserService userService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<PIPOrderLookUp> findPIPCheckList(ApplicantType applicantType, boolean pip) {
        if (applicantType.getId() < 5)
            applicantType.setId((long) 2);
        return customPIPOrderDAO.findAllPIPOrderLookUp(applicantType.getId(), pip);
    }

    public RetObject saveOrder(POrderBase pipOrderBase) {
        RetObject retObject = new RetObject();
        RegistrationUtil registrationUtil = new RegistrationUtil();
        try {
            if (pipOrderBase instanceof PIPOrder) {
                PIPOrder pipOrder = (PIPOrder) pipOrderBase;
                String retValue = validate(pipOrder);
                if (retValue.equals("persist")) {
                    pipOrder.setSubmitDate(new Date());
                    pipOrder = pipOrderDAO.save(pipOrder);
                    pipOrder.setPipNo(registrationUtil.generateAppNo(pipOrder.getId(), "PIP"));
                    pipOrder = pipOrderDAO.save(pipOrder);
                    retObject = createAckLetter(pipOrder);
                    if (!retObject.getMsg().equals("error")) {
                        retObject = new RetObject("persist", pipOrder);
                    } else {
                        retObject.setObj(pipOrder);
                        retObject.setMsg("letter_error");
                    }
                } else {
                    retObject.setMsg(retValue);
                }
            }

            if (pipOrderBase instanceof PurOrder) {
                PurOrder purOrder = (PurOrder) pipOrderBase;
                String retValue = validate(purOrder);
                if (retValue.equals("persist")) {
                    purOrder.setSubmitDate(new Date());
                    purOrder = purOrderDAO.save(purOrder);
                    purOrder.setPipNo(registrationUtil.generateAppNo(purOrder.getId(), "PO"));
                    purOrder = purOrderDAO.save(purOrder);
                    retObject = createAckLetter(purOrder);
                    retObject = new RetObject("persist", purOrder);
                    if (!retObject.getMsg().equals("error")) {
                        retObject = new RetObject("persist", purOrder);
                    } else {
                        retObject.setObj(purOrder);
                        retObject.setMsg("letter_error");
                    }
                } else {
                    retObject.setMsg(retValue);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            retObject.setObj(ex.getMessage());
            return retObject;
        }
        return retObject;


    }

    public byte[] generateLetter(Long piporderid, String path, File pdfFile) throws JRException, IOException {
        JasperPrint jasperPrint;
        Session hibernateSession = entityManager.unwrap(Session.class);
        SessionImpl session = (SessionImpl) hibernateSession;
        Connection conn = session.connection();
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("piporderid", piporderid);
        URL resource = getClass().getResource(path);
        jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdfFile));
        byte[] file = IOUtils.toByteArray(new FileInputStream(pdfFile));
        return file;
    }

    public RetObject createAckLetter(POrderBase pipOrderBase) {
        try {
            POrderDoc pOrderDoc = new POrderDoc();
            byte[] file = new byte[0];
            if(pipOrderBase instanceof PIPOrder) {
                File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_ack", ".pdf");
                file = generateLetter(pipOrderBase.getId(), "/reports/pip_ack.jasper", invoicePDF);
                pOrderDoc.setFileName("PIP_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_ack.pdf");
                pOrderDoc.setPipOrder((PIPOrder) pipOrderBase);
            }else if(pipOrderBase instanceof PurOrder) {
                File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_ack", ".pdf");
                file = generateLetter(pipOrderBase.getId(), "/reports/po_ack.jasper", invoicePDF);
                pOrderDoc.setFileName("PO_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_ack.pdf");
                pOrderDoc.setPurOrder((PurOrder) pipOrderBase);
            }
            pOrderDoc.setContentType("application/pdf");
            pOrderDoc.setUploadedBy(pipOrderBase.getCreatedBy());
            pOrderDoc.setRegState(pipOrderBase.getState());
            pOrderDoc.setTitle("Acknowledgment Letter");
            pOrderDoc.setComment("Automated generated acknowledgement Letter");
            pOrderDoc.setFile(file);
            return save(pOrderDoc);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    public RetObject createApprovalLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_cert", ".pdf");
            byte[] file = generateLetter(pipOrderBase.getId(), "/reports/pip_cert.jasper", invoicePDF);
            POrderDoc pOrderDoc = new POrderDoc();
            pOrderDoc.setFileName("PIP_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_cert.pdf");
            pOrderDoc.setPipOrder((PIPOrder) pipOrderBase);
            pOrderDoc.setContentType("application/pdf");
            pOrderDoc.setUploadedBy(pipOrderBase.getCreatedBy());
            pOrderDoc.setRegState(pipOrderBase.getState());
            pOrderDoc.setTitle("Approval Certificate");
            pOrderDoc.setComment("Automated generated Approval Certificate");
            pOrderDoc.setFile(file);
            return save(pOrderDoc);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    public RetObject createPOApprovalLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_cert", ".pdf");
            byte[] file = generateLetter(pipOrderBase.getId(), "/reports/po_cert.jasper", invoicePDF);
            POrderDoc pOrderDoc = new POrderDoc();
            pOrderDoc.setFileName("PO_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_cert.pdf");
            pOrderDoc.setPurOrder((PurOrder) pipOrderBase);
            pOrderDoc.setContentType("application/pdf");
            pOrderDoc.setUploadedBy(pipOrderBase.getCreatedBy());
            pOrderDoc.setRegState(pipOrderBase.getState());
            pOrderDoc.setTitle("Approval Certificate");
            pOrderDoc.setComment("Automated generated Approval Certificate");
            pOrderDoc.setFile(file);
            return save(pOrderDoc);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    public RetObject createRejectionLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_reject", ".pdf");
            byte[] file = generateLetter(pipOrderBase.getId(), "/reports/pip_reject.jasper", invoicePDF);
            POrderDoc pOrderDoc = new POrderDoc();
            pOrderDoc.setFileName("PIP_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_reject.pdf");
            pOrderDoc.setPipOrder((PIPOrder) pipOrderBase);
            pOrderDoc.setContentType("application/pdf");
            pOrderDoc.setUploadedBy(pipOrderBase.getCreatedBy());
            pOrderDoc.setRegState(pipOrderBase.getState());
            pOrderDoc.setTitle("Rejection Letter");
            pOrderDoc.setComment("Automated generated Rejection Letter");
            pOrderDoc.setFile(file);
            return save(pOrderDoc);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    public RetObject createPORejectionLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_reject", ".pdf");
            byte[] file = generateLetter(pipOrderBase.getId(), "/reports/po_reject.jasper", invoicePDF);
            POrderDoc pOrderDoc = new POrderDoc();
            pOrderDoc.setFileName("PO_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_reject.pdf");
            pOrderDoc.setPipOrder((PIPOrder) pipOrderBase);
            pOrderDoc.setContentType("application/pdf");
            pOrderDoc.setUploadedBy(pipOrderBase.getCreatedBy());
            pOrderDoc.setRegState(pipOrderBase.getState());
            pOrderDoc.setTitle("Rejection Letter");
            pOrderDoc.setComment("Automated generated Rejection Letter");
            pOrderDoc.setFile(file);
            return save(pOrderDoc);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        }
    }

    private String validate(PIPOrder pipOrder) {
        String retValue = "persist";
        List<POrderChecklist> pOrderChecklists = pipOrder.getpOrderChecklists();
        if (pipOrder.getPipProds() == null || pipOrder.getPipProds().size() < 1) {
            retValue = "no_prod";
        }

        for (POrderChecklist pOrderChecklist : pOrderChecklists) {
            YesNoNA value = pOrderChecklist.getValue();
            if (!pOrderChecklist.getPipOrderLookUp().isHeader()) {
                if (value == null || value.equals(YesNoNA.NO)) {
                    retValue = "missing_doc";
                    break;
                }
            }
        }

        return retValue;
    }

    private String validate(PurOrder purOrder) {
        String retValue = "persist";
        List<POrderChecklist> pOrderChecklists = purOrder.getpOrderChecklists();
        if (purOrder.getPurProds() == null || purOrder.getPurProds().size() < 1) {
            retValue = "no_prod";
        }

        for (POrderChecklist pOrderChecklist : pOrderChecklists) {
            YesNoNA value = pOrderChecklist.getValue();
            if (!pOrderChecklist.getPipOrderLookUp().isHeader()) {
                if (value == null || value.equals(YesNoNA.NO)) {
                    retValue = "missing_doc";
                    break;
                }
            }
        }

        return retValue;
    }

    public RetObject findAllSubmittedPIP(Long userID, Long applcntId, boolean companyUser) {
        RetObject retObject = new RetObject();
        List<PIPOrder> pipOrders;

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

    public RetObject findAllSubmittedPO(Long userID, Long applcntId, boolean companyUser) {
        RetObject retObject = new RetObject();
        List<PurOrder> purOrders;

        if (userID == null) {
            retObject.setMsg("error");
        }

        try {
            if (companyUser) {
                purOrders = customPurOrderDAO.findPurOrderByUser(userID, applcntId);
            } else {
                purOrders = customPurOrderDAO.findAllPIPOrder();
            }


            retObject.setObj(purOrders);
            retObject.setMsg("persist");

        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            retObject.setObj(null);
        }
        return retObject;
    }

    public RetObject updatePIPOrder(POrderBase pOrderBase) {

        if (pOrderBase instanceof PIPOrder) {
            PIPOrder pipOrder = (PIPOrder) pOrderBase;
            pOrderBase = pipOrderDAO.save((PIPOrder) pOrderBase);
            if (pipOrder.getState().equals(AmdmtState.APPROVED)) {
                createApprovalLetter(pipOrder);
            } else if (pipOrder.getState().equals(AmdmtState.REJECTED)) {
                createRejectionLetter(pipOrder);
            }
        }

        if (pOrderBase instanceof PurOrder) {
            PurOrder purOrder = (PurOrder) pOrderBase;
            pOrderBase = purOrderDAO.save(purOrder);
            if (pOrderBase.getState().equals(AmdmtState.APPROVED)) {
                createPOApprovalLetter(purOrder);
            } else if (purOrder.getState().equals(AmdmtState.REJECTED)) {
                createPORejectionLetter(purOrder);
            }
        }

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
        } catch (Exception ex) {
            ex.printStackTrace();
            return "fail";
        }
    }

    public String save(ArrayList<POrderDoc> pOrderDocs) {
        try {
            pOrderDocDAO.save(pOrderDocs);
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "fail";
        }
    }

    public RetObject save(POrderDoc pOrderDoc) {
        try {
            pOrderDoc = pOrderDocDAO.save(pOrderDoc);
            return new RetObject("persist", pOrderDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RetObject("fail", null);
        }
    }

    public List<POrderDoc> findPOrderDocs(POrderBase pOrderBase) {
        List<POrderDoc> pOrderDocs = null;
        if (pOrderBase == null)
            pOrderDocs = new ArrayList<POrderDoc>();
        if (pOrderBase instanceof PIPOrder) {
            pOrderDocs = pOrderDocDAO.findByPipOrder_Id(pOrderBase.getId());
        }
        if (pOrderBase instanceof PurOrder) {
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderDocDAO.findByPurOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderDoc>();

        }
        return pOrderDocs;

    }

    public RetObject NotifyFeeRecieved(POrderBase pOrderBase) {
        pOrderBase.setState(AmdmtState.REVIEW);
        pOrderBase.setUpdatedDate(new Date());
        pOrderBase.setFeeRecieveDate(new Date());
        return updatePIPOrder(pOrderBase);
    }

    public POrderBase findPOrder(String pipNo) {
        return pipOrderDAO.findByPipNo(pipNo);
    }

    public List<ProdTable> findProdByLH(Long applcntId) {
        List<ProdTable> prodTables = new ArrayList<ProdTable>();
        if (applcntId != null) {
            prodTables = customPurOrderDAO.findProdByLH(applcntId);

        }
        return prodTables;

    }
}