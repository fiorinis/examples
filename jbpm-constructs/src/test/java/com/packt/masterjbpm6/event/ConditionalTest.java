package com.packt.masterjbpm6.event;

import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class ConditionalTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "conditional.bpmn" };

	public ConditionalTest() {
		super();
		setProcessResources(processResources);
	}

	@Test
	public void testConditional() {
		ksession.getWorkItemManager().registerWorkItemHandler("customtask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession.startProcess("conditional");

		Order order = new Order();
		order.setNote("urgent");
		order.setCost(110);
		ksession.insert(order);
		ksession.fireAllRules();

		// performFirstTaskOnList("luigi");

		waitUserInput();
	}
}