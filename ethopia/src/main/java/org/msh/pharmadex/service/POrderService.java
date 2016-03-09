package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.msh.pharmadex.dao.CustomPIPOrderDAO;
import org.msh.pharmadex.dao.CustomPurOrderDAO;
import org.msh.pharmadex.dao.iface.*;
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
import java.sql.SQLException;
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
    private POrderChecklistDAO pOrderChecklistDAO;

    @Autowired
    private POrderCommentDAO pOrderCommentDAO;

    @Autowired
    private UserService userService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<PIPOrderLookUp> findPIPCheckList(ApplicantType applicantType, boolean pip) {
        Long appID = null;
        if (applicantType.getId() < 5) {
            if (pip)
                appID = (long) 1;
            else
                appID = (long) 2;
        } else {
            appID = applicantType.getId();
        }
        return customPIPOrderDAO.findAllPIPOrderLookUp(appID, pip);
    }

    public POrderBase saveOrder(POrderBase pOrderBase) {
        if (pOrderBase instanceof PIPOrder) {
            PIPOrder pipOrder = (PIPOrder) pOrderBase;
            pipOrder = pipOrderDAO.save(pipOrder);
            return pipOrder;
        }
        if (pOrderBase instanceof PurOrder) {
            PurOrder purOrder = (PurOrder) pOrderBase;
            purOrder = purOrderDAO.save(purOrder);
            return purOrder;

        }
        return pOrderBase;
    }

    public RetObject newOrder(POrderBase pipOrderBase) {
        RetObject retObject = new RetObject();
        RegistrationUtil registrationUtil = new RegistrationUtil();
        try {
            if (pipOrderBase instanceof PIPOrder) {
                PIPOrder pipOrder = (PIPOrder) pipOrderBase;
                pipOrder.setTotalPrice(calculateGrandTotal(pipOrder.getPipProds(), pipOrder.getFreight()));
                String retValue = validate(pipOrder);
                if (retValue.equals("persist")) {
                    pipOrder.setSubmitDate(new Date());
                    pipOrder = pipOrderDAO.save(pipOrder);
                    pipOrder.setPipNo(registrationUtil.generateAppNo(pipOrder.getId(), "PIP"));
                    pipOrder = pipOrderDAO.saveAndFlush(pipOrder);
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
                purOrder.setTotalPrice(calculateGrandTotal(purOrder.getPurProds(), purOrder.getFreight()));
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

    /**
     * Calculates the total price of all the products
     * @param pProdBaseList
     * @param freight
     * @return
     */
    public Double calculateGrandTotal(List pProdBaseList, Double freight){
        if(pProdBaseList == null)
            return 0.0;
        Double grandTotal = freight;
        for(Object obj : pProdBaseList){
            if(obj!=null){
                PProdBase pProdBase = (PProdBase) obj;
                pProdBase.setTotalPrice(pProdBase.getUnitPrice()*pProdBase.getQuantity());
                grandTotal += pProdBase.getTotalPrice();
            }
        }
        return grandTotal;
    }

    /**
     * Method to generated the jasper letter
     * @param pipOrder
     * @param path
     * @param pdfFile
     * @param pOrderComment the comment to be displayed in the letter
     * @return
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     */
    public byte[] generateLetter(POrderBase pipOrder, String path, File pdfFile, String pOrderComment) throws JRException, IOException, SQLException {
        JasperPrint jasperPrint;
        Connection conn = entityManager.unwrap(Session.class).connection();
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("piporderid", pipOrder.getId());
        param.put("pattern", pipOrder.getCurrency().getCurrSym() + "#,##0.00");
        param.put("comment", pOrderComment);
        URL resource = getClass().getResource(path);
        jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdfFile));
        byte[] file = IOUtils.toByteArray(new FileInputStream(pdfFile));
//        conn.close();
        return file;
    }

    public RetObject createAckLetter(POrderBase pipOrderBase) {
        try {
            POrderDoc pOrderDoc = new POrderDoc();
            byte[] file = new byte[0];
            if (pipOrderBase instanceof PIPOrder) {
                File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_ack", ".pdf");
                file = generateLetter(pipOrderBase, "/reports/pip_ack.jasper", invoicePDF, "");
                pOrderDoc.setFileName("PIP_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_ack.pdf");
                pOrderDoc.setPipOrder((PIPOrder) pipOrderBase);
            } else if (pipOrderBase instanceof PurOrder) {
                File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_ack", ".pdf");
                file = generateLetter(pipOrderBase, "/reports/po_ack.jasper", invoicePDF, null);
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");
        }
    }

    public RetObject createApprovalLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_cert", ".pdf");
            byte[] file = generateLetter(pipOrderBase, "/reports/pip_cert.jasper", invoicePDF, null);
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");
        }
    }

    public RetObject createPOApprovalLetter(POrderBase pipOrderBase) {
        try {
            File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_cert", ".pdf");
            byte[] file = generateLetter(pipOrderBase, "/reports/po_cert.jasper", invoicePDF, null);
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");
        }
    }

    public RetObject createRejectionLetter(POrderBase pipOrderBase, POrderComment pOrderComment) {
        try {
            File invoicePDF = File.createTempFile("PIP_" + pipOrderBase.getId() + "_reject", ".pdf");
            byte[] file = generateLetter(pipOrderBase, "/reports/pip_reject.jasper", invoicePDF, pOrderComment.getComment());
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");

        }
    }

    public RetObject createPORejectionLetter(POrderBase pipOrderBase, POrderComment pOrderComment) {
        try {
            File invoicePDF = File.createTempFile("PO_" + pipOrderBase.getId() + "_reject", ".pdf");
            byte[] file = generateLetter(pipOrderBase, "/reports/po_reject.jasper", invoicePDF, pOrderComment.getComment());
            POrderDoc pOrderDoc = new POrderDoc();
            pOrderDoc.setFileName("PO_" + pipOrderBase.getId() + Calendar.getInstance().get(Calendar.YEAR) + "_reject.pdf");
            pOrderDoc.setPurOrder((PurOrder) pipOrderBase);
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
        } catch (SQLException e) {
            e.printStackTrace();
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
                purOrders = customPurOrderDAO.findAllPurOrder();
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
            pipOrder = pipOrderDAO.save(pipOrder);

            if (pipOrder.getState().equals(AmdmtState.APPROVED)) {
                createApprovalLetter(pipOrder);
            } else if (pipOrder.getState().equals(AmdmtState.REJECTED)) {
                createRejectionLetter(pipOrder, pipOrder.getpOrderComments().get(pipOrder.getpOrderComments().size()-1));
            }
        }

        if (pOrderBase instanceof PurOrder) {
            PurOrder purOrder = (PurOrder) pOrderBase;
            purOrder = purOrderDAO.save(purOrder);
            if (purOrder.getState().equals(AmdmtState.APPROVED)) {
                createPOApprovalLetter(purOrder);
            } else if (purOrder.getState().equals(AmdmtState.REJECTED)) {
                createPORejectionLetter(purOrder, purOrder.getpOrderComments().get(purOrder.getpOrderComments().size()-1));
            }
        }

        return new RetObject("persist", pOrderBase);
    }

    @Transactional
    public PIPOrder findPIPOrderByID(Long pipOrderID) {
        PIPOrder pOrderBase;
        try {
            pOrderBase = customPIPOrderDAO.findPIPOrder(pipOrderID);
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
            purOrder = customPurOrderDAO.findPurOrder(purOrderID);
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
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderDocDAO.findByPipOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderDoc>();
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

    public POrderBase findPOrder(String pipNo, boolean purOrder) {
        POrderBase pOrderBase;
        try {
            if (purOrder)
                pOrderBase = purOrderDAO.findByPipNo(pipNo);
            else
                pOrderBase = pipOrderDAO.findByPipNo(pipNo);
        } catch (Exception ex) {
            ex.printStackTrace();
            pOrderBase = null;
        }
        return pOrderBase;
    }

    public List<ProdTable> findProdByLH(Long applcntId) {
        List<ProdTable> prodTables = new ArrayList<ProdTable>();
        if (applcntId != null) {
            prodTables = customPurOrderDAO.findProdByLH(applcntId);

        }
        return prodTables;

    }

    public List<POrderChecklist> findPOrderChecklists(POrderBase pOrderBase) {
        List<POrderChecklist> pOrderDocs = null;
        if (pOrderBase == null)
            pOrderDocs = new ArrayList<POrderChecklist>();
        if (pOrderBase instanceof PIPOrder) {
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderChecklistDAO.findByPipOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderChecklist>();
        }
        if (pOrderBase instanceof PurOrder) {
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderChecklistDAO.findByPurOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderChecklist>();

        }
        return pOrderDocs;
    }

    public List<POrderComment> findPOrderComments(POrderBase pOrderBase) {
        List<POrderComment> pOrderDocs = null;
        if (pOrderBase == null)
            pOrderDocs = new ArrayList<POrderComment>();
        if (pOrderBase instanceof PIPOrder) {
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderCommentDAO.findByPipOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderComment>();
        }
        if (pOrderBase instanceof PurOrder) {
            if (pOrderBase.getId() != null)
                pOrderDocs = pOrderCommentDAO.findByPurOrder_Id(pOrderBase.getId());
            else
                pOrderDocs = new ArrayList<POrderComment>();

        }
        return pOrderDocs;
    }

    @Autowired
    private PipProdDAO pipProdDAO;
    @Autowired
    private PurProdDAO purProdDAO;

    public String removeProd(PProdBase purProd) {
        try {
            if (purProd == null)
                return null;

            if (purProd instanceof PIPProd) {
                pipProdDAO.delete((PIPProd) purProd);
            } else if (purProd instanceof PurProd) {
                purProdDAO.delete((PurProd) purProd);
            }

            return "persist";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}