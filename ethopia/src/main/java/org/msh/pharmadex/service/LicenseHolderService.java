package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.LicenseHolderDAO;
import org.msh.pharmadex.domain.LicenseHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class LicenseHolderService implements Serializable {

    @Autowired
    private LicenseHolderDAO licenseHolderDAO;

    public List<LicenseHolder> findAllLicenseHolder() {
        return licenseHolderDAO.findAll();
    }

    public LicenseHolder findLicHolder(String licHolderID) {
        if(licHolderID!=null)
            return licenseHolderDAO.findOne(Long.valueOf(licHolderID));
        else
            return null;
    }
}
