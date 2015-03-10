package com.packt.masterjbpm6.event;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class MessageTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "messageSimple.bpmn" };

	public MessageTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testMessages() {
		ProcessInstance processInstance = ksession
				.startProcess("messageSimple");
		super.assertTrue(super.performFirstTaskOnList("mario"));
		waitUserInput("wait for a few seconds before accepting work for Luigi");
		int luigitasks = super.getNumTasksOnList("luigi");
		// check the activity has been cancelled
		super.assertTrue(luigitasks == 0);
	}
}