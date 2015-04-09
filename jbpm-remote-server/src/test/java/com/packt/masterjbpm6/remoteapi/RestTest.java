package com.packt.masterjbpm6.remoteapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRestRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteRestRuntimeEngineFactory;
import org.kie.remote.client.api.exception.RemoteApiException;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestTest extends Assert {

	public static URL instanceurl;
	public static String deploymentId = "com.packt.masterjbpm6:pizzadelivery:1.0";
	public static String user = "admin";
	public static String password = "admin";
	public static String processID = "com.packt.masteringjbpm6.pizzadelivery";

	private RemoteRuntimeEngine engine;

	private static final Logger logger = LoggerFactory
			.getLogger(RestTest.class);

	@BeforeClass
	public static void setup() {

		try {
			instanceurl = new URL("http://localhost:8080/jbpm-console/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Before
	public void initRemoteRuntimeEngineF() {

		RemoteRestRuntimeEngineBuilder restEngineBuilder = RemoteRuntimeEngineFactory
				.newRestBuilder().addDeploymentId(deploymentId)
				.addUrl(instanceurl).addUserName(user).addPassword(password);

		RemoteRestRuntimeEngineFactory engineFactory = restEngineBuilder
				.buildFactory();
		assertNotNull(engineFactory);
		engine = engineFactory.newRuntimeEngine();
		assertNotNull(engine);
	}

	@Test
	public void startProcess() {

		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();

		try {
			ProcessInstance processInstance = ksession.startProcess(processID);

			long procId = processInstance.getId();
			String taskUserId = "nino";
			taskService = engine.getTaskService();
			List<TaskSummary> tasks = taskService
					.getTasksAssignedAsPotentialOwner(taskUserId, "en-UK");
			int tasksfound = tasks != null ? tasks.size() : 0;
			logger.info("found " + tasksfound + " task(s) for " + taskUserId);
			TaskSummary selectedtask = null;
			for (TaskSummary task : tasks) {
				if (task.getProcessInstanceId() == procId) {
					selectedtask = task;
					break;
				}
			}
			assertNotNull("no available task found for " + taskUserId,
					selectedtask);
			assertEquals(selectedtask.getName(), "Handle Incoming Order");
			taskService.start(selectedtask.getId(), taskUserId);
			Task startedtask = taskService.getTaskById(selectedtask.getId());
			taskService.complete(startedtask.getId(), taskUserId, null);

			tasks = taskService.getTasksAssignedAsPotentialOwner(taskUserId,
					"en-UK");
			int newtasksfound = tasks != null ? tasks.size() : 0;
			logger.info("found " + newtasksfound + " task(s) for " + taskUserId
					+ " after completing task");
			assertEquals(newtasksfound, tasksfound - 1);

		} catch (RemoteApiException remoteException) {
			remoteException.printStackTrace();
		}

	}

}
