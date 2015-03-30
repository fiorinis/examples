package com.packt.masterjbpm6.activity;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.executor.api.ExecutorService;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class AsyncTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "asynctaskprocess.bpmn" };

	public AsyncTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testAsyncTask() {
		ExecutorService exservice = ExecutorServiceFactory.newExecutorService();
		exservice.init();
		exservice.setThreadPoolSize(1);
		exservice.setInterval(3);
		exservice.clearAllRequests();
		exservice.clearAllErrors();

		AsyncWorkItemHandler asyncHandler = new AsyncWorkItemHandler(exservice);
		ksession.getWorkItemManager().registerWorkItemHandler("async",
				asyncHandler);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "paramValue");

		ProcessInstance processInstance = ksession.startProcess(
				"asynctaskprocess", params);
		waitUserInput("type something to end ");
	}

}