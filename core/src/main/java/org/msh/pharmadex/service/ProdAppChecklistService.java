package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.ProdAppChecklistDAO;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class ProdAppChecklistService implements Serializable {

	@Autowired
	private ProdAppChecklistDAO prodAppChecklistDAO;


	public List<ProdAppChecklist> findProdAppChecklistByProdApp(Long prodAppID) {
		return prodAppChecklistDAO.findByProdApplications_IdOrderByIdAsc(prodAppID);
	}

	public RetObject saveProdAppChecklist(ProdAppChecklist prodAppChecklist) {
		RetObject retObj = new RetObject();
		try {
			ProdAppChecklist chklist = prodAppChecklistDAO.saveAndFlush(prodAppChecklist);
			retObj.setObj(prodAppChecklistDAO.findByProdApplications_IdOrderByIdAsc(chklist.getProdApplications().getId()));
			retObj.setMsg("persist");
		} catch (Exception ex) {
			retObj.setMsg("error");
			retObj.setObj(null);
		}
		return retObj;
	}

	public RetObject saveProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
		RetObject retObj = new RetObject();
		try {
			List<ProdAppChecklist> chklist = prodAppChecklistDAO.save(prodAppChecklists);
			retObj.setObj(chklist);
			retObj.setMsg("persist");
		} catch (Exception ex) {
			retObj.setMsg("error");
			retObj.setObj(null);
		}
		return retObj;
	}
	/**
	 * Strict check screener's responses. All should be defined
	 * @param prodAppChecklists
	 * @return
	 */
	public boolean checkStrict(List<ProdAppChecklist> prodAppChecklists) {
		if(prodAppChecklists != null){
			for(ProdAppChecklist item : prodAppChecklists){
				if(item.getChecklist().isHeader()){
					if(item.getStaffValue() != item.getValue()){
						if(item.getStaffValue() != null){
							if(item.getStaffValue() != item.getValue()){
								return item.getStaffValue() != YesNoNA.NO;
							}
						}else{
							return false;
						}
					}
				}				
			}
			return true;
		}else{
			return false;
		}
	}
}
