package com.packt.masterjbpm6.gateway;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.packt.masterjbpm6.pizza.model.Ingredient;
import com.packt.masterjbpm6.pizza.model.Ingredient.IngredientType;
import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.pizza.model.Pizza;
import com.packt.masterjbpm6.pizza.model.PizzaType;
import com.packt.masterjbpm6.pizza.model.Types;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class GatewayParallelTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String processResource = "gateway_parallel.bpmn";
	public static final String processId = "gateway_parallel";

	public GatewayParallelTest() {
		super(PU_NAME);
		setProcessResources(processResource);
	}

	@Test
	public void testParallel() {
		Map<String, Object> params = new HashMap<String, Object>();
		Order order = new Order();
		order.getPizzas().add(
				new Pizza(PizzaType.getType(Types.MARGHERITA), null));
		order.getPizzas().add(new Pizza(PizzaType.getType(Types.SALAME), null));
		params.put("orderVar", order);
		org.kie.api.runtime.process.ProcessInstance pi = ksession.startProcess(
				processId, params);
		assertProcessVarExists(pi, "orderVar");
		Order processorder = (Order) super.getProcessVarFromInstance(pi,
				"orderVar");
		// then add extra mozzarella cheese
		processorder.getBom().addMaterial(
				Ingredient.getIngredient(IngredientType.MOZZARELLA), 2);
		// perform prepare material human task
		Map<String, Object> taskparams = new HashMap<String, Object>();
		taskparams.put("orderTask", processorder);
		assertTrue(super.performFirstTaskOnList("luigi", taskparams));

		double mozzarellaQty = order.getBom().getMaterialQuantity(
				Ingredient.IngredientType.MOZZARELLA.toString());
		assertEquals("wrong BOM", 4.0, mozzarellaQty, 0);
		assertNotNull("Route is not set !", processorder.getDelivery()
				.getRoute());
		System.out.println("route distance="
				+ processorder.getDelivery().getRoute().getDistance());
	}
}