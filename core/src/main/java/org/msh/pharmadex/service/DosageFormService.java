package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.dao.iface.DosageFormDAO;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.DosageForm;
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
public class DosageFormService implements Serializable {

    private static final long serialVersionUID = -4657880430145288749L;

    private List<DosageForm> dosageForms;
    private List<DosUom> dosUoms;

    @Autowired
    private DosageFormDAO dosageFormDAO;

    @Autowired
    private DosUomDAO dosUomDAO;

    @Transactional
    public List<DosageForm> findAllDosForm() {
        if (dosageForms == null)
            dosageForms = (List<DosageForm>) dosageFormDAO.findAll();
        return dosageForms;
    }

    @Transactional
    public List<DosUom> findAllDosUom() {
        if (dosUoms == null)
            dosUoms = (List<DosUom>) dosUomDAO.findAll();
        return dosUoms;
    }

    @Transactional
    public DosageForm findDosagedForm(Long id) {
        return dosageFormDAO.findOne(id);
    }


    public DosUom findDosUom(int id) {
        return dosUomDAO.findOne(id);

    }
}
