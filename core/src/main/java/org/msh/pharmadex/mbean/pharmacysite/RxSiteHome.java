package org.msh.pharmadex.mbean.pharmacysite;

import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Component
@Scope("session")
public class RxSiteHome implements Serializable {

    @Autowired
    private UserService userService;

    private PharmacySite site;

    private User user;

    public User getUser() {
        user = site.getUsers().get(0);
        user = userService.findUser(user.getUserId());
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
}
