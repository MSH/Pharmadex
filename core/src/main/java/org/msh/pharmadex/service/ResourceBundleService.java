package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ResourceBundleDAO;
import org.msh.pharmadex.dao.iface.AdminRouteDAO;
import org.msh.pharmadex.domain.AdminRoute;
import org.msh.pharmadex.domain.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Author: usrivastava
 */
@Service
public class ResourceBundleService implements Serializable {


    @Autowired
    ResourceBundleDAO resourceBundleDAO;

    public ResourceBundle findResourceBundle(Locale locale) {
        return resourceBundleDAO.findResourceBundleByLocale(locale);
    }

    public void save(ResourceBundle resourceBundle) {
        resourceBundleDAO.save(resourceBundle);
    }
}
