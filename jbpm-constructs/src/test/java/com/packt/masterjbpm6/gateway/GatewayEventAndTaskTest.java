package com.packt.masterjbpm6.gateway;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayEventAndTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_event_and_task.bpmn";
	public static final String processId = "gateway_event_and_task";

	public GatewayEventAndTaskTest() {
		super(PU_NAME);
		setProcessResources(processResource);
	}

	@Test
	public void testTimerExpired() {
		// Timer triggers after 15s
		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess(processId);
		// check task exists
		assertTrue(super.getFirstTaskOnList("luigi") != null);
		// wait
		waitUserInput("check the console for the 'end process' message then proceed");
		assertFalse(super.getFirstTaskOnList("luigi") != null);

	}
}