package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.AtcDAO;
import org.msh.pharmadex.domain.Atc;
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
public class AtcService implements Serializable {


    private static final long serialVersionUID = -1704390569748290869L;
    @Autowired
    private AtcDAO atcDAO;

    private List<Atc> atcList;

    @Transactional
    public List<Atc> getAtcList() {
        atcList = (List<Atc>) atcDAO.findAll();
        return atcList;
    }

    public Atc findAtcById(String id) {
        return atcDAO.findByAtcCode(id);
    }


    public List<Atc> findAtcByName(String innname) {
        return atcDAO.findByAtcName(innname);
    }


//    public Atc findAtcByProduct(Long id){
//        return atcDAO.findByAtcName(id);
//    }


}
