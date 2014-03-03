package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.InnDAO;
import org.msh.pharmadex.dao.iface.ProdInnDAO;
import org.msh.pharmadex.domain.Inn;
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
@Scope("singleton")
public class InnService implements Serializable {

    private static final long serialVersionUID = -1166922531912144288L;

    @Autowired
    private InnDAO innDAO;

    @Autowired
    private ProdInnDAO prodInnDAO;

    private List<Inn> innList;

    public List<Inn> getInnList() {
        innList = (List<Inn>) innDAO.findAll();
        return innList;
    }

    public Inn findInnById(long id) {
        return innDAO.findOne(id);
    }

    public List<ProdInn> findInnByProdApp(Long id) {
        return prodInnDAO.findByProduct_Id(id);
    }
}
