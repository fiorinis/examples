package com.packt.masterjbpm6.handlers;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.packt.masterjbpm6.pizza.model.Order;

public class PacktServiceTaskHandler implements WorkItemHandler {

	private KieSession session;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> results = new HashMap<String, Object>();
		Order order = (Order) workItem.getParameter("Parameter");
		order.setNote("processed by custom servicetask");
		results.put("results", order);
		manager.completeWorkItem(workItem.getId(), results);
	}

	public PacktServiceTaskHandler(KieSession session) {
		this.session = session;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		manager.abortWorkItem(workItem.getId());

	}

}
