package org.msh.pharmadex.auth;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
}