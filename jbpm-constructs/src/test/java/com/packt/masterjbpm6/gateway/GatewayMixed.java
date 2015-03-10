package com.packt.masterjbpm6.gateway;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayMixed extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "tg.bpmn";
	public static final String processId = "tg";

	public GatewayMixed() {
		super();
		setProcessResources(processResource);
	}

	@Test
	public void testTimerExpired() {
		// Timer triggers after 15s
		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess(processId);
		waitUserInput();
	}
}