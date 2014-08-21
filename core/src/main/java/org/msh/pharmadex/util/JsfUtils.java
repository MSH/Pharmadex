package org.msh.pharmadex.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
public class JsfUtils {

    public static String getBrowserName(String userAgent) {

        if (userAgent.contains("MSIE")) {
            return "Internet Explorer";
        }
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }
        if (userAgent.contains("Opera")) {
            return "Opera";
        }
        if (userAgent.contains("Safari")) {
            return "Safari";
        }
        return "Unknown";
    }

    public static <E> List<E> completeSuggestions(String query, List<E> x) {
        List<E> suggestions = new ArrayList<E>(x.size());
        if (query == null || query.equalsIgnoreCase(""))
            return x;

        for (E each : x) {
            if (each.toString().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(each);
        }
        return suggestions;
    }

    public static Date addDate(Date dt, int year){
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.YEAR, 2);
        return c.getTime();
    }


}
