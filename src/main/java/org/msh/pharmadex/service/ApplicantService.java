package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ApplicantService implements Serializable {

    @Resource
    ApplicantDAO applicantDAO;

    private List<Applicant> applicants;

    @Transactional
    public Applicant findApplicant(long id) {
        return applicantDAO.findApplicant(id);
    }


    @Transactional
    public List<Applicant> findAllApplicants() {
        return applicantDAO.findAllApplicants();
    }

    @Transactional
    public List<Applicant> getRegApplicants() {
        applicants = applicantDAO.findRegApplicants();
        return applicants;
    }

    @Transactional
    public List<Applicant> getPendingApplicants() {
        System.out.println("inside getPendingApplicants");
        return applicantDAO.findPendingApplicant();
    }

    @Transactional
    public boolean saveApp(Applicant applicant, User loggedInUserObj) {
        try {
            applicant.setState(ApplicantState.NEW_APPLICATION);
            applicantDAO.saveApplicant(applicant);
            System.out.println("applicant id = " + applicant.getApplcntId());
            loggedInUserObj.setApplicant(applicant);
//            userService.updateUser(user);
            applicants = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateApp(Applicant applicant, User user) {
        try {
            applicantDAO.updateApplicant(applicant);
            System.out.println("applicant id = " + applicant.getApplcntId());
            if (user != null) {
                user.setApplicant(applicant);
//                userService.updateUser(user);
            }
            applicants = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Transactional
    public List<Applicant> findApplicantByProduct(Long id) {
        System.out.println("inside applicant by product");
        return applicantDAO.findApplicantByProduct(id);
    }

}
