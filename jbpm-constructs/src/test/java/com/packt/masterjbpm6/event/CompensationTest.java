package com.packt.masterjbpm6.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.context.exception.CompensationScope;
import org.junit.Test;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class CompensationTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] {
			"compensateorder.bpmn", "compensateendsubprocess.bpmn" };

	public CompensationTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testGlobalCompensationWithSignal() {

		Order order = new Order();
		order.setCost(35.0);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ordercount", new Integer(10));
		params.put("order", order);
		params.put("cancelOrder", "n");
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"compensateorder", params);
		int tasks = super.getNumTasksOnList("luigi");
		assertTrue(tasks == 1);
		super.waitUserInput("press to cancel order with signal; tasks=" + tasks);

		ksession.signalEvent("Compensation",
				CompensationScope.IMPLICIT_COMPENSATION_PREFIX
						+ "compensateorder", pi.getId());
		super.waitUserInput();
	}

	@Test
	public void testSpecificCompensationWithSignal() {

		Order order = new Order();
		order.setCost(35.0);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ordercount", new Integer(10));
		params.put("order", order);
		params.put("cancelOrder", "n");
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"compensateorder", params);
		int tasks = super.getNumTasksOnList("luigi");
		assertTrue(tasks == 1);
		super.waitUserInput("press to cancel order with signal; tasks=" + tasks);
		ksession.signalEvent("Compensation", "_2", pi.getId());
		super.waitUserInput();
	}

	@Test
	public void testSubprocessCompensationEndEvent() {

		Map<String, Object> params = new HashMap<String, Object>();
		ArrayList<Integer> valuelist = new ArrayList<Integer>();
		for (int t = 1; t <= 10; t++) {
			valuelist.add(t);
		}
		params.put("valuelist", valuelist);
		params.put("sum", 0);
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"compensationsubprocessend", params);
		assertProcessVarValue(pi, "sum", "0");
		
	}

	@Test
	public void testCompensationEvent() {

		Order order = new Order();
		order.setCost(35.0);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ordercount", new Integer(0));
		params.put("order", order);
		params.put("cancelOrder", "n");
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"compensateorder", params);
		int tasks = super.getNumTasksOnList("luigi");
		assertTrue(tasks == 1);
		super.waitUserInput("press to cancel order; tasks=" + tasks);
		HashMap<String, Object> paramtask = new HashMap<String, Object>();
		paramtask.put("cancelordertask", "y");
		super.performFirstTaskOnList("luigi", paramtask);
		super.waitUserInput("press to check activity compensation ");
		tasks = super.getNumTasksOnList("luigi");
		System.out.println("tasks=" + tasks);
		assertTrue(tasks == 0);
		assertProcessVarValue(pi, "ordercount", "0");
	}
}