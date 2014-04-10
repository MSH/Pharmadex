package org.msh.pharmadex.auth;

import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDAO userDAO;

//    @Autowired
//    UserService userService;

    private User user;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        UserDetails userDetails = null;
//        User newUser = new User();
//       newUser.setName("Utkarsh Srivastava");
//        newUser.setPassword("test");
//        newUser.setUsername(username);
//        newUser.setEnabled(true);            9
//        newUser.setCompanyName("New Company");
//        newUser.setEmail("a@a.com");
//        newUser.setType(org.msh.pharmadex.domain.enums.UserType.NMRC);
//
//       userService.createUser(newUser);

        User userEntity = userDAO.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userDetails = new UserDetailsAdapter(userEntity);
        setUser(userEntity);
        return userDetails;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

