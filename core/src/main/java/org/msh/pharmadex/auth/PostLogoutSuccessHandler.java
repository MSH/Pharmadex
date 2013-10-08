package org.msh.pharmadex.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: usrivastava
 */
@Component
public class PostLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

//        if(request.getSession(false)!=null)
//            request.getSession(false).invalidate();
//        SecurityContextHolder.clearContext();
//        response.sendRedirect(request.getContextPath()+"/home.faces?faces-redirect=true");
        super.onLogoutSuccess(request, response, authentication);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
