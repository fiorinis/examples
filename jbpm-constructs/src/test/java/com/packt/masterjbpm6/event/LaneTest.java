package com.packt.masterjbpm6.event;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class LaneTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "htlane.bpmn" };

	public LaneTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testLane() {
		ProcessInstance processInstance = ksession.startProcess("htlane");
		super.assertTrue(super.performFirstTaskOnList("john"));
		waitUserInput("wait for a few seconds before completing John's work");
		int johnstask = super.getNumTasksOnList("john");
		super.assertTrue(johnstask == 0);

	}
}