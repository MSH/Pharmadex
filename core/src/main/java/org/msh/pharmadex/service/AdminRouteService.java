package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.AdminRouteDAO;
import org.msh.pharmadex.domain.AdminRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class AdminRouteService implements Serializable {


    @Autowired
    private AdminRouteDAO adminRouteDAO;

    private List<AdminRoute> adminRoutes;

    public List<AdminRoute> getAdminRoutes() {
        return adminRouteDAO.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
    }
}
