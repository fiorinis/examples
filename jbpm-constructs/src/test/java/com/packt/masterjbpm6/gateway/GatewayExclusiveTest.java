package com.packt.masterjbpm6.gateway;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayExclusiveTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_exclusive.bpmn";
	public static final String processId = "gateway_exclusive";

	public GatewayExclusiveTest() {
		super();
		setProcessResources(processResource);
	}

	@Test
	public void testDefaultFlow() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getDelivery().setDelivered(false);
		params.put("orderVar", order);

		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);

		Order processorder = (Order) getProcessVarFromInstance(pi, "orderVar");
		assertEquals(0, processorder.getReport().getActionsCount());

	}

	@Test
	public void testSmoothFlow() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getDelivery().setDelivered(true);
		Calendar duedate = Calendar.getInstance();
		Calendar deliverydate = Calendar.getInstance();
		duedate.setTime(new Date());
		deliverydate.setTime(new Date());
		deliverydate.add(Calendar.MINUTE, -10);
		order.getDelivery().setDueDate(duedate.getTime());
		order.getDelivery().setDeliveryDate(deliverydate.getTime());
		params.put("orderVar", order);
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		Order processorder = (Order) getProcessVarFromInstance(pi, "orderVar");
		assertEquals(1, processorder.getReport().getActionsCount());
	}

	@Test
	public void testNotSmoothFlow() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getDelivery().setDelivered(true);
		Calendar duedate = Calendar.getInstance();
		Calendar deliverydate = Calendar.getInstance();
		duedate.setTime(new Date());
		deliverydate.setTime(new Date());
		deliverydate.add(Calendar.MINUTE, +10);
		order.getDelivery().setDueDate(duedate.getTime());
		order.getDelivery().setDeliveryDate(deliverydate.getTime());
		params.put("orderVar", order);

		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		Order processorder = (Order) getProcessVarFromInstance(pi, "orderVar");
		assertEquals(1, processorder.getReport().getActionsCount());
		assertEquals(1, processorder.getReport().getIssuesCount());
	}

}