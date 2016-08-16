package org.msh.pharmadex.mbean;

import org.msh.pharmadex.util.Scrooge;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created by Одиссей on 13.08.2016.
 */
@ManagedBean
@ViewScoped

public class BackLog implements Serializable {
    FacesContext context = FacesContext.getCurrentInstance();
    private static String backTo;

    private static String decode(String[] parts){
        if (parts.length==1) {
            return parts[0];
        }else {
            Long id=null;
            if (parts[0]!=null)
                id = Long.parseLong(parts[0]);
            if (id!=null)
                Scrooge.setBeanParam("Id",id);
            return parts[1];
        }
    }

    public static String goToBack(){
        if (backTo==null) return "";
        if ("".equals(backTo)) return "";
        String[] parts = backTo.split(";");
        int sz = parts.length;
        String[] pgParts = null;
        if (sz==1){
            pgParts = parts[0].split(":");
            backTo = null;
        }else{
            backTo = "";
            String part;
            for (int i=0;i<sz-1;i++){
                part = parts[i];
                backTo = ("".equals(backTo))?part:backTo+";"+part;
            }
            pgParts = parts[sz].split(":");
        }
        if (backTo!=null) //remember back to for previous page
            Scrooge.setStrBeanParam("backTo",backTo);
        if (pgParts.length>0){
           return decode(pgParts);
        }else {
            return "";
        }
    }

    public static String getBackTo() {
        return backTo;
    }

    public static void setBackTo(String backTo) {
        BackLog.backTo = backTo;
    }
}
