package com.packt.masterjbpm6;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.TaskSummary;

import com.packt.masterjbpm6.pizza.model.Pizza;
import com.packt.masterjbpm6.pizza.model.PizzaType;
import com.packt.masterjbpm6.pizza.model.Types;
import com.packt.masterjbpm6.test.LaneUserCallback;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class DataObjectTest extends PacktJUnitBaseTestCase {

	public static final String[] processResources = new String[] { "data-object.bpmn" };

	public DataObjectTest() {
		super(PU_NAME);
		setProcessResources(processResources);

		setUsergroupcallback(new LaneUserCallback());
	}

	@Test
	public void testDataObject() {
		ProcessInstance processInstance = ksession.startProcess("data-object");

		Pizza pizza = new Pizza(PizzaType.getType(Types.MARGHERITA),
				"margherita");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("output1", pizza);
		assertTrue(super.performFirstTaskOnList("luigi", params));
		waitUserInput();
		TaskSummary tasksummary = super.getFirstTaskByNameOnList("usertask2",
				"luigi");
		taskService.start(tasksummary.getId(), "luigi");
		tasksummary = super.getFirstTaskByNameOnList("usertask2", "luigi");
		Task task = taskService.getTaskById(tasksummary.getId());
		TaskData taskdata = task.getTaskData();
		Map<String, Object> taskcontent = taskService.getTaskContent(task
				.getId());
		Pizza pizzainputtask2 = (Pizza) taskcontent.get("input1");
		assertEquals(pizza.getDesc(), pizzainputtask2.getDesc());
	}
}