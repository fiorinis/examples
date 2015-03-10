package com.packt.masterjbpm6.activity;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.model.TaskSummary;

import com.packt.masterjbpm6.handlers.MyReceiveTaskHandler;
import com.packt.masterjbpm6.handlers.MySendTaskHandler;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class TaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] {
			"reassign.bpmn", "delegate_forward.bpmn", "send_receive.bpmn" };

	public TaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testSendReceive() {
		WorkItemHandler receiveTaskHandler = new MyReceiveTaskHandler(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
				receiveTaskHandler);
		WorkItemHandler sendTaskHandler = new MySendTaskHandler(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
				sendTaskHandler);
		ProcessInstance processInstance = ksession.startProcess("sendreceive");
		int count = super.getNumTasksOnList("luigi");
		int countadmin = super.getNumTasksAdminOnList();
		assertTrue(count == 1 && super.performFirstTaskOnList("luigi"));
		waitUserInput();
	}

	@Test
	public void testDelegateReadyStateAndSkip() {
		ProcessInstance processInstance = ksession.startProcess("delegate");
		int count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 1);
		TaskSummary task = getFirstTaskOnList("luigi");

		super.waitUserInput(" press to delegate to Mario");

		taskService.delegate(task.getId(), "luigi", "mario");
		int countmario = super.getNumTasksOnList("mario");
		int newcountluigi = super.getNumTasksOnList("luigi");
		super.assertTrue(newcountluigi == 1);
		super.assertTrue(countmario == 1);
		taskService.skip(task.getId(), "mario");
		int newcountmario = super.getNumTasksOnList("mario");
		super.assertTrue(newcountmario == 0);

	}

	@Test
	public void testForwardAndSkip() {
		ProcessInstance processInstance = ksession.startProcess("delegate");
		int count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 1);
		TaskSummary task = getFirstTaskOnList("luigi");
		super.waitUserInput(" press to forward to Mario");

		taskService.forward(task.getId(), "luigi", "mario");
		int countmario = super.getNumTasksOnList("mario");
		int newcountluigi = super.getNumTasksOnList("luigi");
		super.assertTrue(newcountluigi == 0);
		super.assertTrue(countmario == 1);
		taskService.skip(task.getId(), "mario");
		int newcountmario = super.getNumTasksOnList("mario");
		super.assertTrue(newcountmario == 0);
	}

	@Test
	public void testSuspendAndResume() {
		ProcessInstance processInstance = ksession.startProcess("delegate");
		int count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 1);
		TaskSummary task = getFirstTaskOnList("luigi");
		super.waitUserInput(" press to suspend");

		taskService.suspend(task.getId(), "luigi");
		count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 0);
		super.waitUserInput(" press to resume");
		taskService.resume(task.getId(), "luigi");
		count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 1);
	}

	@Test
	public void testReassign() {
		ProcessInstance processInstance = ksession.startProcess("reassign");
		int count = super.getNumTasksOnList("luigi");
		super.assertTrue(count == 1);

		super.waitUserInput("wait at least 10 seconds before proceeding... ");
		int countmario = super.getNumTasksOnList("mario");
		int newcountluigi = super.getNumTasksOnList("luigi");
		super.assertTrue(newcountluigi == 0);
		super.assertTrue(countmario == 1);

	}
}