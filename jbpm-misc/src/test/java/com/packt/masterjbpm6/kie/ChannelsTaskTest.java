package com.packt.masterjbpm6.kie;

import org.junit.Test;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

/**
 * 
 * @author simo
 * 
 * 
 */
public class ChannelsTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "rule/rule.bpmn", };
	public static final String[] rules = new String[] { "rule/rule.drl", };

	public ChannelsTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);
	}

	@Test
	public void testRule() {
		StringBuffer orderdesc = new StringBuffer();
		ksession.setGlobal("newnote", orderdesc);
		Order order = new Order();
		order.setCost(200);
		order.setNote("");
		ksession.setGlobal("orderglobal", order);
		FactHandle handle = ksession.insert(order);

		ProcessInstance processInstance = ksession.startProcess("rule");
		waitUserInput("type something to get global var");
		ksession.fireAllRules();

		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		assertEquals("URGENT", ordermodified.getNote());

	}

	/**
	 * executes the Rule test registering a Channel to get notifications
	 */

	@Test
	public void testRuleWithChannel() {
		ksession.registerChannel("appChannel", new AppChannel());
		testRule();
	}

	private class AppChannel implements Channel {

		public void send(Object object) {
			System.out.println(" Mychannel:" + object);
		}
	}

}