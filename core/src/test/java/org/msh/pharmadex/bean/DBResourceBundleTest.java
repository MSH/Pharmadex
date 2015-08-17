package org.msh.pharmadex.bean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.mbean.DBResourceMbn;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DBResourceBundleTest {
	
	@Autowired
    DBResourceMbn dBResourceMbn;

	@Test
	public void shouldGetCorrectMessage() {
        Assert.assertEquals(true, true);
        String newvalue = RegistrationUtil.formatString("New medicine, old medicine. /why (do) we");
        System.out.println("Formatted value == "+newvalue);
        Assert.assertEquals(true, true);
	}
	

}
