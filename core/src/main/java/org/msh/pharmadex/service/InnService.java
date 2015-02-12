package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.ExcipientDAO;
import org.msh.pharmadex.dao.iface.InnDAO;
import org.msh.pharmadex.dao.iface.ProdExcipientDAO;
import org.msh.pharmadex.dao.iface.ProdInnDAO;
import org.msh.pharmadex.domain.Excipient;
import org.msh.pharmadex.domain.Inn;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class InnService implements Serializable {

    private static final long serialVersionUID = -1166922531912144288L;

    @Autowired
    private InnDAO innDAO;

    @Autowired
    private ExcipientDAO excipientDAO;

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

    public Inn saveInn(Inn inn) {
        return innDAO.save(inn);
    }

    public Excipient saveExcipient(Excipient excipient) {
        return excipientDAO.save(excipient);
    }

    public Inn findInnById(long id) {
        return innDAO.findOne(id);
    }

    public List<ProdInn> findInnByProdApp(Long id) {
        return prodInnDAO.findByProduct_Id(id);
    }

    public List<ProdExcipient> findExcipientByProdApp(Long id) {
        return prodExcipientDAO.findByProduct_Id(id);
    }
}
