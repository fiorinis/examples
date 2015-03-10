package com.packt.masterjbpm6.event;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class BoundaryServiceTaskHandler implements WorkItemHandler {

	private KieSession ksession;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void setKnowledgeRuntime(KieSession ksession) {
		this.ksession = ksession;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		System.out.println ("aborted");

	}

}
