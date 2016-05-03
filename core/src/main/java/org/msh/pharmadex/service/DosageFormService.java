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

    public DosageForm findDosageFormByName(String name){
        if (dosageForms == null)
            dosageForms = (List<DosageForm>) dosageFormDAO.findAll();
        for (DosageForm c : dosageForms) {
            if (c.getDosForm().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }

    /**
     * 03.05.2016
     * значение mg вынесено на первое место-чаще всего используется
     */
    @Transactional
    public List<DosUom> findAllDosUom() {
        if (dosUoms == null){
            dosUoms = (List<DosUom>) dosUomDAO.findAll();
            if(dosUoms != null && dosUoms.size() > 0){
            	DosUom mg = null;
            	for(DosUom uom:dosUoms){
            		if(uom.getUom().equals("mg")){
            			mg = uom;
            			break;
            		}
            	}
            	if(mg != null){
            		int ind_mg = dosUoms.indexOf(mg);
            		if(ind_mg > 0){ // вдруг и так первый в списке
            			DosUom firstUom = dosUoms.get(0);
            			dosUoms.set(0, mg);
            			dosUoms.set(ind_mg, firstUom);
            		}
            	}
            }
        }
        return dosUoms;
    }

    @Transactional
    /**
     * Reread list from DB, even dosUom exist (usually after updatr dictionary)
     */
    public List<DosUom> fetchAllDosUom() {
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

    public DosUom findDosUomByName(String name){
        if (name==null) return null;
        if ("".equals(name)) return null;
        List<DosUom> lst = findAllDosUom();
        if (lst==null) return null;
        if (lst.size()==0) return null;
        for (DosUom unit:lst){
            if (unit.getUom().equalsIgnoreCase(name)){
                return unit;
            }
        }
        return  null;
    }

    public DosUom saveDosUom(String name){
        DosUom unit = new DosUom();
        DosUom existUnit = findDosUomByName(name);
        unit.setUom(name);
        unit.setDiscontinued(false);
        if (existUnit!=null) return existUnit;
        unit = dosUomDAO.save(unit);
        return unit;
    }

}
