package com.packt.masterjbpm6.pizzatweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.pizza.model.Pizza;
import com.packt.masterjbpm6.pizzahandlers.PizzaTweetHandler;

public class PizzaTweetTest extends Assert {

	@Test
	public void newTweet() {
		KieServices services = KieServices.Factory.get();
		KieBase kbase = services.getKieClasspathContainer().getKieBase();
		KieSession session = kbase.newKieSession();
		session.getWorkItemManager().registerWorkItemHandler("pizzatweet",
				new PizzaTweetHandler(session));
		Map<String, Object> params = new HashMap<String, Object>();

		Order order = new Order();
		order.setNote("urgent");
		order.setCost(15);
		params.put("order", order);
		params.put("msg", "test message");
		List<String> tags = new ArrayList<String>();
		tags.add("pizza");
		tags.add("pizzerianapoli");
		for (Pizza pizza : order.getPizzas()) {
			tags.add(pizza.getType().getType().toString());
		}
		ProcessInstance instance = session.startProcess("pizzatweet.tweet",
				params);
		Assert.assertTrue(true);
	}
}
