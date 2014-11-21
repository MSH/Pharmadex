package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: usrivastava
 */
@Component
public class PostSuccessfullAuthHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    //    @Autowired
    private UserService userService;

    private UserSession userSession;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        /*
         *  Add post authentication logic in the trackUseLogin method of userService;
        */
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        userService = (UserService) ctx.getBean("userService");

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        UserSession userSession
                = (UserSession) FacesContext.getCurrentInstance().getApplication()
                .getELResolver().getValue(elContext, null, "userSession");

        User user = userService.findUserByUsername(authentication.getName());
        userSession.registerLogin(user, request);


        super.onAuthenticationSuccess(request, response, authentication);
    }


    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
