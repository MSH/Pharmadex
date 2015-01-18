package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.enums.AgentType;
import org.msh.pharmadex.domain.enums.ForeignAppStatusType;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Arrays;
import java.util.List;

/**
 * Created by utkarsh on 1/11/15.
 */
@ManagedBean
@ApplicationScoped

public class GlobalListsET {

    public List<AgentType> getAgentTypes() {
        return Arrays.asList(AgentType.values());
    }

}