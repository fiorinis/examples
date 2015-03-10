package com.packt.masterjbpm6.event;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class StartTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources_signals = new String[] { "start_signal.bpmn" };
	public static final String[] processResources_messages = new String[] {
			"start_message_catch.bpmn", "start_message_throw.bpmn" };

	public StartTest() {
		super();
		setProcessResources(processResources_messages);
		// setProcessResources(processResources_signals);
	}

	@Test
	public void testSignalStart() {
		sendSignal("signalstart", "signalPayload");
		super.waitUserInput();
		sendSignal("signalstart2", "signalPayload2");
		super.waitUserInput();
	}

	@Test
	public void testMessageStart() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processVar", "");
		sendSignal("Message-startmessage", "messagedata");
		sendSignal("Message-startmessage2", "messagedata2");
		super.waitUserInput();
	}

	@Test
	public void testProcessStartFromMessageThrow() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processVar", "startmessage");
		SendMessageTaskHandler messagehandler = new SendMessageTaskHandler();
		messagehandler.setKnowledgeRuntime(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
				messagehandler);
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				"start_message_throw", params);
		super.waitUserInput();
	}

}