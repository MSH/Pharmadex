package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.impl.SessionImpl;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.CountryDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProdApplicationsService implements Serializable {

    private static final long serialVersionUID = 5061629028904167436L;
    @Resource
    ProdApplicationsDAO prodApplicationsDAO;

    @Autowired
    UserService userService;
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    AtcDAO atcDAO;
    @Autowired
    AppointmentDAO appointmentDAO;
    @Autowired
    WorkspaceDAO workspaceDAO;
    @Autowired
    ProdAppChecklistDAO prodAppChecklistDAO;
    @Autowired
    ChecklistDAO checklistDAO;
    @Autowired
    ForeignAppStatusDAO foreignAppStatusDAO;
    ProdApplications prodApp;
    Product product;
    @Autowired
    private RevDeficiencyDAO revDeficiencyDAO;
    @Autowired
    private CountryDAO countryDAO;
    @Autowired
    private ProdAppLetterDAO prodAppLetterDAO;
    private List<ProdApplications> prodApplications;
    @Autowired
    private DosageFormService dosageFormService;
    @Autowired
    private StatusUserDAO statusUserDAO;
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private DosUomDAO dosUomDAO;
    @Autowired
    private AdminRouteDAO adminRouteDAO;
    @Autowired
    private PharmClassDAO pharmClassDAO;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ProdApplications findProdApplications(long id) {
        ProdApplications prodApp = prodApplicationsDAO.findProdApplications(id);
        return prodApp;
    }

    public List<RegState> nextStepOptions(RegState regState, UserSession userSession, boolean reviewStatus) {
        RegState[] options = null;
        switch (regState) {
            case NEW_APPL:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.FEE;
                break;
            case FEE:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.VERIFY;
                break;
            case VERIFY:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.SCREENING;
                break;
            case SCREENING:
                if (!userSession.isStaff()) {
                    options = new RegState[2];
                    options[1] = RegState.REVIEW_BOARD;
                } else {
                    options = new RegState[1];
                }
                options[0] = RegState.FOLLOW_UP;
                break;
            case REVIEW_BOARD:
                if (userSession.isAdmin() || userSession.isModerator()) {
                    if (reviewStatus) {
                        options = new RegState[3];
                        options[0] = RegState.FOLLOW_UP;
                        options[1] = RegState.RECOMMENDED;
                        options[2] = RegState.NOT_RECOMMENDED;
                    } else {
                        options = new RegState[1];
                        options[0] = RegState.FOLLOW_UP;
                    }
                } else {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                }
                break;
            case RECOMMENDED:
                if (userSession.isAdmin() || userSession.isModerator() || userSession.isHead()) {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                    options[0] = RegState.REJECTED;
                }
                break;
            case REGISTERED:
                options = new RegState[3];
                options[0] = RegState.DISCONTINUED;
                options[1] = RegState.XFER_APPLICANCY;
                break;
            case FOLLOW_UP:
                options = new RegState[7];
                options[0] = RegState.FEE;
                options[1] = RegState.VERIFY;
                options[2] = RegState.SCREENING;
                options[3] = RegState.REVIEW_BOARD;
                options[4] = RegState.SCREENING;
                options[5] = RegState.REVIEW_BOARD;
                options[6] = RegState.DEFAULTED;
                break;
            case NOT_RECOMMENDED:
                options = new RegState[1];
                options[0] = RegState.REJECTED;
                break;
        }
        return Arrays.asList(options);


    }

    public List<ProdApplications> getApplications() {
        if (prodApplications == null)
            prodApplications = prodApplicationsDAO.allProdApplications();
        return prodApplications;
    }

    public void refresh() {
        prodApplications = null;
    }

    public List<ProdApplications> getSavedApplications(Long userId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<RegState> regState = new ArrayList<RegState>();
        regState.add(RegState.SAVED);
        params.put("regState", regState);
        params.put("userId", userId);
        params.put("createdBy", userId);
        return prodApplicationsDAO.getProdAppByParams(params);
    }

    public ArrayList<ProdApplications> findExpiringProd() {
        Calendar currDate = Calendar.getInstance();
        currDate.add(Calendar.MONTH, 1);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("startDt", currDate.getTime());
        currDate.add(Calendar.MONTH, 1);
        params.put("endDt", currDate.getTime());

        ArrayList<ProdApplications> prodApps = prodApplicationsDAO.findProdExpiring(params);


        return prodApps;
    }

    public ArrayList<ProdApplications> findExpiredProd() {
        Calendar currDate = Calendar.getInstance();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("regExpDate", currDate.getTime());
        ArrayList<ProdApplications> prodApps = (ArrayList<ProdApplications>) prodApplicationsDAO.getProdAppByParams(params);


        return prodApps;
    }

    public List<ProdApplications> getSubmittedApplications(UserSession userSession) {
        List<ProdApplications> prodApplicationses = null;
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (userSession.isAdmin()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FEE);
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            params.put("regState", regState);
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isModerator()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            params.put("moderatorId", userSession.getLoggedINUserID());
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isReviewer()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            if (workspaceDAO.findAll().get(0).isDetailReview())
                params.put("reviewerInfoId", userSession.getLoggedINUserID());
            else
                params.put("reviewerId", userSession.getLoggedINUserID());
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isHead()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            regState.add(RegState.REJECTED);
            params.put("regState", regState);
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isStaff()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.FOLLOW_UP);
            params.put("regState", regState);
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isCompany()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            regState.add(RegState.REGISTERED);
            params.put("regState", regState);
            params.put("userId", userSession.getLoggedINUserID());
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isLab()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            params.put("regState", regState);

        }
        prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        return prodApplicationses;
    }

    @Transactional
    public ProdApplications saveApplication(ProdApplications prodApplications, User loggedInUserObj) {
        if (prodApplications.getProduct().getId() == null) {
            prodApplications.getProduct().setCreatedBy(loggedInUserObj);
            prodApplications.setSubmitDate(new Date());
        }

//        applicantDAO.updateApplicant(prodApplications.getProd().getApplicant());

//        prodApplications.getProd().setDosForm(
//                dosageFormService.findDosagedForm(prodApplications.getProd().getDosForm().getUid()));

        if (prodApplications.getAppointment() != null)
            appointmentDAO.save(prodApplications.getAppointment());

        prodApplications = prodApplicationsDAO.updateApplication(prodApplications);
        return prodApplications;
    }

    @Transactional
    public RetObject updateProdApp(ProdApplications prodApplications, Long loggedInUserID) {
        RetObject retObject;
        User loggedInUser = userService.findUser(loggedInUserID);
        if (prodApplications == null) {
            retObject = new RetObject("empty_prodApp", null);
        }
        if (prodApplications.getProduct() == null) {
            retObject = new RetObject("empty_product", null);
        }

        try {
            prodApplications.setUpdatedDate(new Date());
            prodApplications.setUpdatedBy(loggedInUser);
            if (prodApplications.getProduct().getId() == null) {
                productDAO.saveProduct(prodApplications.getProduct());
            }
            if (prodApplications.getId() == null) {
                prodApplications.setApplicant(applicantDAO.findApplicant(prodApplications.getApplicant().getApplcntId()));
                prodApplicationsDAO.saveApplication(prodApplications);
            } else
                prodApplications = prodApplicationsDAO.updateApplication(prodApplications);
            prodApplications = prodApplicationsDAO.findProdApplications(prodApplications.getId());
            retObject = new RetObject("persist", prodApplications);
            return retObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RetObject(ex.getMessage(), null);
        }
    }

    @Transactional
    public List<ProdAppChecklist> findAllProdChecklist(Long prodAppId) {
        return prodAppChecklistDAO.findByProdApplications_IdOrderByIdAsc(prodAppId);
    }

    @Transactional
    public List<Checklist> findAllChecklist() {
        return (List<Checklist>) checklistDAO.findAll();
    }

    @Transactional
    public ProdApplications findProdApplicationByProduct(Long id) {
        return prodApplicationsDAO.findProdApplicationByProduct(id);
    }

    public StatusUser findStatusUser(Long prodAppId) {
        return statusUserDAO.findByProdApplications_Id(prodAppId);
    }

    public String saveProcessors(StatusUser module) {
        statusUserDAO.saveAndFlush(module);
        return "success";
    }

    public List<ProdApplications> findPayNotified() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("paymentStatus", PaymentStatus.PAID);
        return prodApplicationsDAO.findPendingRenew(params);
    }

    public JasperPrint initRegCert() throws JRException {
//        Letter letter = letterService.findByLetterType(LetterType.INVOICE);
//        String body = letter.getBody();
//        MessageFormat mf = new MessageFormat(body);
//        Object[] args = {prodApp.getProdName(), prodApp.getApplicant().getAppName(), prodApp.getProdApplications().getId()};
//        body = mf.format(args);

        String regDt = DateFormat.getDateInstance().format(prodApp.getRegistrationDate());
        String expDt = DateFormat.getDateInstance().format(prodApp.getRegExpiryDate());

        URL resource = getClass().getResource("/reports/reg_letter.jasper");
        HashMap param = new HashMap();
        param.put("regName", product.getProdName());
        param.put("regNumber", prodApp.getProdRegNo());
        param.put("genName", product.getGenName());
        param.put("adminRoute", product.getAdminRoute().getName());
//        param.put("regType", product.getProdType());
        param.put("shelfLife", product.getShelfLife());

        String inns = "";
        if (product.getInns().size() > 0) {
            for (ProdInn prodinn : product.getInns()) {
                inns += prodinn.getInn().getName() + ", ";
            }
            inns = inns.substring(0, inns.length() - 2);
        }

        param.put("activeIngredient", inns);
        param.put("appName", prodApp.getApplicant().getAppName());

        String companyName = "";
        String fprcName = "";
        String fprrName = "";
//        for (Company c : product.getCompanies()) {
//            if (c.getCompanyType().equals(CompanyType.FIN_PROD_MANUF))
//                companyName = c.getCompanyName();
//            if (c.getCompanyType().equals(CompanyType.FPRC))
//                fprcName = c.getCompanyName();
//            if (c.getCompanyType().equals(CompanyType.FPRR))
//                fprrName = c.getCompanyName();
//        }

        param.put("manufName", product.getManufName());
        param.put("fprcName", fprcName);
        param.put("frrName", fprrName);
        param.put("regDate", regDt);
        param.put("issueDate", regDt);
        param.put("dosform", product.getDosForm().getDosForm());
        param.put("conditions", "Attached");


//        param.put("subject", "Subject: "+letter.getSubject()+" "+ prodApp.getProdName() + " ");
//        param.put("body", body);
//        param.put("body", "Thank you for applying to register " + prodApp.getProdName() + " manufactured by " + prodApp.getApplicant().getAppName()
//                + ". Your application is successfully submitted and the application number is " + prodApp.getProdApplications().getId() + ". "
//                +"Please use this application number for any future correspondence.");
        param.put("footer", "Johannes");

        Calendar calendar = Calendar.getInstance();
        param.put("currDay", calendar.get(Calendar.DAY_OF_MONTH));
        String currMnthYr = "";
        currMnthYr = "" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
        param.put("currDay", "" + calendar.get(Calendar.DAY_OF_MONTH));

        return JasperFillManager.fillReport(resource.getFile(), param);
    }

    public JasperPrint initRejCert() throws JRException {
        URL resource = getClass().getResource("/reports/rejection_letter.jasper");
        HashMap param = new HashMap();
        param.put("appName", prodApp.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength() + product.getDosUnit());
        param.put("dosForm", product.getDosForm().getDosForm());
        param.put("manufName", product.getManufName());
        param.put("appType", "New Medicine Registration");
        param.put("subject", "Sample request letter for  " + product.getProdName());
        param.put("address1", prodApp.getApplicant().getAddress().getAddress1());
        param.put("address2", prodApp.getApplicant().getAddress().getAddress2());
        param.put("country", prodApp.getApplicant().getAddress().getCountry().getCountryName());
        //        param.put("cso",userS.getName()); 
        param.put("date", new Date());
        param.put("appNumber", prodApp.getProdAppNo());

        return JasperFillManager.fillReport(resource.getFile(), param);
    }

    public String createRejectCert(ProdApplications prodApp) {
        this.prodApp = prodApp;
        this.product = prodApp.getProduct();
        try {
            //            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED); 
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_invoice", ".pdf");
            JasperPrint jasperPrint = initRejCert();
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            prodApp.setRejCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
            prodApplicationsDAO.updateApplication(prodApp);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates. 
            return "error";
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates. 
            // return "error"; 
        }
        return "created";
    }

    public String createRegCert(ProdApplications prodApp) {
        this.prodApp = prodApp;
        this.product = prodApp.getProduct();

        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_invoice", ".pdf");
            JasperPrint jasperPrint = initRegCert();
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            prodApp.setRegCert(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
            prodApplicationsDAO.updateApplication(prodApp);

        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        }

        return "created";
    }

    public String generateAppNo(ProdApplications prodApplications) {
        RegistrationUtil registrationUtil = new RegistrationUtil();
        return registrationUtil.generateAppNo(prodApplications.getId());

    }

    public ProdApplications getProdApp() {
        return prodApp;
    }

    public void setProdApp(ProdApplications prodApp) {
        this.prodApp = prodApp;
    }

    public String removeForeignAppStatus(ForeignAppStatus foreignAppStatus) {
        foreignAppStatusDAO.delete(foreignAppStatus);
        return "removed";
    }

    public List<ProdApplications> findSavedApps(Long loggedInUserID) {
        return prodApplicationsDAO.findSavedProdApp(loggedInUserID);
    }

    public Long findApplicationCount() {
        return prodApplicationsDAO.findApplicationCount();

    }

    public RetObject saveForeignAppStatus(ForeignAppStatus selForeignAppStatus) {
        RetObject retObject = new RetObject();
        try {
            selForeignAppStatus.setCountry(countryDAO.find(selForeignAppStatus.getCountry().getId()));
            selForeignAppStatus = foreignAppStatusDAO.save(selForeignAppStatus);
            retObject.setObj(selForeignAppStatus);
            retObject.setMsg("persist");
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setObj(null);
            retObject.setMsg(ex.getMessage());
        }
        return retObject;
    }

    public List<ForeignAppStatus> findForeignAppStatus(Long prodAppID) {
        return foreignAppStatusDAO.findByProdApplications_Id(prodAppID);


    }

    public String saveProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        try {
            prodAppChecklistDAO.save(prodAppChecklists);
            return "persist";
        } catch (Exception ex) {
            return "error";
        }

    }

    public String submitExecSummary(ProdApplications prodApplications, User user, List<ReviewInfo> reviewInfos) {
        try {
            if (reviewInfos == null || prodApplications == null || user == null)
                return "empty";

            boolean complete = false;
            for (ReviewInfo reviewInfo : reviewInfos) {
                if (!reviewInfo.getReviewStatus().equals(ReviewStatus.ACCEPTED)) {
                    complete = false;
                    break;
                } else {
                    complete = true;
                }
            }

            if (complete) {
                saveApplication(prodApplications, user);
                return "persist";
            } else {
                return "state_error";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }

    }

    public List<RevDeficiency> findRevDefByRI(ReviewInfo reviewInfo) {
        return revDeficiencyDAO.findByReviewInfo_Id(reviewInfo.getId());
    }

    public List<ProdAppLetter> findAllLettersByProdApp(Long id) {
        return prodAppLetterDAO.findByProdApplications_Id(id);
    }

    public RetObject submitProdApp(ProdApplications prodApplications, Long loggedINUserID) {
        RetObject retObject;
        try {
            retObject = updateProdApp(prodApplications, loggedINUserID);
            this.prodApp = (ProdApplications) retObject.getObj();
            createAckLetter();
            return retObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject = new RetObject("error", ex.getMessage());
            return retObject;
        }
    }

    public String createAckLetter() {
        Product prod = prodApp.getProduct();
        try {
            File invoicePDF = File.createTempFile("" + prod.getProdName() + "_ack", ".pdf");

            JasperPrint jasperPrint;
            Session hibernateSession = entityManager.unwrap(Session.class);
            SessionImpl session = (SessionImpl) hibernateSession;
            Connection conn = session.connection();
            HashMap param = new HashMap();
            param.put("prodAppNo", prodApp.getProdAppNo());
            param.put("id", prodApp.getId());
            param.put("subject", "Product Registration for  " + prod.getProdName() + " recieved");
//                + letter.getSubject() + " " + product.getProdName() + " ");
//        param.put("body", body);
            param.put("body", "Thank you for applying to register " + prod.getProdName() + " manufactured by " + prodApp.getApplicant().getAppName()
                    + ". The application number is " + prodApp.getProdAppNo() + ". "
                    + "Please use this application number for any future correspondence.");
            param.put("manufName", prod.getManufName());
            param.put("subject", "Product application deficiency letter for  " + prod.getProdName());

            URL resource = getClass().getResource("/reports/letter.jasper");
            jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));


            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Acknowledgement Letter");
            attachment.setUploadedBy(prodApp.getCreatedBy());
            attachment.setComment("Automatically generated Letter");
            attachment.setContentType("application/pdf");
            attachment.setLetterType(LetterType.ACK_SUBMITTED);
            prodAppLetterDAO.save(attachment);
            return "persist";

//                prodApplicationsDAO.updateApplication(prodApp);

        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        }
    }

    public ProdAppLetter findAllLettersByProdAppAndType(ProdApplications prodApplications, LetterType ackSubmitted) {
        List<ProdAppLetter> prodAppLetters = prodAppLetterDAO.findByProdApplications_IdAndLetterType(prodApplications.getId(), ackSubmitted);
        if (prodAppLetters != null && prodAppLetters.size() > 0)
            return prodAppLetters.get(0);
        else
            return null;


    }
}
