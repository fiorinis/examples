package com.packt.masterjbpm6.event;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class EscalationTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "escalationtask.bpmn" };

	public EscalationTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testEscalationTask() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "0");
		ProcessInstance processInstance = ksession.startProcess(
				"escalationtask", params);

		super.assertTrue(super.performFirstTaskOnList("luigi"));
		waitUserInput("wait for a few seconds before completing Maria's task");
		int counttaskmaria = super.getNumTasksOnList("maria");
		int counttaskluigi = super.getNumTasksOnList("luigi");
		super.assertTrue(counttaskluigi == 0);
		super.assertTrue(counttaskmaria > 0);

		super.assertTrue(super.performFirstTaskOnList("maria"));

		waitUserInput("wait for a few seconds before the process completes");

		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// Did escalation fire?
		assertProcessVarValue(processInstance, "x", "1");

	}
}