package org.msh.pharmadex.service;

import org.apache.commons.collections.IteratorUtils;
import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.dao.iface.PharmacySiteDAO;
import org.msh.pharmadex.dao.iface.RxSiteChecklistDAO;
import org.msh.pharmadex.dao.iface.SiteChecklistDAO;
import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.PharmacySiteChecklist;
import org.msh.pharmadex.domain.SiteChecklist;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class PharmacySiteService implements Serializable {

    @Autowired
    PharmacySiteDAO pharmacySiteDAO;

    @Autowired
    SiteChecklistDAO siteChecklistDAO;

    @Autowired
    RxSiteChecklistDAO rxSiteChecklistDAO;

    @Autowired
    UserDAO userDAO;

    public PharmacySite findPharmacySite(Long id) {
        return (PharmacySite) pharmacySiteDAO.findOne(id);
    }

    public List<PharmacySite> findAllPharmacySite(ApplicantState state) {
        if (state != null)
            return pharmacySiteDAO.findByState(state);
        else
            return IteratorUtils.toList(pharmacySiteDAO.findAll().iterator());
    }

    public List<SiteChecklist> findAllCheckList() {
        return IteratorUtils.toList(siteChecklistDAO.findAll().iterator());
    }


    @Transactional
    public boolean updateApp(PharmacySite rxSite, User user) {
        try {
            pharmacySiteDAO.save(rxSite);
            System.out.println("Site id = " + rxSite.getId());
            if (user != null) {
                user.getPharmacySites().add(rxSite);
//                userService.updateUser(user);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String saveSite(PharmacySite pharmacySite) {
        try {
            pharmacySiteDAO.save(pharmacySite);
            return "persisted";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    public List<PharmacySiteChecklist> findChecklistBySite(Long id) {
        return rxSiteChecklistDAO.findByPharmacySite_Id(id);
    }


    public List<PharmacySite> findPharmacySiteByStateUser(List<User> users, ApplicantState newApplication) {
        return pharmacySiteDAO.findByStateAndUsers(newApplication, users);
    }
}
