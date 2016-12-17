package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.ChecklistDAO;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class ChecklistService implements Serializable {

    @Autowired
    private ChecklistDAO checklistDAO;

    private List<Checklist> checklists;

    /**
     * Method to populate the checklist based on the application type selected on step 2 of the wizard.
     * @parm prodApplications - application
     * @param header      whether to fetch the entire checklist or just header information
     * @return list of Checklist object.
     */
    public List<Checklist> getChecklists(ProdApplications prodApplications, boolean header) {
        if (prodApplications == null)
            return null;

        if(prodApplications.getProdAppType().equals(ProdAppType.GENERIC)){
        	checklists = checklistDAO.findByGenMedOrderByOrdAsc(true);
        }
        if(prodApplications.getProdAppType().equals(ProdAppType.RECOGNIZED)){
        	checklists = checklistDAO.findByRecognizedMedOrderByOrdAsc(true);
        }
        
        if(prodApplications.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)){
        	checklists = checklistDAO.findByNewMedOrderByOrdAsc(true);
        }

        return checklists;
    }

    /**
     * 
     * @param prodApplications
     * @param addParam kind of variation (minor/major) - only to compatibility!
     * @return
     */
    public List<Checklist> getETChecklists(ProdApplications prodApplications, boolean addParam) {
        return getChecklists(prodApplications, false);
    }

	public List<Checklist> findAll() {
		return checklistDAO.findAllByOrderByOrdAsc();
	
	}
    
	public Checklist updateList(Checklist l){
	return checklistDAO.save(l);
	}

}
