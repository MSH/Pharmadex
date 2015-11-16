package org.msh.pharmadex.mbean;

import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * Author: usrivastava
 */
@Component
public class ErrorBean implements Serializable {
    private static final String BR = "n";

    public String getStackTrace()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,Object> map = context.getExternalContext().getRequestMap();
        Throwable throwable = (Throwable) map.get("javax.servlet.error.exception");
        StringBuilder builder = new StringBuilder();
        if(throwable!=null){
            builder.append(throwable.getMessage()).append(BR);

            for (StackTraceElement element : throwable.getStackTrace())
            {
              builder.append(element).append(BR);
            }
        }else{
            builder.append(map.get("exceptionMessage"));

        }

        return builder.toString();
    }
}
