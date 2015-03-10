package com.packt.masterjbpm6.remoteapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.services.client.api.RemoteRestRuntimeEngineFactory;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.kie.services.client.api.command.exception.RemoteApiException;

public class RestTest extends Assert {

	public static URL instaceurl;
	public static String deploymentId = "com.packt.masterjbpm6:pizzadelivery:1.0";
	public static String user = "nino";
	public static String password = "nino";
	public static String processID = "com.packt.masteringjbpm6.pizzadelivery";

	@BeforeClass
	public static void setup() {

		try {
			instaceurl = new URL("http://localhost:8080/jbpm-console/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void startProcess() {

		RemoteRuntimeEngineFactory restSessionFactory = RemoteRestRuntimeEngineFactory
				.newBuilder().addDeploymentId(deploymentId).addUrl(instaceurl)
				.addUserName(user).addPassword(password).build();
		RemoteRuntimeEngine engine = restSessionFactory.newRuntimeEngine();
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();

		try {
			ProcessInstance processInstance = ksession.startProcess(processID);

			long procId = processInstance.getId();
			String taskUserId = user;
			taskService = engine.getTaskService();
			List<TaskSummary> tasks = taskService
					.getTasksAssignedAsPotentialOwner(user, "en-UK");

			TaskSummary selectedtask = null;
			for (TaskSummary task : tasks) {
				if (task.getProcessInstanceId() == procId) {
					selectedtask = task;

					break;
				}
			}
			assertNotNull(selectedtask);
			assertEquals(selectedtask.getName(), "Handle Incoming Order");
			taskService.start(selectedtask.getId(), taskUserId);

		} catch (RemoteApiException remoteException) {
			remoteException.printStackTrace();
		}

	}

}
