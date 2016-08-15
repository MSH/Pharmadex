package org.msh.pharmadex.mbean;

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
    private String backTo;

    public void add(){

    }

    public String goToBack(){
        if (backTo==null) return "";
        if ("".equals(backTo)) return "";
        String[] parts = backTo.split(";");
        int sz = parts.length;
        if (sz==1){

        }else{

        }
        return "";
    }
}
