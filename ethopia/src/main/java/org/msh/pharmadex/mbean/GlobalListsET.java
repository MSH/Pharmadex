package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.SRA;
import org.msh.pharmadex.domain.enums.AgeGroup;
import org.msh.pharmadex.domain.enums.AgentType;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by utkarsh on 1/11/15.
 */
@ManagedBean
@ApplicationScoped
public class GlobalListsET {

    public List<SRA> sras;

    public List<AgentType> getAgentTypes() {
        List<AgentType> res = new ArrayList<AgentType>();
        res.add(AgentType.FIRST);
        res.add(AgentType.SECOND);
        return res;
        //return Arrays.asList(AgentType.values()); //only first and second, but save existing third agents
    }

    public List<AgeGroup> getAgeGroupes() {
        return Arrays.asList(AgeGroup.values());
    }

}
