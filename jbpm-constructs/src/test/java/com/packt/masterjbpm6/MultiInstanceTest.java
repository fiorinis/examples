package com.packt.masterjbpm6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.pizza.model.Pizza;
import com.packt.masterjbpm6.pizza.model.PizzaType;
import com.packt.masterjbpm6.pizza.model.Types;
import com.packt.masterjbpm6.test.LaneUserCallback;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class MultiInstanceTest extends PacktJUnitBaseTestCase {

	public static final String[] processResources = new String[] { "multiinstance.bpmn" };
	public static final String PROCESS_ID = "multiinstance";

	public MultiInstanceTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testMultiInstanceSubprocess() {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Pizza> myList = new ArrayList<Pizza>();
		myList.add(new Pizza(PizzaType.getType(Types.MARGHERITA), "margherita"));
		myList.add(new Pizza(PizzaType.getType(Types.NAPOLI), "assorreta!"));
		params.put("list", myList);
		ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
				params);

		waitUserInput();
	}
}