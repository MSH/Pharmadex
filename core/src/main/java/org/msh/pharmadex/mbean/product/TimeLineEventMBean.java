package org.msh.pharmadex.mbean.product;


import org.primefaces.extensions.component.timeline.Timeline;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 11/12/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TimeLineEventMBean implements Serializable {
    private List<Timeline> timelines;

    private String eventStyle = "box";
    private String axisPosition = "top";
    private boolean showNavigation = true;

    public List<Timeline> getTimelines() {
        timelines = new ArrayList<Timeline>();
        Calendar cal = Calendar.getInstance();
//        Timeline timeline = new DefaultTimeLine("prh", "Primefaces Release History");
//        cal.set(2011, 4, 10);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.1", cal.getTime()));
//        cal.set(2012, 0, 23);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.2.0", cal.getTime()));
//        cal.set(2012, 3, 02);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces Extensions 0.3.0", cal.getTime()));
//        cal.set(2012, 3, 16);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.4.0", cal.getTime()));
//        cal.set(2012, 5, 10);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.5.0", cal.getTime()));
//        cal.set(2012, 5, 19);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.5.1", cal.getTime()));
//        cal.set(2012, 8, 26);
//        timeline.addEvent(new DefaultTimelineEvent("Primefaces-Extensions 0.6.0", cal.getTime()));
//        timelines.add(timeline);
        return timelines;
    }

    public String getEventStyle() {
        return eventStyle;
    }

    public void setEventStyle(String eventStyle) {
        this.eventStyle = eventStyle;
    }

    public String getAxisPosition() {
        return axisPosition;
    }

    public void setAxisPosition(String axisPosition) {
        this.axisPosition = axisPosition;
    }

    public boolean isShowNavigation() {
        return showNavigation;
    }

    public void setShowNavigation(boolean showNavigation) {
        this.showNavigation = showNavigation;
    }
}
