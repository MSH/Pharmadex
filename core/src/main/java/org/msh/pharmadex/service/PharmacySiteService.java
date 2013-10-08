package org.msh.pharmadex.service;

import org.apache.commons.collections.IteratorUtils;
import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.dao.iface.PharmacySiteDAO;
import org.msh.pharmadex.dao.iface.SiteChecklistDAO;
import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.SiteChecklist;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class PharmacySiteService implements Serializable {

    private static final long serialVersionUID = 3764728012503965664L;

    @Autowired
    PharmacySiteDAO pharmacySiteDAO;

    @Autowired
    SiteChecklistDAO siteChecklistDAO;

    @Autowired
    UserDAO userDAO;

    public PharmacySite findPharmacySite (Long id){
        return (PharmacySite) pharmacySiteDAO.findOne(id);
    }

    public List<PharmacySite> findAllPharmacySite (ApplicantState state){
        if(state != null)
            return pharmacySiteDAO.findByState(state);
        else
            return IteratorUtils.toList(pharmacySiteDAO.findAll().iterator());
    }

    public List<SiteChecklist> findAllCheckList (){
        return IteratorUtils.toList(siteChecklistDAO.findAll().iterator());
    }

    @Transactional
    public String saveSite(PharmacySite pharmacySite){
        try{
            User user = pharmacySite.getUsers().get(0);
            user = userDAO.findUser(user.getUserId());
            List<User> users = new ArrayList<User>();
            users.add(user);
            pharmacySiteDAO.save(pharmacySite);
            return "persisted";
        }catch (Exception ex){
            ex.printStackTrace();
            return "error";
        }
    }
}
