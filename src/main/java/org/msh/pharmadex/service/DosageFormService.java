package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.dao.iface.DosageFormDAO;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.DosageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class DosageFormService implements Serializable{

    private static final long serialVersionUID = -4657880430145288749L;

    @Autowired
    private DosageFormDAO dosageFormDAO;

    @Autowired
    private DosUomDAO dosUomDAO;

    @Transactional
    public List<DosageForm> findAllDosForm(){
        return (List<DosageForm>) dosageFormDAO.findAll();
    }

    @Transactional
    public List<DosUom> findAllDosUom(){
        return (List<DosUom>) dosUomDAO.findAll();
    }

    @Transactional
    public DosageForm findDosagedForm(Long id){
        return dosageFormDAO.findOne(id);
    }


}
