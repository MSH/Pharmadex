package org.msh.pharmadex.failure;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.UserAccess;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: usrivastava
 */
public interface UserSession {
    public void login();

    public String getLoggedInUser();

     public String editUser();

     public void setLoggedInUser(String loggedInUser);

     /**
      * Register the logout when the user session is finished by time-out
      */
     @Transactional
     public void logout();

     /**
      * Register the user login
      */
     public void registerLogin(User user, HttpServletRequest request) ;


     /**
      * Register the logout of the current user
      */
     public void registerLogout();

     public User getLoggedInUserObj();

     public UserAccess getUserAccess();

     public void setLoggedInUserObj(User loggedInUserObj);

    public boolean isAdmin();

    public boolean isCompany();

    public boolean isStaff();

    public boolean isGeneral();

    public boolean isInspector();
}
