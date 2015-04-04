package com.packt.masterjbpm6.ws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class WsTest extends PacktJUnitBaseTestCase {

	public static final String processResource = "ws-servicetask.bpmn";
	public static final String processId = "ws-servicetask";

	private static TestWebService service;
	private static Endpoint endpoint;

	public WsTest() {
		super();
		setProcessResources(processResource);
	}

	@BeforeClass
	public static void startWebService() throws Exception {
		service = new TestWebService();
		endpoint = Endpoint.publish(
				"http://127.0.0.1:9931/testwebservice/order", service);
	}

	@AfterClass
	public static void destroy() throws Exception {
		if (endpoint != null) {
			endpoint.stop();
		}
	}

	@Test
	public void testWSprocessSmallOrder() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Order order = new Order();
		order.setNote("just a small order");
		order.setCost(10);
		parameters.put("order", order);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				new ServiceTaskHandler(ksession));
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, parameters);
		super.waitUserInput();
	}

	@Test
	public void testWSprocessLargeOrder() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Order order = new Order();
		order.setNote("a large order! ");
		order.setCost(250);
		parameters.put("order", order);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				new ServiceTaskHandler(ksession));
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, parameters);
		super.waitUserInput();
	}

}
