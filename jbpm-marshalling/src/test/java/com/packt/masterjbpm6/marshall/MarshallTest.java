package com.packt.masterjbpm6.marshall;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.junit.Test;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.CustomJPAPlaceholderResolverStrategy;
import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.pizza.model.entity.OrderEntity;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class MarshallTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "rule_marshall.bpmn", };
	public static final String[] rules = new String[] { "rule.drl", };
	public static String PROCESS_ID = "rule_marshall";

	public MarshallTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);
	}

	@Test
	public void testMarshallStrategy() {
		StringBuffer orderdesc = new StringBuffer();
		ksession.setGlobal("newnote", orderdesc);
		Order order = new Order();
		order.setCost(200);
		order.setNote("");
		ksession.setGlobal("orderglobal", order);
		ksession.insert(order);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processOrder", new Order());
		OrderEntity orderentity = new OrderEntity();
		orderentity.setAmount(20);
		orderentity.setDesc("First order");

		params.put("orderEntity", orderentity);

		ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
				params);

		waitUserInput("type something to perform the user task ");
		ksession.fireAllRules();
		super.performFirstTaskOnList("luigi");
		waitUserInput("type something to get the global var");

		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		System.out.println("global order var:" + ordermodified);
		assertEquals("URGENT", ordermodified.getNote());
		waitUserInput();
	}

	@Override
	public ObjectMarshallingStrategy[] haveStrategies(EntityManagerFactory emf) {
		return new ObjectMarshallingStrategy[] {
				new CustomJPAPlaceholderResolverStrategy(emf),
				// new LocalFileObjectMarshallingStrategy(),
				new SerializablePlaceholderResolverStrategy(
						ClassObjectMarshallingStrategyAcceptor.DEFAULT) };
	}
}
