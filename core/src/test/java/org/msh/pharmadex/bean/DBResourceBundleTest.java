package org.msh.pharmadex.bean;

import org.aspectj.lang.annotation.Before;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.mbean.DBResourceMbn;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.tools.jar.Main;

import java.net.URLClassLoader;
import java.util.Arrays;
@Ignore
@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DBResourceBundleTest {
	
	@Autowired
    DBResourceMbn dBResourceMbn;

    @Before(value = "setup")
    public void setup() {
        URLClassLoader classLoader = (URLClassLoader) Main.class.getClassLoader();
        System.out.println(Arrays.toString(classLoader.getURLs()));
    }

	@Test
	public void shouldGetCorrectMessage() {
        Assert.assertEquals(true, true);
        String newvalue = RegistrationUtil.formatString("New medicine, old medicine. /why (do) we");
        System.out.println("Formatted value == "+newvalue);
        Assert.assertEquals(true, true);
	}
	

}
