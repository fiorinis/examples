package com.packt.masterjbpm6.activity;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class AdHocSubprocessTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "adhocsubprocess.bpmn" };

	public AdHocSubprocessTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testAdHocSubprocess() {
		try {
			ProcessInstance processInstance = ksession
					.startProcess("adhocsubprocess");
			ksession.fireAllRules();
			ksession.signalEvent("report1", null, processInstance.getId());
			ksession.signalEvent("report2", null, processInstance.getId());

			int luigicount = getNumTasksOnList("luigi");
			assertTrue(luigicount > 0);
			assertTrue(super.performFirstTaskOnList("luigi"));
		} catch (Exception exc) {

		}
		waitUserInput("type something to end ");
	}

}