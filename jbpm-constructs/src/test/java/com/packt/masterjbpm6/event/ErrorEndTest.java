package com.packt.masterjbpm6.event;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class ErrorEndTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "errorsubprocess.bpmn" };

	public ErrorEndTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testSubprocessStartError() {
		ProcessInstance processInstance = ksession
				.startProcess("errorsubprocess");
		waitUserInput("wait for a few seconds before checking tasks available to Luigi");
		int luigitasks = super.getNumTasksOnList("luigi");
		super.assertTrue(luigitasks == 1);
		super.assertTrue(super.performFirstTaskOnList("luigi"));
		waitUserInput("wait for a few seconds before checking for subprocess to complete");
//		int mariotasks = super.getNumTasksOnList("mario");
//		super.assertTrue(mariotasks == 1);
//		super.assertTrue(super.performFirstTaskOnList("mario"));
		super.assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
}