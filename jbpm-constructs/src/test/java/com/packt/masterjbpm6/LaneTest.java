package com.packt.masterjbpm6;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

import com.packt.masterjbpm6.test.LaneUserCallback;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class LaneTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "lane.bpmn" };

	public LaneTest() {
		super(PU_NAME);
		setProcessResources(processResources);

		setUsergroupcallback(new LaneUserCallback());
	}

	@Test
	public void testTaskLaneSameUser() {
		ProcessInstance processInstance = ksession.startProcess("lane");
		int countluigi = super.getNumTasksOnList("luigi");
		assertTrue(countluigi == 2);
		super.performByTaskNameOnList("task1", "luigi");
		// task2 is reserved
		countluigi = super.getNumTasksOnList("luigi");
		assertTrue(countluigi == 2);
		TaskSummary task2 = getFirstTaskByNameOnList("task2", "luigi");
		super.assertTaskStatus(task2.getId(), "luigi", Status.Reserved);

		waitUserInput();
	}
}