package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.domain.TimeLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class TimelineService implements Serializable {

    private static final long serialVersionUID = 7475406605652667214L;

    @Autowired
    TimelineDAO timelineDAO;

    List<TimeLine> timeLineList;

    public List<TimeLine> findTimelineByApp(Long prodApplications_Id){
        timeLineList = timelineDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        return timeLineList;
    }

    public TimeLine saveTimeLine(TimeLine timeLine){
        timeLineList = null;
        return timelineDAO.save(timeLine);
    }

}
