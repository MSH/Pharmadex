package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.PharmClassifDAO;
import org.msh.pharmadex.domain.PharmClassif;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class PharmClassifService implements Serializable{

    private static final long serialVersionUID = -806760452220291483L;

    @Autowired
    PharmClassifDAO pharmClassifDAO;

    private List<PharmClassif> pharmClassifList;

    @Transactional
    public List<PharmClassif> getPharmClassifList() {
            pharmClassifList = pharmClassifDAO.allCountry();
        return pharmClassifList;
    }

    @Transactional
    public PharmClassif findPharmClassifById(long id){
        return pharmClassifDAO.find(id);
    }
}
