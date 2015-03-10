package com.packt.masterjbpm6.handlers;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

public class MyReceiveTaskHandler implements WorkItemHandler {

	private Map<String, Long> waiting = new HashMap<String, Long>();
	private KieSession ksession;

	public MyReceiveTaskHandler(KieSession ksession) {
		this.ksession = ksession;
	}

	public void setKnowledgeRuntime(KieSession ksession) {
		this.ksession = ksession;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String messageId = (String) workItem.getParameter("MessageId");
		System.out.println("receive task registered for the " + messageId
				+ " message");
		waiting.put(messageId, workItem.getId());
	}

	public void messageReceived(String messageId, Object message) {
		Long workItemId = waiting.get(messageId);
		if (workItemId == null) {
			return;
		}
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("Message", message);
		ksession.getWorkItemManager().completeWorkItem(workItemId, results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		String messageId = (String) workItem.getParameter("MessageId");
		waiting.remove(messageId);
	}

}