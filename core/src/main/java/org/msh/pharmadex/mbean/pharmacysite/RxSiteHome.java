package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.UserService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Author: usrivastava
 */
@ManagedBean
@SessionScoped
public class RxSiteHome implements Serializable {

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private PharmacySite site;

    private User user;

    public User getUser() {
        user = userService.findUsersBySite(site.getId()).get(0);
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PharmacySite getSite() {
        return site;
    }

    public void setSite(PharmacySite site) {
        this.site = site;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
