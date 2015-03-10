package com.packt.masterjbpm6.event;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class ErrorTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "errorboundary.bpmn" };

	public ErrorTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testBoundaryErrors() {
		Map<String, Object> params = new HashMap<String, Object>();
		// "1" for a runtime exception; "2" for a FileNotFoundException
		String trigger = "2";
		params.put("triggerexceptionflag", trigger);
		ProcessInstance processInstance = ksession.startProcess(
				"errorboundary", params);
		super.assertTrue(super.performFirstTaskOnList("luigi"));
		waitUserInput("wait for a few seconds before completing the test");
		int luigitasks = super.getNumTasksOnList("luigi");
		// check the activity has been cancelled
		super.assertTrue(luigitasks == 0);
	}
}