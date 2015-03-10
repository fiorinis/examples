package com.packt.masterjbpm6.activity;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class CallactivityTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] {
			"callactivity.bpmn", "callactivitySub1.bpmn",
			"callactivityabort.bpmn", "callactivitysubprocessabort.bpmn", };

	public CallactivityTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testIndependentSubprocess() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", new Order());
		ProcessInstance processInstance = ksession.startProcess(
				"callactivityprocess", params);
		waitUserInput("type something to end ");

	}

	@Test
	public void testAbortProcess() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", new Order());
		ProcessInstance processInstance = ksession.startProcess(
				"callactivityabort", params);

		waitUserInput("type something to abort process instance ");
		ksession.abortProcessInstance(processInstance.getId());
		waitUserInput("type something to end");
	}

}