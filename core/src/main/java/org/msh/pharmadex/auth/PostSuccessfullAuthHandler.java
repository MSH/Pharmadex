package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        /*
         *  Add post authentication logic in the trackUseLogin method of userService;
        */
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        UserSession userSession = (UserSession) ctx.getBean("userSession");
        userService = (UserService) ctx.getBean("userService");
        User user = userService.findUserByUsername(authentication.getName());
        userSession.registerLogin(user, request);


        super.onAuthenticationSuccess(request, response, authentication);
    }


}
