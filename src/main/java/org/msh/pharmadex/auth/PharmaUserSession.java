package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 11/4/12
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class PharmaUserSession {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
