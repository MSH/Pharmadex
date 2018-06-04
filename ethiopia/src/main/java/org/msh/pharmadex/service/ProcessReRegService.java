package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.ReRegistrationDAO;
import org.msh.pharmadex.domain.processes.ReRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Одиссей on 26.06.2016.
 */
@Service
public class ProcessReRegService implements Serializable {
    @Autowired
    private ReRegistrationDAO reRegistrationDAO;

    public List<ReRegistration> getList(){
        List<ReRegistration> result = reRegistrationDAO.findAll();
        return result;
    }
}
