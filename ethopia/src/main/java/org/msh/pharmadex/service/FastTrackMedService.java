package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.FastTrackMedDAO;
import org.msh.pharmadex.domain.FastTrackMed;
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
public class FastTrackMedService implements Serializable {

    @Autowired
    private FastTrackMedDAO fastTrackMedDAO;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    @Transactional
    public FastTrackMed findFastTrackMed(Long id) {
        return fastTrackMedDAO.findOne(id);
    }

    public List<FastTrackMed> findAll() {
        return fastTrackMedDAO.findAll();
    }

    public RetObject newFastTrackMed(FastTrackMed fastTrackMed) {
        List<FastTrackMed> fastTrackMeds = fastTrackMedDAO.findByGenMedContainingIgnoreCase(fastTrackMed.getGenMed());
        if (fastTrackMeds != null && fastTrackMeds.size() > 0) {
            return new RetObject("exists", null);
        } else {
            return updateFastTrackMed(fastTrackMed);
        }

    }

    public boolean genmedExists(String genMed) {
        List<FastTrackMed> fastTrackMeds = fastTrackMedDAO.findByGenMedContainingIgnoreCase(genMed);
        if (fastTrackMeds != null && fastTrackMeds.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public RetObject updateFastTrackMed(FastTrackMed fastTrackMed) {
        FastTrackMed saveFastTrackMed;
        try {
            saveFastTrackMed = fastTrackMedDAO.save(fastTrackMed);
            globalEntityLists.setSras(null);
            return new RetObject("persist", saveFastTrackMed);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RetObject("error");
        }
    }

    public String deleteFastTrack(FastTrackMed fastTrackMed) {
        fastTrackMedDAO.delete(fastTrackMed);
//        globalEntityLists.setSras(null);
        return "success";
    }

}
