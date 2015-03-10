package com.packt.masterjbpm6.gateway;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayEventTest2 extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_event2.bpmn";
	public static final String processId = "gateway_event2";

	public GatewayEventTest2() {
		super();
		setProcessResources(processResource);
	}

	@Test
	public void testInstantiate() {

//		org.kie.api.runtime.process.ProcessInstance pi = ksession
//				.createProcessInstance(processId, null);
		String orderid = String.valueOf(System.currentTimeMillis());
		super.sendSignal("Signal_3", orderid);

		waitUserInput();
	}

}