package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.failure.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
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

//    @Autowired
//    private UserSession userSession;

    @Autowired
    ApplicantDAO applicantDAO;

    @Autowired
    ProductDAO productDAO;

    @Autowired
    AtcDAO atcDAO;

    @Autowired
    AppointmentDAO appointmentDAO;

    @Autowired
    ProdAppChecklistDAO prodAppChecklistDAO;

    @Autowired
    ChecklistDAO checklistDAO;

    private List<ProdApplications> prodApplications;

    @Autowired
    private DosageFormService dosageFormService;

    @Autowired
    private StatusUserDAO statusUserDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public ProdApplications findProdApplications(long id) {
        return prodApplicationsDAO.findProdApplications(id);
    }

    public List<ProdApplications> getApplications() {
        if (prodApplications == null)
            prodApplications = prodApplicationsDAO.allProdApplications();
        return prodApplications;
    }

    public void refresh() {
        prodApplications = null;
    }


    public List<ProdApplications> getSavedApplications(int userId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<RegState> regState = new ArrayList<RegState>();
        regState.add(RegState.SAVED);
        params.put("regState", regState);
        params.put("userId", userId);
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
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isModerator()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isReviewer()) {
            prodApplicationses = prodApplicationsDAO.findProdApplicationsByReviewer(userSession.getLoggedInUserObj().getUserId());
        } else if (userSession.isHead()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
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
            params.put("userId", userSession.getLoggedInUserObj().getUserId());
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        }
        return prodApplicationses;
    }

    @Transactional
    public String saveApplication(ProdApplications prodApplications, User loggedInUserObj) {
        String result;
        prodApplications.getProd().setCreatedBy(loggedInUserObj);
        prodApplications.setSubmitDate(new Date());
        prodApplications.getProd().setLicNo("licno");

        applicantDAO.updateApplicant(prodApplications.getProd().getApplicant());

        prodApplications.getProd().setDosForm(
                dosageFormService.findDosagedForm(prodApplications.getProd().getDosForm().getUid()));

        if (prodApplications.getProd().getId() != null)
            productDAO.updateProduct(prodApplications.getProd());
        else
            productDAO.saveProduct(prodApplications.getProd());

        if (prodApplications.getAppointment() != null)
            appointmentDAO.save(prodApplications.getAppointment());


        if (prodApplications.getId() != null)
            result = prodApplicationsDAO.updateApplication(prodApplications);
        else
            result = prodApplicationsDAO.saveApplication(prodApplications);
        return result;
    }

    @Transactional
    public String updateProdApp(ProdApplications prodApplications) {
        return prodApplicationsDAO.updateApplication(prodApplications);
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

    public List<Company> findCompanies(Long prodId) {
        return prodApplicationsDAO.findCompanies(prodId);
    }

    public StatusUser findStatusUser(Long prodAppId) {
        return statusUserDAO.findByProdApplications_Id(prodAppId);
    }

    public String saveProcessors(StatusUser module) {
        statusUserDAO.save(module);
        return "success";
    }


}
