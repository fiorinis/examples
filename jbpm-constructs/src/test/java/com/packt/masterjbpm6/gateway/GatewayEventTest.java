package com.packt.masterjbpm6.gateway;

import java.util.HashMap;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayEventTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_event.bpmn";
	public static final String processId = "gateway_event";

	public GatewayEventTest() {
		super();
		setProcessResources(processResource);
	}

	@Test
	public void testCustomerPhoneCallEvent() {

		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess(processId);
		String orderid = String.valueOf(System.currentTimeMillis());
		System.out.println("sending customerphonecall with orderid=" + orderid);
		super.sendSignal(pi.getId(), "Signal_1", orderid);

	}

	@Test
	public void testTimerExpired() {
		// Timer triggers after 15s
		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess(processId);
		String orderid = String.valueOf(System.currentTimeMillis());
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		super.sendSignal(pi.getId(), "customerCancelOrder", orderid);

	}

	@Test
	public void testDeliveredEvent() {

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", null);
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		String orderid = String.valueOf(System.currentTimeMillis());

		// note that when creating the process def from the eclipse BPMN editor
		// actual signal ID to use is the SignalID in the Signal property editor
		// and not the signal Name in Process Definition Panel (Signals List)
		super.sendSignal(pi.getId(), "Signal_3", orderid);

	}
}