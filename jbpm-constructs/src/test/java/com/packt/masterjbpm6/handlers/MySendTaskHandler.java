package com.packt.masterjbpm6.handlers;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySendTaskHandler implements WorkItemHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(MySendTaskHandler.class);
	private KieSession ksession;

	public MySendTaskHandler(KieSession ksession) {
		this.ksession = ksession;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String messagebody = (String) workItem.getParameter("messagebody");
		logger.debug("Sending message: {}", messagebody);
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("Message", messagebody);
		results.put("MessageId", "themessage");
		ksession.signalEvent("theMessage", messagebody);
		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

	}

}
