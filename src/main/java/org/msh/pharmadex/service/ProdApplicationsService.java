package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.AppointmentDAO;
import org.msh.pharmadex.dao.iface.AtcDAO;
import org.msh.pharmadex.dao.iface.ChecklistDAO;
import org.msh.pharmadex.dao.iface.ProdAppChecklistDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.failure.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
        return prodApplicationsDAO.findProdApplicationsByStateAndUser(RegState.SAVED, userId);
    }

    public List<ProdApplications> getSubmittedApplications(UserSession userSession) {
        if (userSession.isAdmin())
            prodApplicationsDAO.findSubmittedApp(userSession.getLoggedInUserObj().getUserId());
        if (userSession.getLoggedInUserObj().getType().equals(UserType.STAFF)) {
            if (userSession.isModerator()) {
                return prodApplicationsDAO.findProdApplicationsByState(RegState.NEW_APPL);
            } else {
                return prodApplicationsDAO.findProdApplicationsByReviewer(userSession.getLoggedInUserObj().getUserId());
            }

        } else if (userSession != null)
            return prodApplicationsDAO.findSubmittedApp(userSession.getLoggedInUserObj().getUserId());
        else
            return prodApplicationsDAO.findSubmittedApp();
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
}
