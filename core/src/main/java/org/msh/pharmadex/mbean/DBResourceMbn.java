package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.AdminRoute;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.domain.ResourceMessage;
import org.msh.pharmadex.service.DosageFormService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ResourceBundleService;
import org.msh.pharmadex.util.RegistrationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

/**
 * Author: usrivastava
 */
@Component
public class DBResourceMbn implements Serializable {
    @Autowired
    ResourceBundleService resourceBundleService;
    @Autowired
    DosageFormService dosageFormService;
    @Autowired
    GlobalEntityLists globalEntityLists;
    private Logger logger = LoggerFactory.getLogger(DBResourceMbn.class);

    public void startInsert() {
        FacesContext context = FacesContext.getCurrentInstance();
        Locale currLocale = context.getViewRoot().getLocale();
        logger.error("" + currLocale);
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

        org.msh.pharmadex.domain.ResourceBundle rb = new org.msh.pharmadex.domain.ResourceBundle();
        List<ResourceMessage> resourceMessages = new ArrayList<ResourceMessage>();
        rb.setMessages(resourceMessages);
        rb.setLocale(bundle.getLocale().toString());
        rb.setBasename(bundle.toString());
        rb.setCreatedDate(new Date());
        rb.setUpdatedDate(new Date());

        Enumeration<String> lstKeys = bundle.getKeys();
        ResourceMessage rm;
        while (lstKeys.hasMoreElements()) {
            String key = lstKeys.nextElement();
            rm = new ResourceMessage();
            rm.setKey(key);
            rm.setValue(bundle.getString(key));
            rm.setBundle(rb);
            resourceMessages.add(rm);


            System.out.println("Key == " + key);
            System.out.println("Value == " + bundle.getString(key));

        }
        System.out.println("Count == " + resourceMessages.size());
        resourceBundleService.save(rb);

        context.addMessage(null, new FacesMessage("Successfully Inserted the properties file"));
    }


    public void insertLookup() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<DosageForm> dosageForms = globalEntityLists.getDosageForms();
        List<Country> countries = globalEntityLists.getCountries();
        List<AdminRoute> adminRoutes = globalEntityLists.getAdminRoutes();

        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");

        org.msh.pharmadex.domain.ResourceBundle rb = resourceBundleService.findResourceBundle(bundle.getLocale());
        List<ResourceMessage> resourceMessages = rb.getMessages();

        ResourceMessage rm;

        String key = "";
        String value = "";
        String delimiter = "_";
        for (DosageForm df : dosageForms) {
            key = df.getClass().getSimpleName() + delimiter + RegistrationUtil.formatString(df.getDosForm());
            value = df.getDosForm();
            setLookup(rb, resourceMessages, key, value);
        }

        for (Country df : countries) {
            key = df.getClass().getSimpleName() + delimiter + RegistrationUtil.formatString(df.getCountryName());
            value = df.getCountryName();
            setLookup(rb, resourceMessages, key, value);
        }

        for (AdminRoute df : adminRoutes) {
            key = df.getClass().getSimpleName() + delimiter + RegistrationUtil.formatString(df.getName());
            value = df.getName();
            setLookup(rb, resourceMessages, key, value);
        }


        System.out.println("ResourceMessages == " + resourceMessages.size());
        resourceBundleService.save(rb);

        context.addMessage(null, new FacesMessage("Successfully Inserted the resource file"));


    }

    private void setLookup(org.msh.pharmadex.domain.ResourceBundle rb, List<ResourceMessage> resourceMessages, String key, String value) {
        ResourceMessage rm;
        rm = new ResourceMessage();
        rm.setBundle(rb);
        rm.setKey(key);
        rm.setValue(value);
        resourceMessages.add(rm);
    }


    public ResourceBundleService getResourceBundleService() {
        return resourceBundleService;
    }

    public void setResourceBundleService(ResourceBundleService resourceBundleService) {
        this.resourceBundleService = resourceBundleService;
    }

    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }
}
