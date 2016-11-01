package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.List;

import org.msh.pharmadex.dao.ExcipientDAO;
import org.msh.pharmadex.dao.InnDAO;
import org.msh.pharmadex.dao.iface.ProdExcipientDAO;
import org.msh.pharmadex.dao.iface.ProdInnDAO;
import org.msh.pharmadex.domain.Excipient;
import org.msh.pharmadex.domain.Inn;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: usrivastava
 */
@Service
public class InnService implements Serializable {

    private static final long serialVersionUID = -1166922531912144288L;

    @Autowired
    InnDAO innDAO;

    @Autowired
    ExcipientDAO excipientDAO;

    @Autowired
    private ProdInnDAO prodInnDAO;

    @Autowired
    private ProdExcipientDAO prodExcipientDAO;

    private List<Inn> innList;

    public List<Inn> getInnList() {
       innList = (List<Inn>) innDAO.findAll();
       return innList;
    }

    public List<Excipient> getExcipients() {
        return excipientDAO.findAll();
    }

    public boolean isNameInnDuplicated(Inn inn){
    	return innDAO.isNameDuplicated(inn);
    }
    
    public Inn saveInn(Inn inn) {
        return innDAO.saveInn(inn);
    }
    
    public Inn updateInn(Inn inn) {
    	return innDAO.update(inn);
    }
    
    public Inn addInn(Inn inn) {
    	if(innDAO.isNameDuplicated(inn)){
    		inn = innDAO.findByName(inn.getName());
    	}else{
    		inn = innDAO.saveInn(inn);
    	}
        return inn;
    }

    public Excipient addExcipient(Excipient exc) {
    	if(excipientDAO.isNameDuplicated(exc)){
    		exc = excipientDAO.findByName(exc.getName());
    	}else{
    		exc = excipientDAO.saveExcipient(exc);
    	}
        return exc;
    }
    
    public String removeExcipient(ProdExcipient excipient) {
    	prodExcipientDAO.delete(excipient);
        return "removed";
    }

    public Inn findInnById(long id) {
        return innDAO.findInnById(id);
    }

    public List<ProdInn> findInnByProdApp(Long id) {
        return prodInnDAO.findByProduct_Id(id);
    }
    
    public String removeProdInn(ProdInn selectedInn) {
        prodInnDAO.delete(selectedInn);
        return "removed";
    }

    public List<ProdExcipient> findExcipientByProdApp(Long id) {
        return prodExcipientDAO.findByProduct_Id(id);
    }
}
