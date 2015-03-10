package com.packt.masterjbpm6.activity;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class ScriptTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "script.bpmn", };
	public static final String PROCESS_ID = "script";

	public ScriptTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testScriptTask() {

		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.setNote("new order");
		params.put("order", order);
		ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
				params);
		waitUserInput("type something...");
		Object ordervar = super.getProcessVarFromInstance(processInstance,
				"order");
		assertTrue(ordervar instanceof Order
				&& ((Order) ordervar).getNote().equals("order modified"));

	}

}