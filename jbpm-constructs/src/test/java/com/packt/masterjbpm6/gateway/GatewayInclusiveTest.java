package com.packt.masterjbpm6.gateway;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayInclusiveTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_inclusive.bpmn";
	public static final String processId = "gateway_inclusive";

	public GatewayInclusiveTest() {
		super(PU_NAME);
		setProcessResources(processResource);
	}

	@Test
	public void testIssues() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getDelivery().setDelivered(true);
		order.getDelivery().setRetries(1);

		Calendar duedate = Calendar.getInstance();
		Calendar deliverydate = Calendar.getInstance();
		duedate.setTime(new Date());
		deliverydate.setTime(new Date());
		deliverydate.add(Calendar.MINUTE, +30);
		order.getDelivery().setDueDate(duedate.getTime());
		order.getDelivery().setDeliveryDate(deliverydate.getTime());

		params.put("orderVar", order);

		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		assertProcessVarExists(pi, "orderVar");
		Order processordeer = (Order) getProcessVarFromInstance(pi, "orderVar");
		assertEquals(2, processordeer.getReport().getIssuesCount());
	}

	@Test
	public void testNoIssues() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getDelivery().setDelivered(true);
		Calendar duedate = Calendar.getInstance();
		Calendar deliverydate = Calendar.getInstance();
		duedate.setTime(new Date());
		deliverydate.setTime(new Date());
		deliverydate.add(Calendar.MINUTE, -5);
		order.getDelivery().setDueDate(duedate.getTime());
		order.getDelivery().setDeliveryDate(deliverydate.getTime());

		params.put("orderVar", order);

		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		assertProcessVarExists(pi, "orderVar");
		Order processordeer = (Order) getProcessVarFromInstance(pi, "orderVar");
		assertEquals(0, processordeer.getReport().getIssuesCount());
	}

}