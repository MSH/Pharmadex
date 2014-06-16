package org.msh.pharmadex.bean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msh.pharmadex.mbean.product.RegHomeMbean;
import org.msh.pharmadex.model.Task;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;

@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RegHomeMbeanTest {

    @Test
    public void generateAppNoTest(){
        RegistrationUtil registrationUtil = new RegistrationUtil();
        Assert.assertEquals(true, true);
        System.out.println("egistrationUtil.generateAppNo(new Long(10) == "+registrationUtil.generateAppNo(new Long(10)));
        Assert.assertNotNull(registrationUtil.generateAppNo(new Long(10)));
    }
	
}
