package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.ResourceBundleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.*;
import java.util.ResourceBundle;

/**
 * Created by usrivastava on 07/16/2014.
 */
@Component
public class DBControl extends ResourceBundle.Control {
    private Logger logger = LoggerFactory.getLogger(DBControl.class);


    @Override
    public List getFormats(String baseName) {
        if (baseName == null) {
            throw new NullPointerException();
        }
        return Arrays.asList("db");
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                    ClassLoader loader, boolean reload) throws IllegalAccessException,
            InstantiationException, IOException {
        if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
            throw new NullPointerException();
        }
        ResourceBundle bundle = null;
        if (format.equals("db")) {
            bundle = new CustomResourceBundle(locale);
        }
        return bundle;
    }

    /**
     * Our own implementation of a resource bundle inspired on the
     *  ListResourceBundle class with a change so that getting a non existing key
     * does not result in a MissingResourceException being thrown but, instead,
     * returning the passed in key.
     */
    protected class CustomResourceBundle extends ListResourceBundle {

        private Locale locale;

        /**
         * ResourceBundle constructor with locale
         *
         * @param locale
         */
        public CustomResourceBundle(final Locale locale) {
            this.locale = locale;
        }

        /**
         * Returns an array in which each item is a pair of objects in an Object array.
         * The first element of each pair is the key, which must be a String, and the
         * second element is the value associated with that key. See the class description
         * for details.
         *
         * @return an array of an Object array representing a key-value pair.
         */
        protected Object[][] getContents() {
            ResourceBundleService rbs;
            org.msh.pharmadex.domain.ResourceBundle bundle;
            try {
//                final ResourceBundleService resourceBundleService = LookupUtils.
//                        lookupWithinApp(ResourceBundleServiceLocal.BEAN_NAME,
//                                ResourceBundleServiceLocal.class.getName());

                rbs = (ResourceBundleService) ContextLoader.getCurrentWebApplicationContext().getBean("resourceBundleService");

                if(locale.getLanguage().equals("")){
                    locale = Locale.ENGLISH;
                }
                bundle = rbs.findResourceBundle(locale);
                if (bundle != null) {
                    final List resources = bundle.getMessages();
                    Object[][] all = new Object[resources.size()][2];
                    int i = 0;
                    for (Iterator it = resources.iterator(); it.hasNext();) {
                        ResourceMessage resource = (ResourceMessage) it.next();
                        all[i] = new Object[] { resource.getKey(), resource.getValue() };
                        i++;
                    }
                    return all;
                }
            } catch (NoResultException nre){
                logger.error("Resource bundle empty for {}", locale, nre);
                locale = Locale.ENGLISH;

            } catch (final Exception e) {
                logger.error("Problems initializing the db control for {}", locale, e);
                locale = Locale.ENGLISH;
            }
            return new Object[][] {};
        }
    }
}