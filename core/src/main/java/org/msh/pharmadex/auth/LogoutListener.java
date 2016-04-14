package org.msh.pharmadex.auth;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.el.ELContext;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * Created by utkarsh on 11/19/14.
 */
@Component
public class LogoutListener implements ApplicationListener<SessionDestroyedEvent> {


    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent) {
        {
//            ELContext elContext = FacesContext.getCurrentInstance().getELContext();

            List<SecurityContext> lstSecurityContext = sessionDestroyedEvent.getSecurityContexts();
            UserDetails ud = null;
            for (SecurityContext securityContext : lstSecurityContext) {
                ud = (UserDetails) securityContext.getAuthentication().getPrincipal();
                SecurityContextHolder.clearContext();
//                response.sendRedirect(request.getContextPath() + "/home.faces?faces-redirect=true");
            }
           // System.out.println("-------------- Username == " + ud.getUsername());
        }
    }
}
