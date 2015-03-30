package com.packt.masterjbpm6.fluent;

import java.util.HashMap;
import java.util.List;

import org.jbpm.services.task.utils.TaskFluent;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

/**
 * 
 * @author simo
 * 
 */
public class FluentTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "fluentProcess.bpmn2" };

	private static final String PROCESS_ID = "fluentProcess";

	public FluentTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void addTaskToProcessThenAbortAndComplete() {
		TaskFluent ftask = configureTask();
		ProcessInstance processinstance = ksession.startProcess(PROCESS_ID);

		// method name misleading: this actually sets the deployment ID
		// (defaults to the runtime
		// manager name: default-singleton)
		ftask.setWorkItemId("default-singleton");
		ftask.setProcessId(PROCESS_ID);
		ftask.setProcessInstanceId(processinstance.getId());
		ftask.setProcessSessionId((int) ksession.getIdentifier());
		long taskid = taskService.addTask(ftask.getTask(),
				new HashMap<String, Object>());

		// abort the process
		try {
			ksession.abortProcessInstance(processinstance.getId());
		} catch (Exception exc) {
			// process should abort ...
			fail();
		}
		super.waitUserInput(String.format(
				"process %d aborted. type something to complete adhoc task",
				processinstance.getId()));
		super.performFirstTaskOnList("Luigi");
	}

	@Test
	public void addAndCompleteTaskAbortedProcess() {
		TaskFluent ftask = configureTask();
		ProcessInstance processinstance = ksession.startProcess(PROCESS_ID);

		// method name misleading: this actually sets the deployment ID
		// (defaults to the runtime
		// manager name: default-singleton)
		ftask.setWorkItemId("default-singleton");

		super.performFirstTaskOnList("Mario");
		// abort the process
		try {
			ksession.abortProcessInstance(processinstance.getId());
		} catch (Exception exc) {
			// since process already completed...
			assertTrue(exc instanceof IllegalArgumentException);
		}
		super.waitUserInput(String.format(
				"process %d aborted. type something to add task",
				processinstance.getId()));
		// attach the task to the completed process
		ftask.setProcessId(PROCESS_ID);
		ftask.setProcessInstanceId(processinstance.getId());
		ftask.setProcessSessionId(ksession.getId());
		long taskid = taskService.addTask(ftask.getTask(),
				new HashMap<String, Object>());
		super.waitUserInput("type something to complete the task");
		super.performFirstTaskOnList("Luigi");
	}

	@Test
	public void addAndCompleteTaskActiveProcess() {
		TaskFluent ftask = configureTask();
		ProcessInstance processinstance = ksession.startProcess(PROCESS_ID);
		ftask.setProcessId(PROCESS_ID);
		ftask.setProcessInstanceId(processinstance.getId());
		ftask.setProcessSessionId(ksession.getId());

		// method name misleading: this actually sets the deployment ID
		// (defaults to the runtime
		// manager name: default-singleton)
		ftask.setWorkItemId("default-singleton");

		super.waitUserInput("type something to add task");
		// attach the task to the completed process
		long taskid = taskService.addTask(ftask.getTask(),
				new HashMap<String, Object>());
		super.waitUserInput("type something to complete the task");
		super.performFirstTaskOnList("Luigi");

	}

	@Test
	public void addCompleteTaskNoProcess() {

		Task task = configureTask().getTask();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("param1", "param1value");
		long taskid = taskService.addTask(task, params);

		// check the task has been added
		Task newtask = super.taskService.getTaskById(taskid);
		super.assertNotNull(newtask);
		// check the task has been assigned
		TaskSummary luigitask = super.getFirstTaskOnList("Luigi");
		List<TaskSummary> admintasks = taskService
				.getTasksAssignedAsBusinessAdministrator("Administrator",
						"en-UK");
		assertNotNull(luigitask);
		assertEquals(luigitask, admintasks.get(0));
		super.performFirstTaskOnList("Luigi");

	}

	private TaskFluent configureTask() {
		TaskFluent fluent = new TaskFluent();
		fluent.setName("packt-adhocHT");
		fluent.addPotentialUser("Luigi").setAdminUser("Administrator");
		return fluent;
	}
}
