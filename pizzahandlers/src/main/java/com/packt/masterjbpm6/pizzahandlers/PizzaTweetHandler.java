package com.packt.masterjbpm6.pizzahandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.packt.masterjbpm6.pizza.model.Order;

public class PizzaTweetHandler implements WorkItemHandler {

	private KieSession ksession;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		System.out.println("PizzaTweetHandler.executeWorkItem");
		// empty implementation. just log and return results

		Order order = (Order) workItem.getParameter("tweetOrder");
		List<String> tags = (List<String>) workItem.getParameter("tweetTags");
		String msg = (String) workItem.getParameter("tweetMsg");
		System.out.println("PizzaTweetHandler.order="
				+ (order != null ? order.toString() : " NULL !"));

		// return result
		Map<String, Object> results = new HashMap<String, Object>();

		Map<String, Object> operationresults = new HashMap<String, Object>();
		operationresults.put("twitterCode", "200");
		results.put("details", operationresults);
		results.put("tweetOK", Boolean.TRUE);

		boolean result = true;
		manager.completeWorkItem(workItem.getId(), results);
		if (result) {
			ksession.signalEvent("tweetok", order);
		} else {
			ksession.signalEvent("tweetfail", order);
		}

	}

	public PizzaTweetHandler(KieSession session) {
		this.ksession = session;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		System.out.println("PizzaTweetHandler.abortWorkItem");

	}

}
