package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.PharmClassDAO;
import org.msh.pharmadex.domain.PharmClassif;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
@Scope("singleton")
public class PharmClassifService implements Serializable {

    private static final long serialVersionUID = -806760452220291483L;

    @Autowired
    private PharmClassDAO pharmClassDAO;

    private List<PharmClassif> pharmClassifList;

    @Transactional
    public List<PharmClassif> getPharmClassifList() {
        if (pharmClassifList == null)
            pharmClassifList = pharmClassDAO.findAll();
        return pharmClassifList;
    }

    @Transactional
    public PharmClassif findPharmClassifById(long id) {
        return pharmClassDAO.findOne(id);
    }
}
