package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.ApplicantTypeDAO;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.service.converter.ApplicantConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
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

    @Autowired
    UserService userService;

    @Autowired
    ApplicantTypeDAO applicantTypeDAO;

    @Autowired
    ProductDAO productDAO;

    @Autowired
    RoleDAO roleDAO;

    @Autowired
    GlobalEntityLists globalEntityLists;

    @Autowired
    ApplicantConverter applicantConverter;

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
    public Applicant saveApp(Applicant applicant, User loggedInUserObj) {
        try {
            applicant.setState(ApplicantState.NEW_APPLICATION);
            if (applicant.getUsers() == null) {
                applicant.setUsers(new ArrayList<User>());
                applicant.getUsers().add(loggedInUserObj);
            }

            for (User user : applicant.getUsers()) {
                if (user.getType().equals(UserType.COMPANY)) {
                    user.setApplicant(applicant);
                    applicant.setContactName(user.getName());
                }
                if (user.getRoles() == null || user.getRoles().size() < 1) {
                    List<Role> rList = new ArrayList<Role>();
                    Role r = roleDAO.findOne(1);
                    rList.add(r);
                    r = roleDAO.findOne(4);
                    rList.add(r);
                    user.setRoles(rList);
                }
            }
            Applicant a = applicantDAO.saveApplicant(applicant);
            System.out.println("applicant id = " + applicant.getApplcntId());
            globalEntityLists.setRegApplicants(null);
            applicantConverter.setApplicantList(null);
            applicants = null;
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public boolean updateApp(Applicant applicant, User user) {
        try {
            applicantDAO.updateApplicant(applicant);
            System.out.println("applicant id = " + applicant.getApplcntId());
            if (user != null) {
                user.setApplicant(applicant);
                userService.updateUser(user);
            }
            applicants = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Transactional
    public Applicant findApplicantByProduct(Long id) {
        return applicantDAO.findApplicantByProduct(id);
    }

    @Transactional
    public List<Product> findRegProductForApplicant(Long appID) {
        return productDAO.findRegProductByApp(appID);
    }


    public User getDefaultUser(Long applcntId) {
        return applicantDAO.findApplicantDefaultUser(applcntId);
    }

    @Transactional
    public List<ApplicantType> findAllApplicantTypes() {
        return applicantTypeDAO.findAll();
    }
}
