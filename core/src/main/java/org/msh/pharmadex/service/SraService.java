package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.SraDAO;
import org.msh.pharmadex.domain.SRA;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SraService implements Serializable {

    @Autowired
    private SraDAO sraDAO;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    @Transactional
    public SRA findSRA(Long id) {
        return sraDAO.findOne(id);
    }

    public List<SRA> findAllSRAs() {
        return sraDAO.findAll();
    }

    public RetObject newSRA(SRA sra) {
        List<SRA> sras = sraDAO.findByCountry(sra.getCountry());
        if (sras != null && sras.size() > 0) {
            return new RetObject("exists", null);
        } else {
            return updateSRA(sra);
        }

    }

    public RetObject updateSRA(SRA sra) {
        SRA saveSra;
        try {
            saveSra = sraDAO.save(sra);
            globalEntityLists.setSras(null);
            return new RetObject("persist", saveSra);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RetObject("error");
        }
    }

    public String deleteSRA(SRA sra) {
        sraDAO.delete(sra);
        globalEntityLists.setSras(null);
        return "success";
    }

}
