package com.packt.masterjbpm6.event;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class BoundaryTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "boundary.bpmn" };

	public BoundaryTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testSignalStart() {
		sendSignal("signalstart", "signalPayload");
		super.waitUserInput();
	}

	@Test
	public void testBoundaryWithCancel() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reason", "");
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"boundary", params);
		int tasks = super.getNumTasksOnList("luigi");
		assertTrue(tasks == 1);
		super.waitUserInput("press to send the cancellation message, tasks="
				+ tasks);
		sendSignal("Message-messageCancelService", "cancelled by ADMIN");
		super.waitUserInput("press to check activity cancellation; ");
		tasks = super.getNumTasksOnList("luigi");
		System.out.println("tasks=" + tasks);
		assertTrue(tasks == 0);
		super.waitUserInput();
	}

}