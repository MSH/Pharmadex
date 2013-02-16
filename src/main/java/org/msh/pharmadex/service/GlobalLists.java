package org.msh.pharmadex.service;

import org.msh.pharmadex.domain.enums.UserType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("singleton")
public class GlobalLists {

    private List<UserType> userTypes;

    public List<UserType> getUserTypes() {
        return Arrays.asList(UserType.values());
    }
}
