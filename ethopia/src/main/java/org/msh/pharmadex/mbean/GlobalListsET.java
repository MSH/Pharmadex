package org.msh.pharmadex.mbean;

import org.msh.pharmadex.dao.iface.SRADAO;
import org.msh.pharmadex.domain.SRA;
import org.msh.pharmadex.domain.enums.AgentType;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.Arrays;
import java.util.List;

/**
 * Created by utkarsh on 1/11/15.
 */
@ManagedBean
@ApplicationScoped
public class GlobalListsET {

    @ManagedProperty("#{SRADAO}")
    private SRADAO sRADAO;

    public List<AgentType> getAgentTypes() {
        return Arrays.asList(AgentType.values());
    }

    public List<SRA> sras;

    public List<SRA> getSras() {
        if (sras == null) {
            sras = sRADAO.findAll();
        }
        return sras;
    }

    public SRADAO getsRADAO() {
        return sRADAO;
    }

    public void setsRADAO(SRADAO sRADAO) {
        this.sRADAO = sRADAO;
    }
}
