package com.packt.masterjbpm6.activity;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;

import com.packt.masterjbpm6.handlers.PacktServiceTaskHandler;
import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class ServiceTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "servicetask.bpmn" };

	public ServiceTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testJavaServiceTask() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", new Order());
		WorkItemHandler serviceTaskHandler = new ServiceTaskHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				serviceTaskHandler);

		ProcessInstance processInstance = ksession.startProcess("servicetask",
				params);
		waitUserInput("type something to end and check for the console log");

	}

	@Test
	public void testJavaCustomServiceTask() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", new Order());
		WorkItemHandler serviceTaskHandler = new PacktServiceTaskHandler(null);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				serviceTaskHandler);
		ProcessInstance processInstance = ksession.startProcess("servicetask",
				params);
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