package org.msh.pharmadex.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class UserAuthHandler {

    @Autowired
    UserSession userSession;


    public String logout()throws IOException, ServletException{
        userSession.registerLogout();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                 .getRequestDispatcher("/j_spring_security_logout");

        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());

        FacesContext.getCurrentInstance().responseComplete();
        // It's OK to return null here because Faces is just going to exit.

        return "/home.faces";
    }

    // This is the action method called when the user clicks the "login" button
    public String doLogin() throws IOException, ServletException
    {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                     .getRequestDispatcher("/j_spring_security_check");

            dispatcher.forward((ServletRequest) context.getRequest(),
                    (ServletResponse) context.getResponse());

            FacesContext.getCurrentInstance().responseComplete();
        // It's OK to return null here because Faces is just going to exit.
        return null;
    }
}

