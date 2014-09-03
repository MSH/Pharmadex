package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.Role;
import org.msh.pharmadex.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/13/12
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class UserDetailsAdapter extends org.springframework.security.core.userdetails.User {
    //    private int id = 0;
    private Long id;

    public UserDetailsAdapter(User userEntity) {
        super(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(), true, true, true, toAuthorities(userEntity.getRoles()));
        if (userEntity.getUserId() != null)
            this.id = userEntity.getUserId();
    }

    private static Collection<GrantedAuthority> toAuthorities(List<Role> authorities) {
        Collection<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        if (authorities != null) {
            for (Role authority : authorities) {
                authorityList.add(new SimpleGrantedAuthority(authority.getRolename()));
            }
        }
//        authorityList.add(new GrantedAuthorityImpl("ROLE_USER"));
//        authorityList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        return authorityList;
    }

    public Long getId() {
        return id;
    }

}
