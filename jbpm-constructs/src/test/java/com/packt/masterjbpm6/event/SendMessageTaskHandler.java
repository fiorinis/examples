package com.packt.masterjbpm6.event;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class SendMessageTaskHandler implements WorkItemHandler {

	private KieSession ksession;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String message = "startmessage";
		System.out.println(String.format("Sending message:%s", message));
		ksession.signalEvent("Message-" + message, "data");
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void setKnowledgeRuntime(KieSession ksession) {
		this.ksession = ksession;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

}
