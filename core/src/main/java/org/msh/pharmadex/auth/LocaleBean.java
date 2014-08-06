package org.msh.pharmadex.auth;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Component
@Scope("session")
public class LocaleBean implements Serializable{
    private Locale locale;

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public SelectItem[] getLocales() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        Iterator<Locale> supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (supportedLocales.hasNext()) {
            Locale locale = supportedLocales.next();
            items.add(new SelectItem(locale.toString(), locale.getDisplayName()));
        }
        return items.toArray(new SelectItem[] {});
    }

    public String getSelectedLocale() {
        return getLocale().toString();
    }

    public void setSelectedLocale() {
        setSelectedLocale(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("locale"));
        changeLanguage();
    }
    
   public void setSelectedLocale(String localeString) {
        Iterator<Locale> supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (supportedLocales.hasNext()) {
            Locale locale = supportedLocales.next();
            if (locale.toString().equals(localeString)) {
                this.locale = locale;
                break;
            }
        }
    }
    public void changeLanguage (){
        setSelectedLocale(getSelectedLocale());
        FacesContext.getCurrentInstance().getViewRoot().setLocale(getLocale());
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());

        ApplicationResourceBundle applicationBundle = ApplicationAssociate.getCurrentInstance().getResourceBundles().get("msgs");
        Field field = null;
        try {
            field = applicationBundle.getClass().getDeclaredField("resources");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        Map<Locale, ResourceBundle> resources = null;
        try {
            resources = (Map<Locale, ResourceBundle>) field.get(applicationBundle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        resources.clear();

//        ApplicationResourceBundle appBundle = ApplicationAssociate.getCurrentInstance().
//                getResourceBundles().get(DBResourceBundle.class.getName());
//        Map resources = getFieldValue(appBundle, "msgs");
        resources.clear();

    }
    @SuppressWarnings("unchecked")
    private static  Map getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (Map) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}