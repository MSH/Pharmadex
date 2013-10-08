package org.msh.pharmadex.bean;

import org.msh.pharmadex.model.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskHomeTest {
	
	@Autowired
	private TaskHome taskHome;
		
	@Test
	public void shouldGetCorrectMessage() {			
		String message = taskHome.getMessage();
		Assert.assertEquals("Hello from Spring",message);
	}
	
	@Test
	public void shouldSaveTaskAndResetTaskInstanceOnBean() {		
		Task oldTask = taskHome.getTask();
		taskHome.getTask().setDescription("Sample Description");
		taskHome.saveTask();
		Assert.assertNotNull("Saved task ID is null,probably not saved",oldTask.getId());
		Assert.assertNull("Task has not been reset",taskHome.getTask().getDescription());
		Assert.assertNull("Task has not been reset",taskHome.getTask().getId());
		Assert.assertNotSame("Task object has not been replaced",oldTask, taskHome.getTask());
	}
}
