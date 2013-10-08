package org.msh.pharmadex.util;

/**
 * Author: usrivastava
 */
public class JsfUtils {

    public static String getBrowserName(String userAgent) {

        if(userAgent.contains("MSIE")){
            return "Internet Explorer";
        }
        if(userAgent.contains("Firefox")){
            return "Firefox";
        }
        if(userAgent.contains("Chrome")){
            return "Chrome";
        }
        if(userAgent.contains("Opera")){
            return "Opera";
        }
        if(userAgent.contains("Safari")){
            return "Safari";
        }
        return "Unknown";
    }

}
